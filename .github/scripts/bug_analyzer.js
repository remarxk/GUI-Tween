#!/usr/bin/env node
const https = require("https");
const http = require("http");
const fs = require("fs");
const { execSync } = require("child_process");

const TIMEOUT = 15000;
const MAX_BODY_LEN = 1000000;
const MAX_LINKS = 5;
const MAX_FETCH_BYTES = 500 * 1024;
const LOG_DOMAINS = [
  "mclo.gs", "pastebin.com", "paste.ee", "paste.gg",
  "gist.github.com", "hastebin.com", "hastebin.skyra.pw",
  "lpaste.net", "log.savu.li", "paste.rs", "rentry.co",
];

function resolveLogUrl(url) {
  // Convert mclo.gs view links to raw content API endpoint
  const mcloMatch = url.match(/^https?:\/\/(?:www\.)?mclo\.gs\/(.+)/);
  if (mcloMatch) return `https://api.mclo.gs/1/raw/${mcloMatch[1]}`;
  return url;
}

function fetchUrl(url) {
  return new Promise((resolve) => {
    const resolved = resolveLogUrl(url);
    const client = resolved.startsWith("https") ? https : http;
    console.error(`    Resolved URL: ${resolved}`);
    const req = client.get(
      resolved,
      {
        timeout: TIMEOUT,
        headers: {
          "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/125.0.0.0 Safari/537.36",
          "Accept": "text/plain, text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8",
          "Accept-Language": "en-US,en;q=0.9",
        },
      },
      (resp) => {
        console.error(`    HTTP ${resp.statusCode} ${resp.statusMessage}`);
        if (resp.statusCode >= 400) {
          resp.resume();
          return resolve(null);
        }
        const ct = resp.headers["content-type"] || "";
        if (!["text", "json", "xml", "plain", "html"].some((t) => ct.includes(t))) {
          console.error(`    Unsupported content-type: ${ct}`);
          resp.resume();
          return resolve(null);
        }
        let raw = Buffer.alloc(0);
        resp.on("data", (chunk) => {
          raw = Buffer.concat([raw, chunk]);
          if (raw.length > MAX_FETCH_BYTES) {
            resp.destroy();
            resolve(raw.toString("utf-8"));
          }
        });
        resp.on("end", () => {
          resolve(raw.toString("utf-8"));
        });
        resp.on("error", () => resolve(null));
      }
    );
    req.on("error", (e) => {
      console.error(`    Request error: ${e.message}`);
      resolve(null);
    });
    req.on("timeout", () => {
      console.error(`    Request timed out`);
      req.destroy();
      resolve(null);
    });
  });
}

