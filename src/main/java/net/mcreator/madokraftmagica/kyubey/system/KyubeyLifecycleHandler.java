package net.mcreator.madokraftmagica.kyubey.system;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.karma.KarmaData;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import net.mcreator.madokraftmagica.kyubey.init.KyubeyEntities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MadokraftmagicaMod.MODID)
public class KyubeyLifecycleHandler {
    private static final int KARMA_TO_START = 1000;
    private static final int FOLLOW_KARMA = 40000;
    private static final int MIN_DISTANCE_BLOCKS = 64;
    private static final int MIN_SPAWN_RADIUS_BLOCKS = 72;
    private static final int MAX_SPAWN_RADIUS_BLOCKS = 128;

    private static final Map<UUID, Long> NEXT_APPEARANCE_TICK = new HashMap<>();

    private KyubeyLifecycleHandler() {
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            KarmaData.copyForRespawn(event.getOriginal(), event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            KarmaData.addKarma(serverPlayer, 100);
            return;
        }

        if (!(event.getEntity() instanceof KyubeyEntity kyubey)) {
            return;
        }

        UUID ownerUuid = kyubey.getOwnerUuid();
        if (ownerUuid == null) {
            return;
        }

        if (!(kyubey.level instanceof ServerLevel serverLevel)) {
            return;
        }

        ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerUuid);
        if (owner == null || owner.level.dimension() != Level.OVERWORLD) {
            return;
        }

        int karma = KarmaData.getKarma(owner);
        if (karma < KARMA_TO_START) {
            KarmaData.setKyubeyUuid(owner, null);
            return;
        }

        spawnManagedKyubey(owner, kyubey.getHomeBiomeKey());
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        int reward = getHitKarmaReward(event.getTarget());
        if (reward > 0) {
            KarmaData.addKarma(player, reward);
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

        manageKyubeyForPlayer(player);
    }

    private static int getHitKarmaReward(Entity target) {
        if (!(target instanceof Mob mob)) {
            return 0;
        }

        MobCategory category = mob.getType().getCategory();
        if (category == MobCategory.MONSTER) {
            return 2;
        }

        if (category == MobCategory.CREATURE
                || category == MobCategory.AMBIENT
                || category == MobCategory.WATER_CREATURE
                || category == MobCategory.WATER_AMBIENT
                || category == MobCategory.UNDERGROUND_WATER_CREATURE
                || category == MobCategory.AXOLOTLS) {
            return 1;
        }

        return 0;
    }

    private static void manageKyubeyForPlayer(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level;
        long gameTime = level.getGameTime();

        KyubeyEntity managedKyubey = getManagedKyubey(player);

        if (level.dimension() != Level.OVERWORLD) {
            removeManagedKyubey(player, managedKyubey);
            return;
        }

        int karma = KarmaData.getKarma(player);
        if (karma < KARMA_TO_START) {
            removeManagedKyubey(player, managedKyubey);
            return;
        }

        String playerBiomeKey = getBiomeKey(level, player.blockPosition());
        if (playerBiomeKey.isEmpty()) {
            return;
        }

        boolean shouldFollow = karma >= FOLLOW_KARMA;

        if (managedKyubey != null) {
            managedKyubey.setShouldFollowOwner(shouldFollow);
            boolean wrongBiome = !playerBiomeKey.equals(getBiomeKey(level, managedKyubey.blockPosition()))
                    || !playerBiomeKey.equals(managedKyubey.getHomeBiomeKey());
            if (wrongBiome) {
                managedKyubey.discard();
                managedKyubey = null;
            }
        }

        if (managedKyubey == null) {
            spawnManagedKyubey(player, playerBiomeKey);
            return;
        }

        double distanceSqr = managedKyubey.distanceToSqr(player);
        if (distanceSqr <= (double) MIN_DISTANCE_BLOCKS * MIN_DISTANCE_BLOCKS) {
            return;
        }

        long nextTick = NEXT_APPEARANCE_TICK.getOrDefault(player.getUUID(), 0L);
        if (gameTime < nextTick) {
            return;
        }

        BlockPos candidatePos = findSpawnPosition(level, player, playerBiomeKey);
        if (candidatePos == null) {
            NEXT_APPEARANCE_TICK.put(player.getUUID(), gameTime + 200L);
            return;
        }

        managedKyubey.moveTo(candidatePos.getX() + 0.5D, candidatePos.getY(), candidatePos.getZ() + 0.5D,
                managedKyubey.getYRot(), managedKyubey.getXRot());
        managedKyubey.getNavigation().stop();
        managedKyubey.setHomeBiomeKey(playerBiomeKey);
        managedKyubey.setShouldFollowOwner(shouldFollow);
        KarmaData.setKyubeyBiome(player, playerBiomeKey);
        NEXT_APPEARANCE_TICK.put(player.getUUID(), gameTime + getAppearanceIntervalTicks(karma));
    }

