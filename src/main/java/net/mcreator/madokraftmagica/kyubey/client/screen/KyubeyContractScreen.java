package net.mcreator.madokraftmagica.kyubey.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
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
    
    // Custom fonts
    private static final ResourceLocation CUSTOM_FONT = new ResourceLocation(MadokraftmagicaMod.MODID, "hangyaku");
    private static final ResourceLocation TITLE_FONT = new ResourceLocation(MadokraftmagicaMod.MODID, "gd-madomaru");
    private Font customFont;
    
    private List<Button> currentButtons = new ArrayList<>();
    
    // Item selection state
    private List<Item> availableItems = new ArrayList<>();
    private Item selectedItem = null;
    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 4; // Reduced from 6 to 4 to make room for bottom buttons
    private static final int ITEM_BUTTON_HEIGHT = 18;

    public KyubeyContractScreen(KyubeyContractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 240;
        this.imageHeight = 180;
        
        // Initialize with default font, will be updated when screen is shown
        this.customFont = Minecraft.getInstance().font;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // For Minecraft 1.19.2, we'll use a simpler approach
        // The custom font will be loaded automatically by the game if the JSON is correct
        // We can reference it using Component.literal().withStyle(Style.EMPTY.withFont(CUSTOM_FONT))
        // For now, we'll use the default font but the infrastructure is ready for custom font
        this.customFont = this.minecraft.font;
        MadokraftmagicaMod.LOGGER.info("Using font for Kyubey screen");
        
        this.clearButtons();
        this.setupButtonsForCurrentState();
        
        // Initialize available items list
        this.initializeAvailableItems();
    }
    
    private void initializeAvailableItems() {
        this.availableItems.clear();
        
        // Add common Minecraft items
        this.availableItems.add(Items.DIAMOND);
        this.availableItems.add(Items.EMERALD);
        this.availableItems.add(Items.GOLD_INGOT);
        this.availableItems.add(Items.IRON_INGOT);
        this.availableItems.add(Items.NETHERITE_INGOT);
        this.availableItems.add(Items.DIAMOND_SWORD);
        this.availableItems.add(Items.DIAMOND_PICKAXE);
        this.availableItems.add(Items.DIAMOND_AXE);
        this.availableItems.add(Items.DIAMOND_SHOVEL);
        this.availableItems.add(Items.DIAMOND_HOE);
        this.availableItems.add(Items.DIAMOND_HELMET);
        this.availableItems.add(Items.DIAMOND_CHESTPLATE);
        this.availableItems.add(Items.DIAMOND_LEGGINGS);
        this.availableItems.add(Items.DIAMOND_BOOTS);
        this.availableItems.add(Items.ENCHANTED_BOOK);
        this.availableItems.add(Items.ENDER_PEARL);
        this.availableItems.add(Items.BLAZE_ROD);
        this.availableItems.add(Items.NETHER_STAR);
        this.availableItems.add(Items.DRAGON_EGG);
        this.availableItems.add(Items.ELYTRA);
        this.availableItems.add(Items.TOTEM_OF_UNDYING);
        this.availableItems.add(Items.BEACON);
        this.availableItems.add(Items.CONDUIT);
        this.availableItems.add(Items.HEART_OF_THE_SEA);
        this.availableItems.add(Items.TRIDENT);
        this.availableItems.add(Items.CROSSBOW);
        this.availableItems.add(Items.BOW);
        this.availableItems.add(Items.ARROW);
        this.availableItems.add(Items.GOLDEN_APPLE);
        this.availableItems.add(Items.ENCHANTED_GOLDEN_APPLE);
        this.availableItems.add(Items.EXPERIENCE_BOTTLE);
        this.availableItems.add(Items.FIRE_CHARGE);
        this.availableItems.add(Items.FIREWORK_ROCKET);
        this.availableItems.add(Items.END_CRYSTAL);
        this.availableItems.add(Items.SHULKER_BOX);
        this.availableItems.add(Items.ENDER_CHEST);
        this.availableItems.add(Items.OBSIDIAN);
        this.availableItems.add(Items.CRYING_OBSIDIAN);
        this.availableItems.add(Items.ANCIENT_DEBRIS);
        this.availableItems.add(Items.NETHERITE_SCRAP);
    }
    
    // Helper method to create Component with custom font
    private Component createTextWithCustomFont(String text) {
        return Component.literal(text)
            .withStyle(s -> s.withFont(CUSTOM_FONT));
    }
    
    // Helper method to create Component with title font
    private Component createTitleWithCustomFont(String text) {
        return Component.literal(text.toUpperCase()) // Ensure uppercase for title font
            .withStyle(s -> s.withFont(TITLE_FONT));
    }
    
    // Helper method to draw text with custom font
    private void drawCustomText(PoseStack poseStack, String text, int x, int y, int color) {
        Component component = Component.literal(text)
            .withStyle(s -> s.withFont(CUSTOM_FONT));
        this.font.draw(poseStack, component, x, y, color);
    }
    
    // Helper method to draw title with custom font
    private void drawCustomTitle(PoseStack poseStack, String text, int x, int y, int color) {
        Component component = Component.literal(text.toUpperCase())
            .withStyle(s -> s.withFont(TITLE_FONT));
        this.font.draw(poseStack, component, x, y, color);
    }
    
    // Helper method to get text width with custom font
    private int getCustomTextWidth(String text) {
        Component component = Component.literal(text); // Use default font for now
        return this.font.width(component);
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
        // Center content vertically between subtitle (ends ~y=65) and bottom margin (y=160)  
        // Content area: 65 to 160 = 95px, center at y=112
        int centerY = this.topPos + 112;
        
        Button yesButton = createStyledButton(centerX - 60, centerY, 50, 20,
                createTextWithCustomFont("YES"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.WISH_SELECTION));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button noButton = createStyledButton(centerX + 10, centerY, 50, 20,
                createTextWithCustomFont("NO"), button -> {
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
        // No subtitle, so center content between title (ends ~y=30) and bottom margin (y=160)
        // Content area: 30 to 160 = 130px
        // Button heights: 3 * 20px + 2 * 5px spacing + cancel button 20px + 15px spacing = 85px total  
        // Center this 85px block: start at y = 30 + (130-85)/2 = 52.5 ≈ 53
        int startY = this.topPos + 53;
        
        Button itemWishButton = createStyledButton(centerX - 80, startY, 160, 20,
                createTextWithCustomFont("WISH FOR MINECRAFT ITEM"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.ITEM_WISH);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.ITEM_WISH));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button entityWishButton = createStyledButton(centerX - 80, startY + 25, 160, 20,
                createTextWithCustomFont("WISH FOR MINECRAFT ENTITY"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.ENTITY_WISH);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.ENTITY_WISH));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button eventWishButton = createStyledButton(centerX - 80, startY + 50, 160, 20,
                createTextWithCustomFont("WISH FOR EVENT CONTROL"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.EVENT_WISH);
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new KyubeyScreenStatePacket(this.menu.getKyubey().getId(), KyubeyScreenState.EVENT_WISH));
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX - 30, startY + 85, 60, 20,
                createTextWithCustomFont("CANCEL"), button -> {
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
        // No subtitle, so content starts from title area (ends ~y=30)
        // Available space for items: from y=35 to y=130 (leaving room for bottom buttons at y=135)
        // Item list: 4 items * 18px + 3 gaps * 2px = 78px total
        // Center in available space: start at y = 35 + (95-78)/2 = 43.5 ≈ 44
        int startY = this.topPos + 44;
        
        // Calculate visible items
        int maxVisibleItems = Math.min(ITEMS_PER_PAGE, this.availableItems.size() - scrollOffset);
        
        // Item selection buttons
        for (int i = 0; i < maxVisibleItems; i++) {
            int itemIndex = scrollOffset + i;
            if (itemIndex >= this.availableItems.size()) break;
            
            Item item = this.availableItems.get(itemIndex);
            String itemName = new ItemStack(item).getHoverName().getString().toUpperCase();
            
            // Truncate long item names
            if (itemName.length() > 20) {
                itemName = itemName.substring(0, 17) + "...";
            }
            
            int buttonY = startY + (i * (ITEM_BUTTON_HEIGHT + 2));
            boolean isSelected = item == this.selectedItem;
            
            Button itemButton = createItemButton(centerX - 90, buttonY, 180, ITEM_BUTTON_HEIGHT,
                    createTextWithCustomFont(itemName), item, isSelected, button -> {
                        this.selectedItem = item;
                        // Refresh buttons to show selection
                        this.clearButtons();
                        this.setupButtonsForCurrentState();
                    });
            
            this.addRenderableWidget(itemButton);
            currentButtons.add(itemButton);
        }
        
        // Scroll buttons
        if (scrollOffset > 0) {
            Button scrollUpButton = createStyledButton(centerX + 95, startY, 20, 20,
                    createTextWithCustomFont("^"), button -> {
                        this.scrollOffset = Math.max(0, this.scrollOffset - 1);
                        this.clearButtons();
                        this.setupButtonsForCurrentState();
                    });
            this.addRenderableWidget(scrollUpButton);
            currentButtons.add(scrollUpButton);
        }
        
        if (scrollOffset + ITEMS_PER_PAGE < this.availableItems.size()) {
            Button scrollDownButton = createStyledButton(centerX + 95, startY + ((ITEMS_PER_PAGE - 1) * (ITEM_BUTTON_HEIGHT + 2)), 20, 20,
                    createTextWithCustomFont("v"), button -> {
                        this.scrollOffset = Math.min(this.availableItems.size() - ITEMS_PER_PAGE, this.scrollOffset + 1);
                        this.clearButtons();
                        this.setupButtonsForCurrentState();
                    });
            this.addRenderableWidget(scrollDownButton);
            currentButtons.add(scrollDownButton);
        }
        
        // Bottom control buttons with consistent margin (20px from bottom)
        int bottomY = this.topPos + this.imageHeight - 40; // 20px margin for buttons height 20px
        
        Button makeWishButton = createStyledButton(centerX - 80, bottomY, 70, 20,
                createTextWithCustomFont("MAKE WISH"), button -> {
                    if (this.selectedItem != null) {
                        // TODO: Send packet to server with selected item
                        MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                                new ContractResponsePacket(this.menu.getKyubey().getId(), true));
                        this.onClose();
                    }
                });
        makeWishButton.active = this.selectedItem != null; // Only active if item is selected
        
        Button backButton = createStyledButton(centerX - 5, bottomY, 35, 20,
                createTextWithCustomFont("BACK"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    this.selectedItem = null;
                    this.scrollOffset = 0;
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX + 35, bottomY, 45, 20,
                createTextWithCustomFont("CANCEL"), button -> {
                    MadokraftmagicaMod.PACKET_HANDLER.sendToServer(
                            new ContractResponsePacket(this.menu.getKyubey().getId(), false));
                    this.onClose();
                });
        
        this.addRenderableWidget(makeWishButton);
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(cancelButton);
        currentButtons.add(makeWishButton);
        currentButtons.add(backButton);
        currentButtons.add(cancelButton);
    }
    
    private void setupEntityWishButtons() {
        int centerX = this.leftPos + this.imageWidth / 2;
        // Center the buttons vertically in available space with consistent bottom margin
        int bottomY = this.topPos + this.imageHeight - 40; // 20px margin from bottom
        
        Button backButton = createStyledButton(centerX - 40, bottomY, 35, 20,
                createTextWithCustomFont("BACK"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX + 5, bottomY, 45, 20,
                createTextWithCustomFont("CANCEL"), button -> {
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
        // Center the event options and control buttons vertically
        // Content: 2 event buttons (40px) + 1 gap (5px) + bottom buttons (20px) + gap (15px) = 80px total
        // Center this in the available 120px space: start at y = 40 + (120-80)/2 = 60
        int startY = this.topPos + 60;
        
        Button timeSetButton = createStyledButton(centerX - 80, startY, 160, 20,
                createTextWithCustomFont("SET TIME COMMAND"), button -> {
                    // Future implementation
                });
        
        Button teleportButton = createStyledButton(centerX - 80, startY + 25, 160, 20,
                createTextWithCustomFont("TELEPORT TO POSITION"), button -> {
                    // Future implementation
                });
        
        // Bottom buttons with consistent margin
        int bottomY = this.topPos + this.imageHeight - 40; // 20px margin from bottom
        
        Button backButton = createStyledButton(centerX - 40, bottomY, 35, 20,
                createTextWithCustomFont("BACK"), button -> {
                    this.menu.setCurrentState(KyubeyScreenState.WISH_SELECTION);
                    this.clearButtons();
                    this.setupButtonsForCurrentState();
                });
        
        Button cancelButton = createStyledButton(centerX + 5, bottomY, 45, 20,
                createTextWithCustomFont("CANCEL"), button -> {
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
                
                // Draw double button border
                drawDoubleBorder(poseStack, this.x, this.y, this.width, this.height, BUTTON_BORDER_COLOR);
                
                // Draw button text with custom font (no shadow)
                int textColor = this.active ? TEXT_COLOR : 0xFF666666;
                String buttonText = this.getMessage().getString();
                int textWidth = minecraft.font.width(this.getMessage());
                int textX = this.x + (this.width - textWidth) / 2;
                int textY = this.y + (this.height - 8) / 2;
                
                // Use our custom font drawing method to avoid shadows
                Component customText = Component.literal(buttonText).withStyle(s -> s.withFont(CUSTOM_FONT));
                minecraft.font.draw(poseStack, customText, textX, textY, textColor);
            }
        };
    }
    
    private Button createItemButton(int x, int y, int width, int height, Component text, Item item, boolean isSelected, Button.OnPress onPress) {
        return new Button(x, y, width, height, text, onPress) {
            @Override
            public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                // Different background color for selected item
                int backgroundColor = isSelected ? 0xFFE6D7B0 : BUTTON_COLOR; // Light golden for selected
                fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor);
                
                // Draw double button border
                drawDoubleBorder(poseStack, this.x, this.y, this.width, this.height, BUTTON_BORDER_COLOR);
                
                // Draw item icon
                ItemStack itemStack = new ItemStack(item);
                minecraft.getItemRenderer().renderGuiItem(itemStack, this.x + 2, this.y + 1);
                
                // Draw button text with custom font (no shadow)
                int textColor = this.active ? TEXT_COLOR : 0xFF666666;
                String buttonText = this.getMessage().getString();
                int textY = this.y + (this.height - 8) / 2;
                
                // Use our custom font drawing method to avoid shadows
                Component customText = Component.literal(buttonText).withStyle(s -> s.withFont(CUSTOM_FONT));
                minecraft.font.draw(poseStack, customText, this.x + 22, textY, textColor);
            }
        };
    }
    
    private void drawDoubleBorder(PoseStack poseStack, int x, int y, int width, int height, int color) {
        // Outer border
        // Top border
        fill(poseStack, x, y, x + width, y + 1, color);
        // Bottom border
        fill(poseStack, x, y + height - 1, x + width, y + height, color);
        // Left border
        fill(poseStack, x, y, x + 1, y + height, color);
        // Right border
        fill(poseStack, x + width - 1, y, x + width, y + height, color);
        
        // Inner border (2 pixels inside the outer border)
        if (width > 4 && height > 4) {
            // Top inner border
            fill(poseStack, x + 2, y + 2, x + width - 2, y + 3, color);
            // Bottom inner border
            fill(poseStack, x + 2, y + height - 3, x + width - 2, y + height - 2, color);
            // Left inner border
            fill(poseStack, x + 2, y + 2, x + 3, y + height - 2, color);
            // Right inner border
            fill(poseStack, x + width - 3, y + 2, x + width - 2, y + height - 2, color);
        }
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
        
        // Double Golden border
        drawDoubleBorder(poseStack, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, BORDER_COLOR);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        // Main title at the top (moved 5 pixels lower)
        Component mainTitle = createTitleWithCustomFont("MAKE A CONTRACT");
        int mainTitleX = (this.imageWidth - this.font.width(mainTitle)) / 2;
        this.font.draw(poseStack, mainTitle, mainTitleX, 11, TEXT_COLOR);
        
        // State-specific subtitle (only for some states)
        KyubeyScreenState state = this.menu.getCurrentState();
        Component subtitle = null;
        
        switch (state) {
            case CONTRACT_CONFIRMATION:
                subtitle = createTextWithCustomFont("WANNA MAKE A CONTRACT?");
                break;
            case ENTITY_WISH:
                subtitle = createTextWithCustomFont("WISH FOR AN ENTITY");
                break;
            case EVENT_WISH:
                subtitle = createTextWithCustomFont("WISH FOR EVENT CONTROL");
                break;
            default:
                // No subtitle for WISH_SELECTION and ITEM_WISH
                break;
        }
        
        if (subtitle != null) {
            int subtitleX = (this.imageWidth - this.font.width(subtitle)) / 2;
            // Move subtitle lower to be centered between title and content
            int subtitleY = (state == KyubeyScreenState.CONTRACT_CONFIRMATION) ? 65 : 30;
            this.font.draw(poseStack, subtitle, subtitleX, subtitleY, TEXT_COLOR);
        }
        
        // Show selected item information for ITEM_WISH state
        if (state == KyubeyScreenState.ITEM_WISH) {
            if (this.selectedItem != null) {
                String selectedText = "SELECTED: " + new ItemStack(this.selectedItem).getHoverName().getString().toUpperCase();
                Component selectedComponent = createTextWithCustomFont(selectedText);
                int selectedX = (this.imageWidth - this.font.width(selectedComponent)) / 2;
                this.font.draw(poseStack, selectedComponent, selectedX, this.imageHeight - 50, 0xFF006600); // Dark green color, moved up
            } else {
                Component selectText = createTextWithCustomFont("SELECT AN ITEM FROM THE LIST");
                int selectX = (this.imageWidth - this.font.width(selectText)) / 2;
                this.font.draw(poseStack, selectText, selectX, this.imageHeight - 50, 0xFF666666); // Gray color, moved up
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Only handle scrolling when on item wish screen
        if (this.menu.getCurrentState() == KyubeyScreenState.ITEM_WISH) {
            int oldScrollOffset = this.scrollOffset;
            
            if (delta > 0) {
                // Scroll up
                this.scrollOffset = Math.max(0, this.scrollOffset - 1);
            } else if (delta < 0) {
                // Scroll down
                this.scrollOffset = Math.min(Math.max(0, this.availableItems.size() - ITEMS_PER_PAGE), this.scrollOffset + 1);
            }
            
            // Refresh buttons if scroll changed
            if (oldScrollOffset != this.scrollOffset) {
                this.clearButtons();
                this.setupButtonsForCurrentState();
                return true;
            }
        }
        
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
