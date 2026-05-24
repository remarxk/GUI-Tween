#!/usr/bin/env node
/**
 * Local test runner for bug_analyzer
 *
 * Usage:
 *   node .github/scripts/local_test.js                    # Run all test cases
 *   node .github/scripts/local_test.js bug-report-zh      # Run a specific test
 *   node .github/scripts/local_test.js --list             # List available tests
 *
 * Requires GLM_API_KEY environment variable set.
 * Run: $env:GLM_API_KEY="your-key"; node .github/scripts/local_test.js
 */

const fs = require("fs");
const path = require("path");

const { analyzeIssue } = require("./bug_analyzer.js");

const TEST_DATA_DIR = path.join(__dirname, "test-data");

function listTests() {
  const files = fs.readdirSync(TEST_DATA_DIR).filter((f) => f.endsWith(".json"));
  console.log("\nAvailable test cases:\n");
  for (const f of files) {
    const name = f.replace(".json", "");
    const data = JSON.parse(fs.readFileSync(path.join(TEST_DATA_DIR, f), "utf-8"));
    console.log(`  ${name}`);
    console.log(`    Title: ${data.title}`);
    console.log(`    Body preview: ${(data.body || "").slice(0, 80).replace(/\n/g, " ")}...`);
    console.log();
  }
}

async function runTest(testName) {
  const filePath = path.join(TEST_DATA_DIR, testName + ".json");
  if (!fs.existsSync(filePath)) {
    console.error(`[ERROR] Test case "${testName}" not found at ${filePath}`);
    console.error(`  Run with --list to see available tests`);
    return { name: testName, status: "error", message: "Test file not found" };
  }

  const issue = JSON.parse(fs.readFileSync(filePath, "utf-8"));
  const apiKey = process.env.GLM_API_KEY;

  if (!apiKey) {
    console.error("[ERROR] GLM_API_KEY environment variable not set");
    console.error("  PowerShell: $env:GLM_API_KEY=\"your-key\"");
    console.error("  CMD: set GLM_API_KEY=your-key");
    process.exit(1);
  }

  console.log("=".repeat(60));
  console.log(`Test: ${testName}`);
  console.log(`Title: ${issue.title}`);
  console.log("=".repeat(60));
  console.log();

  const startTime = Date.now();
  try {
    const result = await analyzeIssue(issue, apiKey, true);
    const elapsed = ((Date.now() - startTime) / 1000).toFixed(1);
    console.log(`\n[${elapsed}s] Result type: ${result.type}`);

    if (result.type === "bug") {
      // Verify key sections exist
      const checks = {
        "问题分析": result.response.includes("问题分析"),
        "可能原因": result.response.includes("可能原因"),
        "建议方案": result.response.includes("建议方案"),
        "需要更多信息": result.response.includes("需要更多信息"),
        "关闭提示": result.response.includes("如果问题已经解决"),
      };
      const passed = Object.values(checks).every(Boolean);
      console.log(`Checks: ${passed ? "PASS" : "FAIL"}`);
      for (const [key, ok] of Object.entries(checks)) {
        console.log(`  ${ok ? "✓" : "✗"} ${key}`);
      }
      return { name: testName, status: passed ? "pass" : "fail", details: checks };
    } else if (result.type === "not_bug") {
      console.log("Status: PASS (correctly identified as non-bug)");
      return { name: testName, status: "pass" };
    } else {
      console.log(`Status: ERROR - ${result.message}`);
      return { name: testName, status: "error", message: result.message };
    }
  } catch (e) {
    console.error(`\nTest threw exception: ${e.message}`);
    return { name: testName, status: "error", message: e.message };
  }
}

async function main() {
  const args = process.argv.slice(2);

  if (args.includes("--list") || args.includes("-l")) {
    listTests();
    return;
  }

  if (!process.env.GLM_API_KEY) {
    console.error("[ERROR] GLM_API_KEY is required");
    console.error("  $env:GLM_API_KEY=\"your-api-key\"");
    process.exit(1);
  }

  let testNames;
  if (args.length > 0 && !args[0].startsWith("-")) {
    testNames = args;
  } else {
    // Run all tests
    testNames = fs
      .readdirSync(TEST_DATA_DIR)
      .filter((f) => f.endsWith(".json"))
      .map((f) => f.replace(".json", ""));
  }

  console.log(`Running ${testNames.length} test(s)...\n`);

  const results = [];
  for (const name of testNames) {
    results.push(await runTest(name));
    console.log("\n");
  }

  // Summary
  console.log("=".repeat(60));
  console.log("TEST SUMMARY");
  console.log("=".repeat(60));
  let pass = 0,
    fail = 0,
    error = 0;
  for (const r of results) {
    const icon = r.status === "pass" ? "✓" : r.status === "fail" ? "✗" : "!";
    console.log(`  ${icon} ${r.name}: ${r.status.toUpperCase()}`);
    if (r.status === "pass") pass++;
    else if (r.status === "fail") fail++;
    else error++;
  }
  console.log(`\nPass: ${pass}  Fail: ${fail}  Error: ${error}`);
  if (fail > 0 || error > 0) process.exit(1);
}

main().catch((e) => {
  console.error(`[FATAL] ${e.message}`);
  process.exit(1);
});