    private static KyubeyEntity getManagedKyubey(ServerPlayer player) {
        UUID trackedUuid = KarmaData.getKyubeyUuid(player);
        if (trackedUuid == null) {
            return null;
        }

        if (!(((ServerLevel) player.level).getEntity(trackedUuid) instanceof KyubeyEntity kyubey)) {
            KarmaData.setKyubeyUuid(player, null);
            return null;
        }

        if (!player.getUUID().equals(kyubey.getOwnerUuid())) {
            KarmaData.setKyubeyUuid(player, null);
            return null;
        }

        return kyubey;
    }

    private static void removeManagedKyubey(ServerPlayer player, KyubeyEntity managedKyubey) {
        if (managedKyubey != null) {
            managedKyubey.discard();
        }
        KarmaData.setKyubeyUuid(player, null);
        KarmaData.setKyubeyBiome(player, "");
    }

    private static void spawnManagedKyubey(ServerPlayer player, String requiredBiomeKey) {
        ServerLevel level = (ServerLevel) player.level;
        BlockPos candidatePos = findSpawnPosition(level, player, requiredBiomeKey);
        if (candidatePos == null) {
            NEXT_APPEARANCE_TICK.put(player.getUUID(), level.getGameTime() + 200L);
            return;
        }

        KyubeyEntity kyubey = KyubeyEntities.KYUBEY.get().create(level);
        if (kyubey == null) {
            return;
        }

        kyubey.moveTo(candidatePos.getX() + 0.5D, candidatePos.getY(), candidatePos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F, 0.0F);
        kyubey.setOwnerUuid(player.getUUID());
        kyubey.setHomeBiomeKey(requiredBiomeKey);
        kyubey.setShouldFollowOwner(KarmaData.getKarma(player) >= FOLLOW_KARMA);
        kyubey.finalizeSpawn(level, level.getCurrentDifficultyAt(candidatePos), MobSpawnType.EVENT, null, null);

        if (!level.addFreshEntity(kyubey)) {
            NEXT_APPEARANCE_TICK.put(player.getUUID(), level.getGameTime() + 200L);
            return;
        }

        KarmaData.setKyubeyUuid(player, kyubey.getUUID());
        KarmaData.setKyubeyBiome(player, requiredBiomeKey);
        NEXT_APPEARANCE_TICK.put(player.getUUID(),
                level.getGameTime() + getAppearanceIntervalTicks(KarmaData.getKarma(player)));
    }

    private static BlockPos findSpawnPosition(ServerLevel level, ServerPlayer player, String requiredBiomeKey) {
        BlockPos playerPos = player.blockPosition();

        for (int i = 0; i < 32; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0D;
            int distance = MIN_SPAWN_RADIUS_BLOCKS
                    + level.random.nextInt(MAX_SPAWN_RADIUS_BLOCKS - MIN_SPAWN_RADIUS_BLOCKS + 1);
            int x = playerPos.getX() + (int) Math.round(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int) Math.round(Math.sin(angle) * distance);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            if (!requiredBiomeKey.equals(getBiomeKey(level, pos))) {
                continue;
            }

            if (player.distanceToSqr(x + 0.5D, y, z + 0.5D) <= (double) MIN_DISTANCE_BLOCKS * MIN_DISTANCE_BLOCKS) {
                continue;
            }

            BlockState below = level.getBlockState(pos.below());
            if (!below.isFaceSturdy(level, pos.below(), Direction.UP)) {
                continue;
            }

            if (!level.isEmptyBlock(pos)) {
                continue;
            }

            if (!level.isEmptyBlock(pos.above())) {
                continue;
            }

            return pos;
        }

        return null;
    }

    private static String getBiomeKey(ServerLevel level, BlockPos pos) {
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
        return biomeKey.map(resourceKey -> resourceKey.location().toString()).orElse("");
    }

    private static long getAppearanceIntervalTicks(int karma) {
        if (karma < 10000) {
            return 1200L;
        }

        int steps = ((karma - 10000) / 5000) + 1;
        long interval = 1200L - (steps * 100L);
        return Math.max(200L, interval);
    }
}

