package com.worldstack;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class WorldStackNetwork {

    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL =
        NetworkRegistry.newSimpleChannel(
            new ResourceLocation("worldstack", "main"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals
        );

    public static void register() {
        CHANNEL.registerMessage(
            0,
            WorldStackPacket.class,
            WorldStackPacket::encode,
            WorldStackPacket::decode,
            WorldStackPacket::handle
        );
    }
}
