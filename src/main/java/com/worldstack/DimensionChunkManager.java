package com.worldstack;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = "worldstack")
public class DimensionChunkManager {

    private static final int UPDATE_INTERVAL = 100;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        if (!(event.player instanceof ServerPlayer player)) return;

        if (player.tickCount % UPDATE_INTERVAL != 0) return;

        ServerLevel current = player.getLevel();

        if (current.dimension() != Level.OVERWORLD) return;

        ServerLevel nether = player.server.getLevel(Level.NETHER);
        ServerLevel end = player.server.getLevel(Level.END);

        if (nether == null || end == null) return;

        int chunkX = player.blockPosition().getX() >> 4;
        int chunkZ = player.blockPosition().getZ() >> 4;

        LevelChunk netherChunk = nether.getChunk(chunkX, chunkZ);
        LevelChunk endChunk = end.getChunk(chunkX, chunkZ);

        int x = player.blockPosition().getX();
        int z = player.blockPosition().getZ();

        BlockPos netherPos = new BlockPos(x, 64, z);
        BlockPos endPos = new BlockPos(x, 64, z);

        int netherBlock = Block.getId(
            netherChunk.getBlockState(netherPos)
        );

        int endBlock = Block.getId(
            endChunk.getBlockState(endPos)
        );

        WorldStackNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new WorldStackPacket(netherBlock, endBlock)
        );
    }
}
