package net.mcreator.madokraftmagica.karma;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class KarmaData {
    private static final String ROOT_KEY = "MadokraftMagica";
    private static final String KARMA_KEY = "Karma";
    private static final String KYUBEY_UUID_KEY = "KyubeyUuid";
    private static final String KYUBEY_BIOME_KEY = "KyubeyBiome";

    private static final String CONTRACTED_KEY = "Contracted";
    private static final String SOUL_GEM_ID_KEY = "SoulGemContractId";
    private static final String SOUL_GEM_MISSING_SINCE_KEY = "SoulGemMissingSince";
    private static final String SOUL_GEM_SEVERED_KEY = "SoulGemSevered";
    private static final String MAGIC_CURRENT_KEY = "MagicCurrent";
    private static final String MAGIC_MAX_KEY = "MagicMax";
    private static final String SOUL_GEM_COLOR_KEY = "SoulGemColor";
    private static final String LAST_MAGIC_DRAIN_TICK_KEY = "MagicDrainTick";
    private static final String LAST_MAGIC_REGEN_TICK_KEY = "MagicRegenTick";
    private static final String FULL_REGEN_READY_KEY = "FullRegenReady";

    public static final int MAX_KARMA = 50000;

    private KarmaData() {
    }

    public static int getKarma(Player player) {
        CompoundTag data = getData(player);
        return Math.max(0, Math.min(MAX_KARMA, data.getInt(KARMA_KEY)));
    }

    public static void addKarma(Player player, int amount) {
        if (amount <= 0 || isContracted(player)) {
            return;
        }
        setKarma(player, getKarma(player) + amount);
    }

    public static void setKarma(Player player, int karma) {
        getData(player).putInt(KARMA_KEY, Math.max(0, Math.min(MAX_KARMA, karma)));
    }

    public static UUID getKyubeyUuid(Player player) {
        CompoundTag data = getData(player);
        return data.hasUUID(KYUBEY_UUID_KEY) ? data.getUUID(KYUBEY_UUID_KEY) : null;
    }

    public static void setKyubeyUuid(Player player, UUID kyubeyUuid) {
        CompoundTag data = getData(player);
        if (kyubeyUuid == null) {
            data.remove(KYUBEY_UUID_KEY);
            return;
        }
        data.putUUID(KYUBEY_UUID_KEY, kyubeyUuid);
    }

    public static String getKyubeyBiome(Player player) {
        return getData(player).getString(KYUBEY_BIOME_KEY);
    }

    public static void setKyubeyBiome(Player player, String biomeKey) {
        getData(player).putString(KYUBEY_BIOME_KEY, biomeKey == null ? "" : biomeKey);
    }

    public static boolean isContracted(Player player) {
        return getData(player).getBoolean(CONTRACTED_KEY);
    }

    public static void completeContract(Player player, String soulGemContractId, int initialMagic, int soulGemColor) {
        CompoundTag data = getData(player);
        int clampedMagic = Math.max(0, Math.min(MAX_KARMA, initialMagic));
        data.putBoolean(CONTRACTED_KEY, true);
        data.putString(SOUL_GEM_ID_KEY, soulGemContractId == null ? "" : soulGemContractId);
        data.putLong(SOUL_GEM_MISSING_SINCE_KEY, -1L);
        data.putBoolean(SOUL_GEM_SEVERED_KEY, false);
        data.putInt(MAGIC_MAX_KEY, clampedMagic);
        data.putInt(MAGIC_CURRENT_KEY, clampedMagic);
        data.putInt(SOUL_GEM_COLOR_KEY, soulGemColor);
        data.putLong(LAST_MAGIC_DRAIN_TICK_KEY, 0L);
        data.putLong(LAST_MAGIC_REGEN_TICK_KEY, 0L);
        data.putInt(KARMA_KEY, 0);
    }

    public static String getSoulGemContractId(Player player) {
        return getData(player).getString(SOUL_GEM_ID_KEY);
    }

    public static void setSoulGemContractId(Player player, String soulGemContractId) {
        getData(player).putString(SOUL_GEM_ID_KEY, soulGemContractId == null ? "" : soulGemContractId);
    }

    public static long getSoulGemMissingSince(Player player) {
        CompoundTag data = getData(player);
        return data.contains(SOUL_GEM_MISSING_SINCE_KEY) ? data.getLong(SOUL_GEM_MISSING_SINCE_KEY) : -1L;
    }

    public static void setSoulGemMissingSince(Player player, long gameTick) {
        getData(player).putLong(SOUL_GEM_MISSING_SINCE_KEY, gameTick);
    }

    public static boolean isSoulGemSevered(Player player) {
        return getData(player).getBoolean(SOUL_GEM_SEVERED_KEY);
    }

    public static void setSoulGemSevered(Player player, boolean severed) {
        getData(player).putBoolean(SOUL_GEM_SEVERED_KEY, severed);
    }

    public static int getMagicCurrent(Player player) {
        CompoundTag data = getData(player);
        int max = getMagicMax(player);
        return Math.max(0, Math.min(max, data.getInt(MAGIC_CURRENT_KEY)));
    }

    public static int getMagicMax(Player player) {
        CompoundTag data = getData(player);
        return Math.max(0, Math.min(MAX_KARMA, data.getInt(MAGIC_MAX_KEY)));
    }

    public static void setMagicCurrent(Player player, int amount) {
        int max = getMagicMax(player);
        getData(player).putInt(MAGIC_CURRENT_KEY, Math.max(0, Math.min(max, amount)));
    }

    public static boolean consumeMagic(Player player, int amount) {
        if (amount <= 0) {
            return true;
        }
        int current = getMagicCurrent(player);
        if (current < amount) {
            return false;
        }
        setMagicCurrent(player, current - amount);
        return true;
    }

    public static int getSoulGemColor(Player player) {
        return getData(player).getInt(SOUL_GEM_COLOR_KEY);
    }

    public static void setSoulGemColor(Player player, int argbColor) {
        getData(player).putInt(SOUL_GEM_COLOR_KEY, argbColor);
    }

    public static long getLastMagicDrainTick(Player player) {
        return getData(player).getLong(LAST_MAGIC_DRAIN_TICK_KEY);
    }

    public static void setLastMagicDrainTick(Player player, long tick) {
        getData(player).putLong(LAST_MAGIC_DRAIN_TICK_KEY, tick);
    }

    public static long getLastMagicRegenTick(Player player) {
        return getData(player).getLong(LAST_MAGIC_REGEN_TICK_KEY);
    }

    public static void setLastMagicRegenTick(Player player, long tick) {
        getData(player).putLong(LAST_MAGIC_REGEN_TICK_KEY, tick);
    }

    public static boolean isFullRegenReady(Player player) {
        CompoundTag data = getData(player);
        if (!data.contains(FULL_REGEN_READY_KEY)) {
            return true;
        }
        return data.getBoolean(FULL_REGEN_READY_KEY);
    }

    public static void setFullRegenReady(Player player, boolean ready) {
        getData(player).putBoolean(FULL_REGEN_READY_KEY, ready);
    }

    public static int getDestinyLevel(Player player) {
        if (isContracted(player)) {
            return getMagicMax(player);
        }
        return getKarma(player);
    }

    public static void copyForRespawn(Player from, Player to) {
        CompoundTag fromTag = from.getPersistentData().getCompound(ROOT_KEY);
        to.getPersistentData().put(ROOT_KEY, fromTag.copy());
    }

    private static CompoundTag getData(Player player) {
        CompoundTag persisted = player.getPersistentData();
        if (!persisted.contains(ROOT_KEY, 10)) {
            persisted.put(ROOT_KEY, new CompoundTag());
        }
        return persisted.getCompound(ROOT_KEY);
    }
}
