package com.worldstack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WorldStackPacket {

    private final int netherBlock;
    private final int endBlock;

    public WorldStackPacket(int netherBlock, int endBlock) {
        this.netherBlock = netherBlock;
        this.endBlock = endBlock;
    }

    public static void encode(
        WorldStackPacket packet,
        FriendlyByteBuf buffer
    ) {
        buffer.writeInt(packet.netherBlock);
        buffer.writeInt(packet.endBlock);
    }

    public static WorldStackPacket decode(FriendlyByteBuf buffer) {
        return new WorldStackPacket(
            buffer.readInt(),
            buffer.readInt()
        );
    }

    public static void handle(
        WorldStackPacket packet,
        Supplier<NetworkEvent.Context> context
    ) {
        context.get().enqueueWork(() -> {

            System.out.println(
                "[WorldStack] Received dimension data: Nether="
                + packet.netherBlock
                + ", End="
                + packet.endBlock
            );

        });

        context.get().setPacketHandled(true);
    }
}
