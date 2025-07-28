package net.mcreator.madokraftmagica.kyubey.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mcreator.madokraftmagica.kyubey.menu.KyubeyContractMenu;
import net.mcreator.madokraftmagica.kyubey.network.ContractResponsePacket;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class KyubeyContractScreen extends AbstractContainerScreen<KyubeyContractMenu> {

    public KyubeyContractScreen(KyubeyContractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 200;
        this.imageHeight = 120;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.leftPos + this.imageWidth / 2;
        int centerY = this.topPos + 40;

        // Yes button
        this.addRenderableWidget(new Button(centerX - 60, centerY, 50, 20,
                Component.literal("Yes"),
                button -> {
                    MadokraftmagicaMod.PACKET_HANDLER
                            .sendToServer(new ContractResponsePacket(this.menu.getKyubey().getId(), true));
                    this.onClose();
                }));

        // No button
        this.addRenderableWidget(new Button(centerX + 10, centerY, 50, 20,
                Component.literal("No"),
                button -> {
                    MadokraftmagicaMod.PACKET_HANDLER
                            .sendToServer(new ContractResponsePacket(this.menu.getKyubey().getId(), false));
                    this.onClose();
                }));
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        // Simple background
        fill(poseStack, this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight,
                0x88000000);
        fill(poseStack, this.leftPos + 2, this.topPos + 2, this.leftPos + this.imageWidth - 2,
                this.topPos + this.imageHeight - 2, 0xFF333333);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        Component title = Component.literal("Wanna make a contract?");
        int titleX = (this.imageWidth - this.font.width(title)) / 2;
        this.font.draw(poseStack, title, titleX, 15, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
