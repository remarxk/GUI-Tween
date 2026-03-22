package com.remarxk.guitween.client;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.launch.MappingConfiguration;
import net.fabricmc.loader.impl.lib.mappingio.tree.MappingTree;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class GUITweenDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(RemapClassProvider::new);
    }

    public static class RemapClassProvider implements DataProvider {

        private final FabricDataOutput dataOutput;

        public RemapClassProvider(FabricDataOutput dataOutput) {
            this.dataOutput = dataOutput;
        }

        @Override
        public CompletableFuture<?> run(DataWriter writer) {
            JsonObject langEntryJson = new JsonObject();

            //[official, intermediary, named]

            MappingConfiguration mappingConfig = FabricLauncherBase.getLauncher().getMappingConfiguration();
            for (MappingTree.ClassMapping classDef : mappingConfig.getMappings().getClasses()) {
                String className = classDef.getName("intermediary").replace('/', '.');
                String remapClassName = classDef.getName("named").replace('/', '.');
                if (remapClassName.startsWith("net.minecraft.client.gui.screen")) {
                    langEntryJson.addProperty(className, remapClassName);
                }
            }

            return DataProvider.writeToPath(writer, langEntryJson, getLangFilePath());
        }

        @Override
        public String getName() {
            return "GUITween RemapClassProvider";
        }

        protected Path getLangFilePath() {
            return dataOutput
                    .getResolver(DataOutput.OutputType.RESOURCE_PACK, "remap_class_name")
                    .resolveJson(Identifier.of(dataOutput.getModId(), "remap_class_name"));
        }
    }
}
