package com.ironextractor;

import com.ironextractor.blockentity.ExtractorBlockEntity;
import com.ironextractor.util.ExtractorTier;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityTypes {

    public static BlockEntityType<ExtractorBlockEntity> BASIC_EXTRACTOR;
    public static BlockEntityType<ExtractorBlockEntity> ADVANCED_EXTRACTOR;
    public static BlockEntityType<ExtractorBlockEntity> EXPERT_EXTRACTOR;

    public static void register() {
        BASIC_EXTRACTOR = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(IronExtractorMod.MOD_ID, "basic_extractor"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new ExtractorBlockEntity(pos, state, ExtractorTier.BASIC),
                        ModBlocks.BASIC_EXTRACTOR
                ).build()
        );
        ADVANCED_EXTRACTOR = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(IronExtractorMod.MOD_ID, "advanced_extractor"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new ExtractorBlockEntity(pos, state, ExtractorTier.ADVANCED),
                        ModBlocks.ADVANCED_EXTRACTOR
                ).build()
        );
        EXPERT_EXTRACTOR = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(IronExtractorMod.MOD_ID, "expert_extractor"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new ExtractorBlockEntity(pos, state, ExtractorTier.EXPERT),
                        ModBlocks.EXPERT_EXTRACTOR
                ).build()
        );
    }

    public static BlockEntityType<ExtractorBlockEntity> getType(ExtractorTier tier) {
        return switch (tier) {
            case BASIC -> BASIC_EXTRACTOR;
            case ADVANCED -> ADVANCED_EXTRACTOR;
            case EXPERT -> EXPERT_EXTRACTOR;
        };
    }
}
