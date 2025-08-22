package net.mcreator.madokraftmagica.kyubey.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;

import java.util.function.Supplier;

public class ItemWishPacket {
    private final int kyubeyId;
    private final ResourceLocation itemId;

    public ItemWishPacket(int kyubeyId, Item item) {
        this.kyubeyId = kyubeyId;
        ResourceLocation tempId = ForgeRegistries.ITEMS.getKey(item);
        this.itemId = tempId != null ? tempId : new ResourceLocation("minecraft:air");
        
        // Debug: Log what we're sending
        MadokraftmagicaMod.LOGGER.info("ItemWishPacket created with item: {} for item object: {}", 
                this.itemId.toString(), item.getClass().getSimpleName());
    }

    public ItemWishPacket(FriendlyByteBuf buffer) {
        this.kyubeyId = buffer.readInt();
        ResourceLocation tempId = buffer.readResourceLocation();
        this.itemId = tempId != null ? tempId : new ResourceLocation("minecraft:air");
        MadokraftmagicaMod.LOGGER.info("ItemWishPacket decoded with item ID: {}", this.itemId.toString());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.kyubeyId);
        buffer.writeResourceLocation(this.itemId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                Entity entity = player.level.getEntity(this.kyubeyId);
                if (entity instanceof KyubeyEntity kyubey) {
                    // End the interaction state
                    kyubey.endInteraction();

                    // Get the item from the registry
                    MadokraftmagicaMod.LOGGER.info("Server received ItemWishPacket with item ID: {}", 
                            this.itemId != null ? this.itemId.toString() : "null");
                    Item wishedItem = ForgeRegistries.ITEMS.getValue(this.itemId);
                    MadokraftmagicaMod.LOGGER.info("Retrieved item from registry: {}", 
                            wishedItem != null ? wishedItem.toString() : "null");
                    
                    if (wishedItem != null && wishedItem != net.minecraft.world.item.Items.AIR) {
                        ItemStack itemStack = new ItemStack(wishedItem);

                        // Capture the name BEFORE mutating the stack
                        Component itemName = itemStack.getHoverName().copy();

                        // Try to add; if full, drop the same stack
                        if (!player.getInventory().add(itemStack)) {
                            player.drop(itemStack, false);
                        }

                        player.sendSystemMessage(
                            Component.literal("Your wish has been granted! You received: ")
                                .append(itemName)
                        );
                    } else {
                        player.sendSystemMessage(Component.literal(
                            "Something went wrong with your wish... Item ID: " +
                            (this.itemId != null ? this.itemId.toString() : "null")
                        ));
                    }
                }
            }
        });
        return true;
    }
}
