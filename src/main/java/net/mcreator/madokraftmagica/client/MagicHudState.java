package net.mcreator.madokraftmagica.client;

public final class MagicHudState {
    private static int magicCurrent;
    private static int magicMax;
    private static int soulGemColor;
    private static boolean showBar;

    private MagicHudState() {
    }

    public static void update(int current, int max, int color, boolean shouldShow) {
        magicCurrent = Math.max(0, current);
        magicMax = Math.max(0, max);
        soulGemColor = color;
        showBar = shouldShow;
    }

    public static int getMagicCurrent() {
        return magicCurrent;
    }

    public static int getMagicMax() {
        return magicMax;
    }

    public static int getSoulGemColor() {
        return soulGemColor;
    }

    public static boolean shouldShowBar() {
        return showBar && magicMax > 0;
    }
}

