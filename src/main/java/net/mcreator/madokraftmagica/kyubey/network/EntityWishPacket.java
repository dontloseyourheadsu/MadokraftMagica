package net.mcreator.madokraftmagica.kyubey.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import net.mcreator.madokraftmagica.kyubey.system.SoulGemContractHandler;
import net.mcreator.madokraftmagica.karma.KarmaData;

import java.util.function.Supplier;

public class EntityWishPacket {
    private static final int COUNTDOWN_SECONDS = 10;
    private static final int TICKS_PER_SECOND = 20;

    private final int kyubeyId;
    private final ResourceLocation entityId;

    public EntityWishPacket(int kyubeyId, EntityType<?> entityType) {
        this.kyubeyId = kyubeyId;
        ResourceLocation tempId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        this.entityId = tempId != null ? tempId : new ResourceLocation("minecraft", "marker");
    }

    public EntityWishPacket(FriendlyByteBuf buffer) {
        this.kyubeyId = buffer.readInt();
        ResourceLocation tempId = buffer.readResourceLocation();
        this.entityId = tempId != null ? tempId : new ResourceLocation("minecraft", "marker");
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.kyubeyId);
        buffer.writeResourceLocation(this.entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            if (KarmaData.isContracted(player)) {
                player.sendSystemMessage(Component.literal("Kyubey refuses: your soul is already under contract."));
                return;
            }

            Entity entity = player.level.getEntity(this.kyubeyId);
            if (entity instanceof KyubeyEntity kyubey) {
                kyubey.endInteraction();
            }

            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(this.entityId);
            if (entityType == null) {
                player.sendSystemMessage(Component.literal(
                        "Something went wrong with your wish... Entity ID: " + this.entityId));
                return;
            }

            String entityName = entityType.getDescription().getString();
            for (int remaining = COUNTDOWN_SECONDS; remaining >= 1; remaining--) {
                int delayTicks = (COUNTDOWN_SECONDS - remaining) * TICKS_PER_SECOND;
                int remainingFinal = remaining;
                MadokraftmagicaMod.queueServerWork(delayTicks, () -> player.displayClientMessage(
                        Component.literal("Summoning " + entityName + " in " + remainingFinal + "s"), true));
            }

            MadokraftmagicaMod.queueServerWork(COUNTDOWN_SECONDS * TICKS_PER_SECOND, () -> {
                ServerLevel level = player.getLevel();
                Entity spawned = entityType.create(level);
                if (spawned == null) {
                    player.sendSystemMessage(Component.literal(
                            "Your wish failed... Could not create: " + entityName));
                    return;
                }

                double spawnX = player.getX() + player.getLookAngle().x * 2.0D;
                double spawnY = player.getY();
                double spawnZ = player.getZ() + player.getLookAngle().z * 2.0D;

                spawned.moveTo(spawnX, spawnY, spawnZ, player.getYRot(), 0.0F);
                level.addFreshEntity(spawned);

                SoulGemContractHandler.finalizeContractIfNeeded(player);
                player.sendSystemMessage(Component.literal("Your wish has been granted!"));
            });
        });
        return true;
    }
}
