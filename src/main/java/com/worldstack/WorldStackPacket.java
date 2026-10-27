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
            buffer.readVarIntArray(4096),
            buffer.readVarIntArray(4096)
        );
    }

    public static void handle(
        WorldStackPacket packet,
        Supplier<NetworkEvent.Context> context
    ) {
        NetworkEvent.Context ctx = context.get();

        ctx.enqueueWork(() -> {

            if (packet.netherBlocks.length == 0) {

                ClientDimensionData.clear();

                return;
            }

            if (packet.netherBlocks.length != 4096) {
                LOGGER.warn(
                    "[WorldStack] Invalid Nether data length: {}",
                    packet.netherBlocks.length
                );
                return;
            }

            ClientDimensionData.update(
                packet.baseX,
                packet.baseZ,
                packet.netherBlocks,
                packet.endBlocks
            );

            LOGGER.info(
                "[WorldStack] Alpha 8: Nether blocks received: {}",
                packet.netherBlocks.length
            );
        });

        ctx.setPacketHandled(true);
    }
}
