package net.mcreator.madokraftmagica.gems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.mcreator.madokraftmagica.gems.init.GemItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SoulGemUtil {
    public static final String OWNER_TAG = "SoulGemOwner";
    public static final String CONTRACT_ID_TAG = "SoulGemContractId";
    public static final String COLOR_TAG = "SoulGemColor";

    private static final List<Item> SOUL_GEM_ITEMS = new ArrayList<>();

    private SoulGemUtil() {
    }

    public static boolean isSoulGem(ItemStack stack) {
        return !stack.isEmpty() && isSoulGemItem(stack.getItem());
    }

    public static boolean isSoulGemItem(Item item) {
        if (SOUL_GEM_ITEMS.isEmpty()) {
            SOUL_GEM_ITEMS.add(GemItems.SOUL_GEM.get());
            SOUL_GEM_ITEMS.add(GemItems.RED_SOUL_GEM.get());
            SOUL_GEM_ITEMS.add(GemItems.PINK_SOUL_GEM.get());
            SOUL_GEM_ITEMS.add(GemItems.BLUE_SOUL_GEM.get());
            SOUL_GEM_ITEMS.add(GemItems.YELLOW_SOUL_GEM.get());
            SOUL_GEM_ITEMS.add(GemItems.GREEN_SOUL_GEM.get());
            SOUL_GEM_ITEMS.add(GemItems.PURPLE_SOUL_GEM.get());
        }
        return SOUL_GEM_ITEMS.contains(item);
    }

    public static Item getRandomContractSoulGem(RandomSource random) {
        Item[] contractGems = new Item[] {
                GemItems.RED_SOUL_GEM.get(),
                GemItems.PINK_SOUL_GEM.get(),
                GemItems.BLUE_SOUL_GEM.get(),
                GemItems.YELLOW_SOUL_GEM.get(),
                GemItems.GREEN_SOUL_GEM.get(),
                GemItems.PURPLE_SOUL_GEM.get()
        };
        return contractGems[random.nextInt(contractGems.length)];
    }

    public static int getSoulGemColor(Item item) {
        if (item == GemItems.RED_SOUL_GEM.get()) {
            return 0xFFD54848;
        }
        if (item == GemItems.PINK_SOUL_GEM.get()) {
            return 0xFFE67AC9;
        }
        if (item == GemItems.BLUE_SOUL_GEM.get()) {
            return 0xFF4E84D8;
        }
        if (item == GemItems.YELLOW_SOUL_GEM.get()) {
            return 0xFFE5C24A;
        }
        if (item == GemItems.GREEN_SOUL_GEM.get()) {
            return 0xFF59BE74;
        }
        if (item == GemItems.PURPLE_SOUL_GEM.get()) {
            return 0xFF9463D8;
        }
        return 0xFFB8B8C2;
    }

    public static void bindSoulGem(ItemStack soulGem, UUID owner, String contractId, int color) {
        CompoundTag tag = soulGem.getOrCreateTag();
        tag.putUUID(OWNER_TAG, owner);
        tag.putString(CONTRACT_ID_TAG, contractId);
        tag.putInt(COLOR_TAG, color);
    }

    public static boolean isBoundTo(ItemStack soulGem, UUID playerUuid, String requiredContractId) {
        if (!isSoulGem(soulGem) || !soulGem.hasTag()) {
            return false;
        }

        CompoundTag tag = soulGem.getTag();
        if (tag == null || !tag.hasUUID(OWNER_TAG)) {
            return false;
        }

        if (!playerUuid.equals(tag.getUUID(OWNER_TAG))) {
            return false;
        }

        String gemContractId = tag.getString(CONTRACT_ID_TAG);
        return !requiredContractId.isEmpty() && requiredContractId.equals(gemContractId);
    }

    public static boolean hasOwner(ItemStack soulGem) {
        return soulGem.hasTag() && soulGem.getTag() != null && soulGem.getTag().hasUUID(OWNER_TAG);
    }

    @Nullable
    public static UUID getOwner(ItemStack soulGem) {
        if (!hasOwner(soulGem)) {
            return null;
        }
        CompoundTag tag = soulGem.getTag();
        return tag != null ? tag.getUUID(OWNER_TAG) : null;
    }

    public static int getStoredColor(ItemStack soulGem) {
        if (!soulGem.hasTag() || soulGem.getTag() == null) {
            return 0xFFB8B8C2;
        }
        return soulGem.getTag().contains(COLOR_TAG) ? soulGem.getTag().getInt(COLOR_TAG) : 0xFFB8B8C2;
    }
}

