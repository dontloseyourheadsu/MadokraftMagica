package net.mcreator.madokraftmagica.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;

@Mod.EventBusSubscriber(modid = MadokraftmagicaMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagicHudOverlay {
    private static final int BORDER_COLOR = 0xFFFFD15C;
    private static final int BAR_BACKGROUND_COLOR = 0xA0101010;

    private MagicHudOverlay() {
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!VanillaGuiOverlay.HOTBAR.id().equals(event.getOverlay().id())) {
            return;
        }

        if (!MagicHudState.shouldShowBar()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        int magicCurrent = MagicHudState.getMagicCurrent();
        int magicMax = MagicHudState.getMagicMax();
        float ratio = magicMax <= 0 ? 0.0F : Math.min(1.0F, (float) magicCurrent / (float) magicMax);

        int x = 10;
        int y = event.getWindow().getGuiScaledHeight() - 38;
        int width = 120;
        int height = 8;

        PoseStack poseStack = event.getPoseStack();

        GuiComponent.fill(poseStack, x - 1, y - 1, x + width + 1, y + height + 1, BORDER_COLOR);
        GuiComponent.fill(poseStack, x, y, x + width, y + height, BAR_BACKGROUND_COLOR);

        int fillWidth = Math.max(0, Math.min(width, Math.round(width * ratio)));
        if (fillWidth > 0) {
            GuiComponent.fill(poseStack, x, y, x + fillWidth, y + height, MagicHudState.getSoulGemColor());
        }

        String percentText = Math.round(ratio * 100.0F) + "%";
        minecraft.font.drawShadow(poseStack, Component.literal(percentText), x + width + 6, y - 1, 0xFFFFFFFF);
    }
}

