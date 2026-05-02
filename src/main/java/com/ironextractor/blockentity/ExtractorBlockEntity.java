package com.ironextractor.blockentity;

import com.ironextractor.ModBlockEntityTypes;
import com.ironextractor.block.ExtractorBlock;
import com.ironextractor.config.ExtractorConfig;
import com.ironextractor.screen.ExtractorScreenHandler;
import com.ironextractor.util.ExtractorTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Random;

public class ExtractorBlockEntity extends BlockEntity implements Container, MenuProvider {

    // スロット: 0=入力, 1=燃料, 2=出力
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    private final ExtractorTier tier;
    private int progress = 0;
    private int processTimer = 0;
    private int fuelTime = 0;
    private int maxFuelTime = 0;

    private static final Random RANDOM = new Random();

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> ExtractorBlockEntity.this.progress;
                case 1 -> ExtractorBlockEntity.this.processTimer;
                case 2 -> ExtractorBlockEntity.this.fuelTime;
                case 3 -> ExtractorBlockEntity.this.maxFuelTime;
                case 4 -> ExtractorConfig.getInstance().getProcessTicks(ExtractorBlockEntity.this.tier);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> ExtractorBlockEntity.this.progress = value;
                case 1 -> ExtractorBlockEntity.this.processTimer = value;
                case 2 -> ExtractorBlockEntity.this.fuelTime = value;
                case 3 -> ExtractorBlockEntity.this.maxFuelTime = value;
            }
        }

        @Override
        public int getCount() { return 5; }
    };

    public ExtractorBlockEntity(BlockPos pos, BlockState state, ExtractorTier tier) {
        super(ModBlockEntityTypes.getType(tier), pos, state);
        this.tier = tier;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ExtractorBlockEntity entity) {
        if (level.isClientSide()) return;

        ExtractorConfig config = ExtractorConfig.getInstance();
        int baseProcessTicks = config.getProcessTicks(entity.tier);
        float fuelMultiplier = config.getFuelMultiplier(entity.tier);

        if (entity.fuelTime <= 0) {
            ItemStack fuelStack = entity.inventory.get(1);
            int fuelValue = getFuelTime(fuelStack);
            if (fuelValue > 0) {
                entity.maxFuelTime = fuelValue;
                entity.fuelTime = fuelValue;
                fuelStack.shrink(1);
                BlockEntity.setChanged(level, pos, state);
            }
        }

        ItemStack inputStack = entity.inventory.get(0);
        boolean shouldBeLit = !inputStack.isEmpty() && isValidInput(inputStack);

        // LIT ブロックステートを更新（変化があるときのみ）
        if (shouldBeLit != state.getValue(ExtractorBlock.LIT)) {
            level.setBlock(pos, state.setValue(ExtractorBlock.LIT, shouldBeLit), 3);
        }

        if (!shouldBeLit) {
            entity.processTimer = 0;
            return;
        }

        boolean hasFuel = entity.fuelTime > 0;
        int tickAdvance = hasFuel ? (int) fuelMultiplier : 1;

        if (hasFuel) {
            entity.fuelTime = Math.max(0, entity.fuelTime - 1);
        }

        entity.processTimer += tickAdvance;

        if (entity.processTimer >= baseProcessTicks) {
            entity.processTimer = 0;

            float rate = isStone(inputStack)
                    ? config.getStoneRate(entity.tier)
                    : config.getDirtRate(entity.tier);

            if (RANDOM.nextFloat() < rate) {
                entity.progress++;

                if (entity.progress >= 100) {
                    ItemStack outputStack = entity.inventory.get(2);
                    if (outputStack.isEmpty()) {
                        entity.inventory.set(2, new ItemStack(Items.IRON_INGOT));
                        entity.progress = 0;
                    } else if (outputStack.is(Items.IRON_INGOT)
                            && outputStack.getCount() < outputStack.getMaxStackSize()) {
                        outputStack.grow(1);
                        entity.progress = 0;
                    }
                }
            }

            inputStack.shrink(1);
            BlockEntity.setChanged(level, pos, state);
        }
    }

    public static boolean isValidInput(ItemStack stack) {
        return isStone(stack) || isDirt(stack);
    }

    private static boolean isStone(ItemStack stack) {
        return stack.is(Items.STONE) || stack.is(Items.COBBLESTONE)
                || stack.is(Items.DEEPSLATE) || stack.is(Items.COBBLED_DEEPSLATE)
                || stack.is(Items.GRAVEL);
    }

    private static boolean isDirt(ItemStack stack) {
        return stack.is(Items.DIRT) || stack.is(Items.GRASS_BLOCK)
                || stack.is(Items.ROOTED_DIRT) || stack.is(Items.COARSE_DIRT)
                || stack.is(Items.PODZOL) || stack.is(Items.SAND);
    }

    public static int getFuelTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) return 1600;
        if (stack.is(Items.COAL_BLOCK)) return 14400;
        if (stack.is(Items.BLAZE_ROD)) return 2400;
        if (stack.is(Items.LAVA_BUCKET)) return 20000;
        if (stack.is(Items.OAK_LOG) || stack.is(Items.BIRCH_LOG)
                || stack.is(Items.SPRUCE_LOG) || stack.is(Items.JUNGLE_LOG)
                || stack.is(Items.ACACIA_LOG) || stack.is(Items.DARK_OAK_LOG)
                || stack.is(Items.MANGROVE_LOG) || stack.is(Items.CHERRY_LOG)) return 300;
        if (stack.is(Items.OAK_PLANKS) || stack.is(Items.BIRCH_PLANKS)
                || stack.is(Items.SPRUCE_PLANKS) || stack.is(Items.JUNGLE_PLANKS)
                || stack.is(Items.ACACIA_PLANKS) || stack.is(Items.DARK_OAK_PLANKS)) return 300;
        if (stack.is(Items.STICK)) return 100;
        return 0;
    }

    // --- Container ---

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case 0 -> isValidInput(stack);
            case 1 -> getFuelTime(stack) > 0;
            default -> false; // 出力スロットは直接挿入不可
        };
    }

    @Override public int getContainerSize() { return 3; }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = inventory.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (stack.getCount() <= amount) {
            inventory.set(slot, ItemStack.EMPTY);
            return stack;
        }
        return stack.split(amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override public void clearContent() { inventory.replaceAll(ignored -> ItemStack.EMPTY); }

    // --- NBT ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, inventory);
        output.putInt("Progress", progress);
        output.putInt("ProcessTimer", processTimer);
        output.putInt("FuelTime", fuelTime);
        output.putInt("MaxFuelTime", maxFuelTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, inventory);
        progress = input.getIntOr("Progress", 0);
        processTimer = input.getIntOr("ProcessTimer", 0);
        fuelTime = input.getIntOr("FuelTime", 0);
        maxFuelTime = input.getIntOr("MaxFuelTime", 0);
    }

    // --- MenuProvider ---

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.iron_extractor." + tier.id);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ExtractorScreenHandler(containerId, playerInventory, this, this.containerData);
    }
}
