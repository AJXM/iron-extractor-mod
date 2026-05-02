package com.ironextractor;

import com.ironextractor.config.ExtractorConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IronExtractorMod implements ModInitializer {

    public static final String MOD_ID = "iron_extractor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModScreenHandlers.register();
        ExtractorConfig.load();
        LOGGER.info("Iron Extractor MOD が初期化されました。");
    }
}
