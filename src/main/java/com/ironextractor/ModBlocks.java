package com.ironextractor;

import com.ironextractor.block.ExtractorBlock;
import com.ironextractor.util.ExtractorTier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final Block BASIC_EXTRACTOR = new ExtractorBlock(
            ExtractorTier.BASIC,
            BlockBehaviour.Properties.of()
                    .setId(blockKey("basic_extractor"))
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(ExtractorBlock.LIT) ? 7 : 0)
    );
    public static final Block ADVANCED_EXTRACTOR = new ExtractorBlock(
            ExtractorTier.ADVANCED,
            BlockBehaviour.Properties.of()
                    .setId(blockKey("advanced_extractor"))
                    .strength(4.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(ExtractorBlock.LIT) ? 10 : 0)
    );
    public static final Block EXPERT_EXTRACTOR = new ExtractorBlock(
            ExtractorTier.EXPERT,
            BlockBehaviour.Properties.of()
                    .setId(blockKey("expert_extractor"))
                    .strength(5.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(ExtractorBlock.LIT) ? 13 : 0)
    );

    public static void register() {
        registerBlock("basic_extractor", BASIC_EXTRACTOR);
        registerBlock("advanced_extractor", ADVANCED_EXTRACTOR);
        registerBlock("expert_extractor", EXPERT_EXTRACTOR);
    }

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(IronExtractorMod.MOD_ID, name));
    }

    private static void registerBlock(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath(IronExtractorMod.MOD_ID, name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))));
    }
}
