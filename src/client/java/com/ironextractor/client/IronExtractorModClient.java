package com.ironextractor.client;

import com.ironextractor.ModScreenHandlers;
import com.ironextractor.client.screen.ExtractorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class IronExtractorModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModScreenHandlers.EXTRACTOR_SCREEN_HANDLER, ExtractorScreen::new);
    }
}
