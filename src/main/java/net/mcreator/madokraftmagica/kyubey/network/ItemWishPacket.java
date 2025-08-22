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

import java.util.function.Supplier;

public class ItemWishPacket {
    private final int kyubeyId;
    private final ResourceLocation itemId;

    public ItemWishPacket(int kyubeyId, Item item) {
        this.kyubeyId = kyubeyId;
        this.itemId = ForgeRegistries.ITEMS.getKey(item);
    }

    public ItemWishPacket(FriendlyByteBuf buffer) {
        this.kyubeyId = buffer.readInt();
        this.itemId = buffer.readResourceLocation();
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
                    Item wishedItem = ForgeRegistries.ITEMS.getValue(this.itemId);
                    if (wishedItem != null) {
                        // Create the item stack
                        ItemStack itemStack = new ItemStack(wishedItem, 1);
                        
                        // Add the item to player's inventory
                        if (!player.getInventory().add(itemStack)) {
                            // If inventory is full, drop the item
                            player.drop(itemStack, false);
                        }
                        
                        // Send success message
                        String itemName = itemStack.getHoverName().getString();
                        player.sendSystemMessage(Component.literal("Your wish has been granted! You received: " + itemName));
                    } else {
                        player.sendSystemMessage(Component.literal("Something went wrong with your wish..."));
                    }
                }
            }
        });
        return true;
    }
}
