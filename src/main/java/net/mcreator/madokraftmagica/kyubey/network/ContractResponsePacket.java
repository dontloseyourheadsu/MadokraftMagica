package net.mcreator.madokraftmagica.kyubey.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;

import java.util.function.Supplier;

public class ContractResponsePacket {
    private final int kyubeyId;
    private final boolean accepted;

    public ContractResponsePacket(int kyubeyId, boolean accepted) {
        this.kyubeyId = kyubeyId;
        this.accepted = accepted;
    }

    public ContractResponsePacket(FriendlyByteBuf buffer) {
        this.kyubeyId = buffer.readInt();
        this.accepted = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.kyubeyId);
        buffer.writeBoolean(this.accepted);
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

                    // Send message based on response
                    if (this.accepted) {
                        player.sendSystemMessage(Component.literal("Contract made!"));
                    } else {
                        player.sendSystemMessage(Component.literal("Contract ignored"));
                    }
                }
            }
        });
        return true;
    }
}
