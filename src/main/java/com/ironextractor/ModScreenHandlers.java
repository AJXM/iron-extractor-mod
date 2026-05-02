package com.ironextractor;

import com.ironextractor.screen.ExtractorScreenHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlers {

    public static MenuType<ExtractorScreenHandler> EXTRACTOR_SCREEN_HANDLER;

    public static void register() {
        EXTRACTOR_SCREEN_HANDLER = Registry.register(
                BuiltInRegistries.MENU,
                Identifier.fromNamespaceAndPath(IronExtractorMod.MOD_ID, "extractor"),
                new MenuType<>(ExtractorScreenHandler::new, FeatureFlags.VANILLA_SET)
        );
    }
}
