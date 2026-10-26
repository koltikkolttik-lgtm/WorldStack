package com.worldstack;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class WorldStackPacket {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final int baseX;
    private final int baseZ;

    private final int[] netherBlocks;
    private final int[] endBlocks;

    public WorldStackPacket(
        int baseX,
        int baseZ,
        int[] netherBlocks,
        int[] endBlocks
    ) {
        this.baseX = baseX;
        this.baseZ = baseZ;
        this.netherBlocks = netherBlocks;
        this.endBlocks = endBlocks;
    }

    public static void encode(
        WorldStackPacket packet,
        FriendlyByteBuf buffer
    ) {
        buffer.writeInt(packet.baseX);
        buffer.writeInt(packet.baseZ);

        buffer.writeVarIntArray(packet.netherBlocks);
        buffer.writeVarIntArray(packet.endBlocks);
    }

    public static WorldStackPacket decode(FriendlyByteBuf buffer) {
        return new WorldStackPacket(
            buffer.readInt(),
            buffer.readInt(),
            buffer.readVarIntArray(512),
            buffer.readVarIntArray(512)
        );
    }

    public static void handle(
        WorldStackPacket packet,
        Supplier<NetworkEvent.Context> context
    ) {
        context.get().enqueueWork(() -> {

            ClientDimensionData.update(
                packet.baseX,
                packet.baseZ,
                packet.netherBlocks,
                packet.endBlocks
            );

            LOGGER.info(
                "[WorldStack] Client data updated: X={}, Z={}, Nether={}, End={}",
                packet.baseX,
                packet.baseZ,
                packet.netherBlocks.length,
                packet.endBlocks.length
            );
        });

        context.get().setPacketHandled(true);
    }
}
