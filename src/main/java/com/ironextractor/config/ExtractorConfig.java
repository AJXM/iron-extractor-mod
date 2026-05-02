package com.ironextractor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ironextractor.IronExtractorMod;
import com.ironextractor.util.ExtractorTier;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExtractorConfig {

    private static ExtractorConfig INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 初級
    public float basicStoneRate = 0.30f;
    public float basicDirtRate = 0.15f;
    public int basicProcessTicks = 100;
    public float basicFuelMultiplier = 2.0f;

    // 中級
    public float advancedStoneRate = 0.50f;
    public float advancedDirtRate = 0.25f;
    public int advancedProcessTicks = 60;
    public float advancedFuelMultiplier = 2.5f;

    // 上級
    public float expertStoneRate = 0.70f;
    public float expertDirtRate = 0.40f;
    public int expertProcessTicks = 30;
    public float expertFuelMultiplier = 3.0f;

    public static ExtractorConfig getInstance() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("iron_extractor.json");
        if (Files.exists(configPath)) {
            try (Reader reader = new FileReader(configPath.toFile())) {
                INSTANCE = GSON.fromJson(reader, ExtractorConfig.class);
            } catch (IOException e) {
                IronExtractorMod.LOGGER.error("設定ファイルの読み込みに失敗しました。デフォルト値を使用します。", e);
                INSTANCE = new ExtractorConfig();
            }
        } else {
            INSTANCE = new ExtractorConfig();
            save();
        }
    }

    private static void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("iron_extractor.json");
        try (Writer writer = new FileWriter(configPath.toFile())) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            IronExtractorMod.LOGGER.error("設定ファイルの保存に失敗しました。", e);
        }
    }

    public float getStoneRate(ExtractorTier tier) {
        return switch (tier) {
            case BASIC -> basicStoneRate;
            case ADVANCED -> advancedStoneRate;
            case EXPERT -> expertStoneRate;
        };
    }

    public float getDirtRate(ExtractorTier tier) {
        return switch (tier) {
            case BASIC -> basicDirtRate;
            case ADVANCED -> advancedDirtRate;
            case EXPERT -> expertDirtRate;
        };
    }

    public int getProcessTicks(ExtractorTier tier) {
        return switch (tier) {
            case BASIC -> basicProcessTicks;
            case ADVANCED -> advancedProcessTicks;
            case EXPERT -> expertProcessTicks;
        };
    }

    public float getFuelMultiplier(ExtractorTier tier) {
        return switch (tier) {
            case BASIC -> basicFuelMultiplier;
            case ADVANCED -> advancedFuelMultiplier;
            case EXPERT -> expertFuelMultiplier;
        };
    }
}
