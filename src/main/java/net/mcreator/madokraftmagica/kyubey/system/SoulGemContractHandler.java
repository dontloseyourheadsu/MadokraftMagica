package net.mcreator.madokraftmagica.kyubey.system;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.gems.SoulGemUtil;
import net.mcreator.madokraftmagica.karma.KarmaData;
import net.mcreator.madokraftmagica.kyubey.network.MagicSyncPacket;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MadokraftmagicaMod.MODID)
public class SoulGemContractHandler {
    private static final int MAX_SOUL_GEMS = 2;
    private static final long GEM_GRACE_TICKS = 2000L;
    private static final long MAGIC_DRAIN_INTERVAL_TICKS = 600L;
    private static final int MAGIC_DRAIN_AMOUNT = 10;
    private static final long MAGIC_REGEN_INTERVAL_TICKS = 10L;
    private static final int MAGIC_PER_HEART = 200;
    private static final float HEART_HEAL_AMOUNT = 2.0F;

    private SoulGemContractHandler() {
    }

    public static void finalizeContractIfNeeded(ServerPlayer player) {
        if (KarmaData.isContracted(player)) {
            return;
        }

        int karmaAtContract = KarmaData.getKarma(player);
        String contractId = UUID.randomUUID().toString();
        Item soulGemItem = SoulGemUtil.getRandomContractSoulGem(player.level.random);
        int soulGemColor = SoulGemUtil.getSoulGemColor(soulGemItem);

        ItemStack soulGem = new ItemStack(soulGemItem);
        SoulGemUtil.bindSoulGem(soulGem, player.getUUID(), contractId, soulGemColor);

        KarmaData.completeContract(player, contractId, karmaAtContract, soulGemColor);

        if (!player.getInventory().add(soulGem)) {
            player.drop(soulGem, false);
        }

        syncMagic(player, true);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncMagic(player, true);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncMagic(player, true);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack pickedStack = event.getItem().getItem();
        if (!SoulGemUtil.isSoulGem(pickedStack)) {
            return;
        }

        if (SoulGemUtil.hasOwner(pickedStack)) {
            UUID owner = SoulGemUtil.getOwner(pickedStack);
            if (owner == null || !owner.equals(player.getUUID())) {
                event.setCanceled(true);
                return;
            }
        }

        int carriedGems = countSoulGems(player);
        if (carriedGems >= MAX_SOUL_GEMS) {
            event.setCanceled(true);
            return;
        }

        if (!KarmaData.isContracted(player)) {
            return;
        }

        if (KarmaData.isSoulGemSevered(player)) {
            event.setCanceled(true);
            return;
        }

        String contractId = KarmaData.getSoulGemContractId(player);
        if (!SoulGemUtil.isBoundTo(pickedStack, player.getUUID(), contractId)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide()) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        enforceSoulGemLimit(player);

        if (!KarmaData.isContracted(player)) {
            if (player.level.getGameTime() % 40L == 0L) {
                syncMagic(player, false);
            }
            return;
        }

        long gameTime = player.level.getGameTime();
        boolean hasBoundGem = hasBoundSoulGem(player);
        handleBoundSoulGemState(player, gameTime, hasBoundGem);

        int beforeMagic = KarmaData.getMagicCurrent(player);
        processPassiveDrain(player, gameTime);
        processMagicRegeneration(player, gameTime);

        int afterMagic = KarmaData.getMagicCurrent(player);
        boolean changed = beforeMagic != afterMagic;
        if (changed || gameTime % 20L == 0L) {
            syncMagic(player, hasBoundGem);
        }
    }

    private static void handleBoundSoulGemState(ServerPlayer player, long gameTime, boolean hasBoundGem) {
        if (hasBoundGem) {
            if (KarmaData.getSoulGemMissingSince(player) >= 0L) {
                KarmaData.setSoulGemMissingSince(player, -1L);
            }
            return;
        }

        long missingSince = KarmaData.getSoulGemMissingSince(player);
        if (missingSince < 0L) {
            KarmaData.setSoulGemMissingSince(player, gameTime);
            return;
        }

        if (!KarmaData.isSoulGemSevered(player) && gameTime - missingSince >= GEM_GRACE_TICKS) {
            KarmaData.setSoulGemSevered(player, true);
            player.getInventory().clearContent();
            player.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        }
    }

    private static void processPassiveDrain(ServerPlayer player, long gameTime) {
        long lastDrainTick = KarmaData.getLastMagicDrainTick(player);
        if (lastDrainTick <= 0L) {
            KarmaData.setLastMagicDrainTick(player, gameTime);
            return;
        }

        long elapsed = gameTime - lastDrainTick;
        if (elapsed < MAGIC_DRAIN_INTERVAL_TICKS) {
            return;
        }

        long cycles = elapsed / MAGIC_DRAIN_INTERVAL_TICKS;
        int drain = (int) cycles * MAGIC_DRAIN_AMOUNT;
        KarmaData.setMagicCurrent(player, KarmaData.getMagicCurrent(player) - drain);
        KarmaData.setLastMagicDrainTick(player, lastDrainTick + cycles * MAGIC_DRAIN_INTERVAL_TICKS);
    }

    private static void processMagicRegeneration(ServerPlayer player, long gameTime) {
        if (player.getHealth() >= player.getMaxHealth() || KarmaData.getMagicCurrent(player) < MAGIC_PER_HEART) {
            KarmaData.setLastMagicRegenTick(player, gameTime);
            return;
        }

        long lastRegenTick = KarmaData.getLastMagicRegenTick(player);
        if (lastRegenTick <= 0L) {
            KarmaData.setLastMagicRegenTick(player, gameTime);
            return;
        }

        if (gameTime - lastRegenTick < MAGIC_REGEN_INTERVAL_TICKS) {
            return;
        }

        if (KarmaData.consumeMagic(player, MAGIC_PER_HEART)) {
            player.heal(HEART_HEAL_AMOUNT);
        }
        KarmaData.setLastMagicRegenTick(player, gameTime);
    }

    private static void enforceSoulGemLimit(ServerPlayer player) {
        int gemsLeft = MAX_SOUL_GEMS;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!SoulGemUtil.isSoulGem(stack)) {
                continue;
            }

            if (gemsLeft <= 0) {
                player.getInventory().setItem(slot, ItemStack.EMPTY);
                player.drop(stack, false);
                continue;
            }

            int allowed = Math.min(gemsLeft, stack.getCount());
            if (stack.getCount() > allowed) {
                ItemStack overflow = stack.split(stack.getCount() - allowed);
                player.drop(overflow, false);
            }
            gemsLeft -= allowed;
        }
    }

    private static int countSoulGems(ServerPlayer player) {
        int total = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (SoulGemUtil.isSoulGem(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static boolean hasBoundSoulGem(ServerPlayer player) {
        String contractId = KarmaData.getSoulGemContractId(player);
        if (contractId.isEmpty()) {
            return false;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (SoulGemUtil.isBoundTo(stack, player.getUUID(), contractId)) {
                return true;
            }
        }
        return false;
    }

    private static void syncMagic(ServerPlayer player, boolean hasBoundGem) {
        boolean showBar = hasBoundGem && KarmaData.isContracted(player) && !KarmaData.isSoulGemSevered(player);
        MadokraftmagicaMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player),
                new MagicSyncPacket(
                        KarmaData.getMagicCurrent(player),
                        KarmaData.getMagicMax(player),
                        KarmaData.getSoulGemColor(player),
                        showBar));
    }
}
