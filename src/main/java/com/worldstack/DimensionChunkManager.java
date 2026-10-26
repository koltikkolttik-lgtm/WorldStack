package com.worldstack;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = "worldstack")
public class DimensionChunkManager {

    private static final int SIZE = 8;
    private static final int UPDATE_INTERVAL = 100;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (player.tickCount % UPDATE_INTERVAL != 0) return;

        if (player.getLevel().dimension() != Level.OVERWORLD) return;

        ServerLevel nether = player.server.getLevel(Level.NETHER);
        ServerLevel end = player.server.getLevel(Level.END);

        if (nether == null || end == null) return;

        int[] netherBlocks = new int[SIZE * SIZE * SIZE];
        int[] endBlocks = new int[SIZE * SIZE * SIZE];

        int baseX = player.blockPosition().getX();
        int baseZ = player.blockPosition().getZ();

        int index = 0;

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {

                    BlockPos netherPos = new BlockPos(
                        baseX + x,
                        60 + y,
                        baseZ + z
                    );

                    BlockPos endPos = new BlockPos(
                        baseX + x,
                        60 + y,
                        baseZ + z
                    );

                    netherBlocks[index] = Block.getId(
                        nether.getBlockState(netherPos)
                    );

                    endBlocks[index] = Block.getId(
                        end.getBlockState(endPos)
                    );

                    index++;
                }
            }
        }

        WorldStackNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new WorldStackPacket(netherBlocks, endBlocks)
        );
    }
}
