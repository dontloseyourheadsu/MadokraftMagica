package net.mcreator.madokraftmagica.kyubey.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mcreator.madokraftmagica.client.MagicHudState;

import java.util.function.Supplier;

public class MagicSyncPacket {
    private final int magicCurrent;
    private final int magicMax;
    private final int soulGemColor;
    private final boolean showBar;

    public MagicSyncPacket(int magicCurrent, int magicMax, int soulGemColor, boolean showBar) {
        this.magicCurrent = magicCurrent;
        this.magicMax = magicMax;
        this.soulGemColor = soulGemColor;
        this.showBar = showBar;
    }

    public MagicSyncPacket(FriendlyByteBuf buffer) {
        this.magicCurrent = buffer.readInt();
        this.magicMax = buffer.readInt();
        this.soulGemColor = buffer.readInt();
        this.showBar = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.magicCurrent);
        buffer.writeInt(this.magicMax);
        buffer.writeInt(this.soulGemColor);
        buffer.writeBoolean(this.showBar);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> MagicHudState.update(this.magicCurrent, this.magicMax, this.soulGemColor, this.showBar));
        return true;
    }
}

