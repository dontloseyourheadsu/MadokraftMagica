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
import net.mcreator.madokraftmagica.kyubey.network.KyubeyScreenStatePacket;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class KyubeyContractScreen extends AbstractContainerScreen<KyubeyContractMenu> {
    
    // UI Colors
    private static final int BACKGROUND_COLOR = 0xFFFFFFFF; // White background
    private static final int BORDER_COLOR = 0xFFAE935E; // Golden border
    private static final int BUTTON_COLOR = 0xFFFFFFFF; // White button background
    private static final int BUTTON_BORDER_COLOR = 0xFFAE935E; // Golden button border
    private static final int TEXT_COLOR = 0xFF000000; // Black text
    
    private List<Button> currentButtons = new ArrayList<>();

    public KyubeyContractScreen(KyubeyContractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 240;
        this.imageHeight = 180;
    }

    @Override
    protected void init() {
        super.init();
        this.clearButtons();
        this.setupButtonsForCurrentState();
    }
    
    private void clearButtons() {
        for (Button button : currentButtons) {
            this.removeWidget(button);
        }
        currentButtons.clear();
    }
    
    private void setupButtonsForCurrentState() {
        KyubeyScreenState state = this.menu.getCurrentState();
        
        switch (state) {
            case CONTRACT_CONFIRMATION:
                setupContractButtons();
                break;
            case WISH_SELECTION:
                setupWishSelectionButtons();
                break;
            case ITEM_WISH:
                setupItemWishButtons();
                break;
            case ENTITY_WISH:
                setupEntityWishButtons();
                break;
            case EVENT_WISH:
                setupEventWishButtons();
                break;
        }
    }
    
    private void setupContractButtons() {
        int centerX = this.leftPos + this.imageWidth / 2;
        int centerY = this.topPos + 80;
        
        Button yesButton = createStyledButton(centerX - 60, centerY, 50, 20,
                Component.literal("Yes"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.WISH_SELECTION));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button noButton = createStyledButton(centerX + 10, centerY, 50, 20,
                Component.literal("No"), button -> {
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new ContractResponsePacket(this.menu.getKyubey().getId(), false));
                    this.onClose();
                });
        
        this.addRenderableWidget(yesButton);
        this.addRenderableWidget(noButton);
        currentButtons.add(yesButton);
        currentButtons.add(noButton);
    }
    
    private void setupWishSelectionButtons() {
        int centerX = this.leftPos + this.imageWidth / 2;
        int startY = this.topPos + 60;
        
        Button itemWishButton = createStyledButton(centerX - 80, startY, 160, 20,
                Component.literal("Wish for Minecraft Item"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.ITEM_WISH);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.ITEM_WISH));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button entityWishButton = createStyledButton(centerX - 80, startY + 25, 160, 20,
                Component.literal("Wish for Minecraft Entity"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.ENTITY_WISH);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.ENTITY_WISH));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button eventWishButton = createStyledButton(centerX - 80, startY + 50, 160, 20,
                Component.literal("Wish for Event Control"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.EVENT_WISH);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.EVENT_WISH));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX - 30, startY + 80, 60, 20,
                Component.literal("Cancel"), button -> {
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new ContractResponsePacket(this.menu.getKyubey().getId(), false));
                    this.onClose();
                });
        
        this.addRenderableWidget(itemWishButton);
        this.addRenderableWidget(entityWishButton);
        this.addRenderableWidget(eventWishButton);
        this.addRenderableWidget(cancelButton);
        currentButtons.add(itemWishButton);
        currentButtons.add(entityWishButton);
        currentButtons.add(eventWishButton);
        currentButtons.add(cancelButton);
    }
    
    private void setupItemWishButtons() {
        int centerX = this.leftPos + this.imageWidth / 2;
        int startY = this.topPos + 80;
        
        Button backButton = createStyledButton(centerX - 80, startY, 70, 20,
                Component.literal("Back"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX + 10, startY, 70, 20,
                Component.literal("Cancel"), button -> {
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new ContractResponsePacket(this.menu.getKyubey().getId(), false));
                    this.onClose();
                });
        
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(cancelButton);
        currentButtons.add(backButton);
        currentButtons.add(cancelButton);
    }
    
    private void setupEntityWishButtons() {
        int centerX = this.leftPos + this.imageWidth / 2;
        int startY = this.topPos + 80;
        
        Button backButton = createStyledButton(centerX - 80, startY, 70, 20,
                Component.literal("Back"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX + 10, startY, 70, 20,
                Component.literal("Cancel"), button -> {
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new ContractResponsePacket(this.menu.getKyubey().getId(), false));
                    this.onClose();
                });
        
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(cancelButton);
        currentButtons.add(backButton);
        currentButtons.add(cancelButton);
    }
    
    private void setupEventWishButtons() {
        int centerX = this.leftPos + this.imageWidth / 2;
        int startY = this.topPos + 60;
        
        Button timeSetButton = createStyledButton(centerX - 80, startY, 160, 20,
                Component.literal("Set Time Command"), button -> {
                    // Future implementation
                });
        
        Button teleportButton = createStyledButton(centerX - 80, startY + 25, 160, 20,
                Component.literal("Teleport to Position"), button -> {
                    // Future implementation
                });
        
        Button backButton = createStyledButton(centerX - 80, startY + 55, 70, 20,
                Component.literal("Back"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX + 10, startY + 55, 70, 20,
                Component.literal("Cancel"), button -> {
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new ContractResponsePacket(this.menu.getKyubey().getId(), false));
                    this.onClose();
                });
        
        this.addRenderableWidget(timeSetButton);
        this.addRenderableWidget(teleportButton);
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(cancelButton);
        currentButtons.add(timeSetButton);
        currentButtons.add(teleportButton);
        currentButtons.add(backButton);
        currentButtons.add(cancelButton);
    }
    
    private Button createStyledButton(int x, int y, int width, int height, Component text, Button.OnPress onPress) {
        return new Button(x, y, width, height, text, onPress) {
            @Override
            public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                // Draw button background
                fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, BUTTON_COLOR);
                
                // Draw button border
                drawBorder(poseStack, this.x, this.y, this.width, this.height, BUTTON_BORDER_COLOR);
                
                // Draw button text
                int textColor = this.active ? TEXT_COLOR : 0xFF666666;
                drawCenteredString(poseStack, minecraft.font, this.getMessage(), 
                    this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
            }
        };
    }
    
    private void drawBorder(PoseStack poseStack, int x, int y, int width, int height, int color) {
        // Top border
        fill(poseStack, x, y, x + width, y + 1, color);
        // Bottom border
        fill(poseStack, x, y + height - 1, x + width, y + height, color);
        // Left border
        fill(poseStack, x, y, x + 1, y + height, color);
        // Right border
        fill(poseStack, x + width - 1, y, x + width, y + height, color);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        // White background
        fill(poseStack, this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight,
                BACKGROUND_COLOR);
        
        // Golden border
        drawBorder(poseStack, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, BORDER_COLOR);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        KyubeyScreenState state = this.menu.getCurrentState();
        Component title;
        
        switch (state) {
            case CONTRACT_CONFIRMATION:
                title = Component.literal("Wanna make a contract?");
                break;
            case WISH_SELECTION:
                title = Component.literal("Make a Wish");
                break;
            case ITEM_WISH:
                title = Component.literal("Wish for an Item");
                break;
            case ENTITY_WISH:
                title = Component.literal("Wish for an Entity");
                break;
            case EVENT_WISH:
                title = Component.literal("Wish for Event Control");
                break;
            default:
                title = Component.literal("Kyubey");
                break;
        }
        
        int titleX = (this.imageWidth - this.font.width(title)) / 2;
        this.font.draw(poseStack, title, titleX, 15, TEXT_COLOR);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
