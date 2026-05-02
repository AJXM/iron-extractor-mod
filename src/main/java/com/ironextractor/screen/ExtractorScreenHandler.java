package com.ironextractor.screen;

import com.ironextractor.ModScreenHandlers;
import com.ironextractor.blockentity.ExtractorBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ExtractorScreenHandler extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData containerData;

    // クライアント側コンストラクタ
    public ExtractorScreenHandler(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(5));
    }

    // サーバー側コンストラクタ
    public ExtractorScreenHandler(int containerId, Inventory playerInventory,
                                   Container container, ContainerData containerData) {
        super(ModScreenHandlers.EXTRACTOR_SCREEN_HANDLER, containerId);
        checkContainerSize(container, 3);
        this.container = container;
        this.containerData = containerData;
        container.startOpen(playerInventory.player);

        // スロット 0=入力(石/土), 1=燃料(燃料アイテムのみ), 2=出力(鉄ブロック)
        this.addSlot(new Slot(container, 0, 56, 17));
        this.addSlot(new FuelSlot(container, 1, 56, 53));
        this.addSlot(new OutputSlot(container, 2, 116, 35));

        // プレイヤーインベントリ（3行×9列）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // ホットバー
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(containerData);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack originalStack = slot.getItem();
        ItemStack newStack = originalStack.copy();

        if (index < 3) {
            // 機械スロット → インベントリへ
            if (!this.moveItemStackTo(originalStack, 3, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // インベントリ → 種別に応じた機械スロットへ
            boolean moved = false;
            if (ExtractorBlockEntity.isValidInput(originalStack)) {
                moved = this.moveItemStackTo(originalStack, 0, 1, false);
            }
            if (!moved && ExtractorBlockEntity.getFuelTime(originalStack) > 0) {
                moved = this.moveItemStackTo(originalStack, 1, 2, false);
            }
            if (!moved) return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public int getProgress()        { return containerData.get(0); }
    public int getProcessTimer()    { return containerData.get(1); }
    public int getFuelTime()        { return containerData.get(2); }
    public int getMaxFuelTime()     { return containerData.get(3); }
    public int getMaxProcessTicks() { return containerData.get(4); }

    private static class FuelSlot extends Slot {
        public FuelSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return ExtractorBlockEntity.getFuelTime(stack) > 0;
        }
    }

    private static class OutputSlot extends Slot {
        public OutputSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
