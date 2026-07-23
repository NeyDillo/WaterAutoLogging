package com.NeyDillo.WaterAutoLogging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;
public class ConfigManager {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("WaterAutoLogging.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig loadConfig() {
        if (!CONFIG_PATH.toFile().exists()) {
            return new ModConfig();
        }
        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            return GSON.fromJson(reader, ModConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ModConfig();
        }
    }
    public static void saveConfig(ModConfig config) {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}