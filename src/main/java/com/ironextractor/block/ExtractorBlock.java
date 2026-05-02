package com.ironextractor.block;

import com.ironextractor.ModBlockEntityTypes;
import com.ironextractor.blockentity.ExtractorBlockEntity;
import com.ironextractor.util.ExtractorTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ExtractorBlock extends BaseEntityBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final MapCodec<ExtractorBlock> CODEC_BASIC =
            simpleCodec(props -> new ExtractorBlock(ExtractorTier.BASIC, props));
    public static final MapCodec<ExtractorBlock> CODEC_ADVANCED =
            simpleCodec(props -> new ExtractorBlock(ExtractorTier.ADVANCED, props));
    public static final MapCodec<ExtractorBlock> CODEC_EXPERT =
            simpleCodec(props -> new ExtractorBlock(ExtractorTier.EXPERT, props));

    private final ExtractorTier tier;

    public ExtractorBlock(ExtractorTier tier, BlockBehaviour.Properties properties) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return switch (tier) {
            case BASIC -> CODEC_BASIC;
            case ADVANCED -> CODEC_ADVANCED;
            case EXPERT -> CODEC_EXPERT;
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExtractorBlockEntity(pos, state, tier);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MenuProvider menuProvider) {
                player.openMenu(menuProvider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                   BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntityTypes.getType(tier), ExtractorBlockEntity::tick);
    }
}