function extractLogUrls(text) {
  const urls = [...text.matchAll(/https?:\/\/[^\s<>"']+/g)].map((m) => m[0]);
  return urls.filter((url) => {
    try {
      const hostname = new URL(url).hostname.replace(/^www\./, "");
      return LOG_DOMAINS.some((d) => hostname === d || hostname.endsWith("." + d));
    } catch {
      return false;
    }
  });
}

function buildPrompt(issueTitle, issueBody, linkSection) {
  let truncatedBody = issueBody.slice(0, MAX_BODY_LEN);
  if (issueBody.length > MAX_BODY_LEN) {
    truncatedBody += "\n\n[...内容过长，已截断...]";
  }

  const systemPrompt = [
    "你是一个Minecraft模组的技术支持专家。当前项目是GUITween模组（一个GUI动画库）。请分析用户提交的issue内容，判断是否是一个有效的bug报告。",
    "",
    "注意：禁止建议用户删除或卸载GUITween模组作为解决方案，因为当前项目本身就是GUITween，需要解决的是模组自身的问题。根据用户的发送内容语言使用相同的语言回复，默认用英文。",
    "",
    "判断标准：",
    "- 用户描述了一个具体的异常行为、崩溃、报错或不符合预期的功能表现",
    "- 用户提供了相关上下文信息（如日志、截图、复现步骤等）",
    "- 用户遇到了一个需要开发者修复的程序缺陷",
    "",
    "如果确认是bug报告：",
    "1. 详细分析可能的原因",
    "2. 提供可行的排查步骤或解决方案建议",
    "3. 如果描述不清晰（缺少MC版本、模组列表、完整日志、复现步骤等），引导用户补充这些信息",
    "4. 自动检测用户使用的语言，并用相同的语言回复（默认英文）",
    "",
     "回复格式（重要：所有章节标题和内容都必须使用用户的语言，不可固定为中文）：",
    "## [问题分析 / Problem Analysis]",
    "[对问题的简要分析和定位]",
    "",
    "## [环境信息 / Environment]",
    "- MC版本/MC Version: [用户提供的版本号，如果没有则写\"未提供/N/A\"]",
    "- 模组版本/Mod Version: [用户提供的模组版本，如果没有则写\"未提供/N/A\"]",
    "- 模组加载器/Mod Loader: [Forge/Fabric/NeoForge，根据用户提供的信息填写]",
    "- 其他模组/Other Mods: [列出用户提到的相关模组，如果没有则写\"未提供/N/A\"]",
    "- 崩溃日志/Crash Log: [如果有日志链接或内容，简要说明包含的关键错误信息]",
    "",
    "## [可能原因 / Possible Causes]",
    "- [原因1 / Cause 1]",
    "- [原因2 / Cause 2]",
    "",
    "## [建议方案 / Suggested Solutions]",
    "- [方案1 / Solution 1]",
    "- [方案2 / Solution 2]",
    "",
    "## [需要更多信息 / Additional Information Needed]",
    "- [列出需要用户补充的信息，如MC版本、模组列表、完整日志、复现步骤等]",
    "",
    "---",
    "**[如果问题已经解决，请关闭此issue。如果没有解决，请等待作者回复。/ If the issue has been resolved, please close it. If not, please wait for the author's reply.]**",
    "",
    "如果不是bug报告（如功能请求、讨论、问题咨询等），请只回复：NOT_A_BUG",
  ].join("\n");

  const userMessage = `## Issue 标题\n${issueTitle}\n\n## Issue 内容\n${truncatedBody}${linkSection}`;

  return [
    { role: "system", content: systemPrompt },
    { role: "user", content: userMessage },
  ];
}

function callGbmApi(apiKey, messages) {
  return new Promise((resolve, reject) => {
    const payload = JSON.stringify({
      // model: "glm-4.7-flash",
      model: "glm-4-plus",
      messages,
      stream: false,
      temperature: 1.0,
      max_tokens: 65536,
      thinking: { type: "enabled" },
    });

    const options = {
      hostname: "open.bigmodel.cn",
      path: "/api/paas/v4/chat/completions",
      method: "POST",
      timeout: 120000,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${apiKey}`,
        "Content-Length": Buffer.byteLength(payload),
      },
    };

    const req = https.request(options, (resp) => {
      let data = "";
      resp.on("data", (chunk) => (data += chunk));
      resp.on("end", () => {
        try {
          const json = JSON.parse(data);
          if (json.error) {
            reject(new Error(`API error [${json.error.code}]: ${json.error.message}`));
            return;
          }
          if (!json.choices || !json.choices[0] || !json.choices[0].message) {
            reject(new Error(`Unexpected API response structure: ${data.slice(0, 300)}`));
            return;
          }
          resolve(json.choices[0].message.content);
        } catch (e) {
          reject(new Error(`Parse error: ${e.message}, raw: ${data.slice(0, 200)}`));
        }
      });
    });

    req.on("error", (e) => reject(new Error(`Request failed: ${e.message}`)));
    req.on("timeout", () => {
      req.destroy();
      reject(new Error("Request timed out"));
    });

    req.write(payload);
    req.end();
  });
}

function ensureClosingLine(response) {
  if (!response.includes("如果问题已经解决") || !response.includes("请关闭此issue")) {
    response +=
      "\n\n---\n*如果问题已经解决，请关闭此issue。如果没有解决，请等待作者回复。*";
  }
  return response;
}

async function analyzeIssue(issue, apiKey, dryRun = false) {
  let issueBody = issue.body || "";
  const issueTitle = issue.title || "";
  const issueNumber = issue.number;

  // Extract crash-log URLs, fetch content, and remove URLs from body
  const urls = extractLogUrls(issueBody);
  if (urls.length > 0) {
    console.error(`Found ${urls.length} crash-log URL(s) to fetch`);
  }
  const logContents = [];
  const urlPatterns = [];
  for (let i = 0; i < Math.min(urls.length, MAX_LINKS); i++) {
    console.error(`  Fetching ${i + 1}/${Math.min(urls.length, MAX_LINKS)}: ${urls[i]}`);
    urlPatterns.push(urls[i].replace(/[.*+?^${}()|[\]\\]/g, "\\$&"));
    const content = await fetchUrl(urls[i]);
    if (content) {
      logContents.push(`[来自 ${urls[i]} 的日志内容]\n${content}`);
      console.error(`  -> Fetched ${content.length} chars`);
      console.log("=".repeat(60));
      console.log(`[FETCHED] ${urls[i]}`);
      console.log("=".repeat(60));
      console.log(content);
      console.log("=".repeat(60));
    } else {
      console.error("  -> Skipped (unreadable or empty)");
    }
  }

  // Remove log URLs from issue body so the AI only receives fetched content
  if (urlPatterns.length > 0) {
    const stripPattern = new RegExp(urlPatterns.join("|"), "g");
    issueBody = issueBody.replace(stripPattern, "[日志内容已提取到下方]");
  }

  const logSection =
    logContents.length > 0
      ? "\n\n用户提供的崩溃日志/错误信息：\n" + logContents.join("\n\n")
      : "";

  const messages = buildPrompt(issueTitle, issueBody, logSection);

  // Call AI
  console.error("Calling GLM-4 API for analysis...");
  let response;
  try {
    response = await callGbmApi(apiKey, messages);
  } catch (e) {
    console.error(`[ERROR] AI analysis failed: ${e.message}`);
    return { type: "error", message: e.message };
  }

  if (!response) {
    console.error("[ERROR] AI analysis returned empty response");
    return { type: "error", message: "Empty response from API" };
  }

  response = response.trim();

  if (response === "NOT_A_BUG") {
    console.error(`Issue #${issueNumber} is not a valid bug report. Skipping comment.`);
    return { type: "not_bug", issueNumber };
  }

  response = ensureClosingLine(response);

  if (dryRun) {
    console.log("\n=== AI RESPONSE (dry-run) ===\n");
    console.log(response);
    console.log("\n=== END ===\n");
    return { type: "bug", issueNumber, response };
  }

  // Post comment via GitHub API directly (more reliable than gh CLI)
  console.error(`Posting comment on issue #${issueNumber}...`);
  console.error(`  Response length: ${response.length} chars`);

  const repo = process.env.GITHUB_REPOSITORY || "";
  const token = process.env.GITHUB_TOKEN || "";
  if (!repo || !token) {
    console.error("[ERROR] GITHUB_REPOSITORY or GITHUB_TOKEN not set");
    return { type: "error", message: "Missing GITHUB_REPOSITORY or GITHUB_TOKEN" };
  }

  const [owner, repoName] = repo.split("/");
  const postData = JSON.stringify({ body: response });

  try {
    execSync(
      `gh api repos/${owner}/${repoName}/issues/${issueNumber}/comments --input - --method POST`,
      { input: postData, env: { ...process.env } }
    );
  } catch (e) {
    console.error(`[ERROR] Failed to post comment via gh api: ${e.message}`);
    // Fallback: try gh issue comment
    try {
      console.error("  Retrying with gh issue comment --body-file - ...");
      execSync("gh issue comment " + issueNumber + " --body-file -", {
        input: response,
        env: { ...process.env },
      });
    } catch (e2) {
      console.error(`[ERROR] Fallback also failed: ${e2.message}`);
      return { type: "error", message: e2.message };
    }
  }

  console.error(`[SUCCESS] Analysis comment posted on issue #${issueNumber}`);
  return { type: "bug", issueNumber, response };
}

async function main() {
  const isDryRun = process.argv.includes("--dry-run");
  const dryRunFile = isDryRun ? process.argv[process.argv.indexOf("--dry-run") + 1] : null;

  const apiKey = process.env.GLM_API_KEY;
  if (!apiKey) {
    console.error("[ERROR] GLM_API_KEY not set");
    process.exit(1);
  }

  let issue;
  if (isDryRun && dryRunFile) {
    issue = JSON.parse(fs.readFileSync(dryRunFile, "utf-8"));
    console.error(`[DRY-RUN] Testing with file: ${dryRunFile}`);
  } else {
    const eventPath = process.env.GITHUB_EVENT_PATH;
    if (!eventPath) {
      console.error("[ERROR] GITHUB_EVENT_PATH not set (use --dry-run <file> for local testing)");
      process.exit(1);
    }
    const event = JSON.parse(fs.readFileSync(eventPath, "utf-8"));
    issue = event.issue || {};
    if (!issue.number) {
      console.error("[ERROR] No issue number found");
      process.exit(1);
    }
  }

  console.error(`Analyzing issue: ${issue.title}`);
  const result = await analyzeIssue(issue, apiKey, isDryRun);

  if (result.type === "error") {
    process.exit(1);
  }
}

if (require.main === module) {
  main().catch((e) => {
    console.error(`[ERROR] Unhandled exception: ${e.message}`);
    process.exit(1);
  });
}

module.exports = { fetchUrl, extractLogUrls, buildPrompt, callGbmApi, ensureClosingLine, analyzeIssue };
