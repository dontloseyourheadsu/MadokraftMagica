package net.mcreator.madokraftmagica.karma;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class KarmaData {
    private static final String ROOT_KEY = "MadokraftMagica";
    private static final String KARMA_KEY = "Karma";
    private static final String KYUBEY_UUID_KEY = "KyubeyUuid";
    private static final String KYUBEY_BIOME_KEY = "KyubeyBiome";

    public static final int MAX_KARMA = 50000;

    private KarmaData() {
    }

    public static int getKarma(Player player) {
        CompoundTag data = getData(player);
        return Math.max(0, Math.min(MAX_KARMA, data.getInt(KARMA_KEY)));
    }

    public static void addKarma(Player player, int amount) {
        if (amount <= 0) {
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
