package com.ironextractor.client.screen;

import com.ironextractor.screen.ExtractorScreenHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class ExtractorScreen extends AbstractContainerScreen<ExtractorScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
    private static final Identifier BURN_PROGRESS_SPRITE =
            Identifier.withDefaultNamespace("container/furnace/burn_progress");
    private static final Identifier LIT_PROGRESS_SPRITE =
            Identifier.withDefaultNamespace("container/furnace/lit_progress");

    // 0xFFFFFFFF = 不透明ホワイト（アルファ必須）
    private static final int COLOR_LABEL = 0xFFFFFFFF;

    public ExtractorScreen(ExtractorScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 166);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;

        // 背景テクスチャ
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 0f, imageWidth, imageHeight, 256, 256);

        // 燃料インジケーター（炎スプライト / 下から上に充填）
        int fuelTime    = menu.getFuelTime();
        int maxFuelTime = menu.getMaxFuelTime();
        if (maxFuelTime > 0 && fuelTime > 0) {
            int litH = Mth.ceil((float) fuelTime / maxFuelTime * 13.0f) + 1;
            g.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE,
                    14, 14,
                    0, 14 - litH,
                    x + 56, y + 36 + (14 - litH),
                    14, litH);
        }

        // 進捗矢印（左から右に充填、0–100% → 0–24px）
        int burnW = Mth.ceil(menu.getProgress() / 100.0f * 24.0f);
        if (burnW > 0) {
            g.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE,
                    24, 16,
                    0, 0,
                    x + 79, y + 34,
                    burnW, 16);
        }

        super.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        // タイトル（例: "Basic Iron Extractor"）
        g.text(this.font, title, this.titleLabelX, this.titleLabelY, COLOR_LABEL);
        // "Inventory" ラベル
        g.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, COLOR_LABEL);

        // 鉄生成進捗（燃料スロット右の空きエリア）
        g.text(this.font, Component.literal("Iron: " + menu.getProgress() + "%"), 76, 54, COLOR_LABEL);

        // ブロック処理進捗
        int maxTicks = menu.getMaxProcessTicks();
        int blockPct = maxTicks > 0 ? Math.min(100, menu.getProcessTimer() * 100 / maxTicks) : 0;
        g.text(this.font, Component.literal("Block: " + blockPct + "%"), 76, 63, COLOR_LABEL);
    }
}
