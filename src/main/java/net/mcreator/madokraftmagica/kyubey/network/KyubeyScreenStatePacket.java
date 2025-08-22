package net.mcreator.madokraftmagica.kyubey.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import net.mcreator.madokraftmagica.kyubey.client.screen.KyubeyScreenState;

import java.util.function.Supplier;

public class KyubeyScreenStatePacket {
    private final int kyubeyId;
    private final KyubeyScreenState state;

    public KyubeyScreenStatePacket(int kyubeyId, KyubeyScreenState state) {
        this.kyubeyId = kyubeyId;
        this.state = state;
    }

    public KyubeyScreenStatePacket(FriendlyByteBuf buffer) {
        this.kyubeyId = buffer.readInt();
        this.state = KyubeyScreenState.values()[buffer.readInt()];
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.kyubeyId);
        buffer.writeInt(this.state.ordinal());
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                Entity entity = player.level.getEntity(this.kyubeyId);
                if (entity instanceof KyubeyEntity kyubey) {
                    // Handle different state transitions on server
                    switch (this.state) {
                        case CONTRACT_CONFIRMATION:
                            // Back to contract confirmation
                            break;
                        case WISH_SELECTION:
                            // Move to wish selection
                            break;
                        case ITEM_WISH:
                        case ENTITY_WISH:
                        case EVENT_WISH:
                            // Handle specific wish types (for future implementation)
                            break;
                    }
                    // For now, just keep the interaction open
                }
            }
        });
        return true;
    }
}
