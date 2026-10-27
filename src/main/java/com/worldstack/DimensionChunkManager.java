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

    private static final int SIZE = 16;
    private static final int UPDATE_INTERVAL = 100;

    private static final int NETHER_ACTIVATION_Y = -40;
    private static final int NETHER_SOURCE_Y = 60;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        if (!(event.player instanceof ServerPlayer player)) return;

        if (player.tickCount % UPDATE_INTERVAL != 0) return;

        if (player.getLevel().dimension() != Level.OVERWORLD) {
            return;
        }

        // Не загружаем Незер, если игрок далеко
        if (player.getY() >= NETHER_ACTIVATION_Y) {

            WorldStackNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new WorldStackPacket(
                    0,
                    0,
                    new int[0],
                    new int[0]
                )
            );

            return;
        }

        ServerLevel nether = player.server.getLevel(Level.NETHER);

        if (nether == null) return;

        int[] netherBlocks = new int[SIZE * SIZE * SIZE];

        int baseX = player.blockPosition().getX() - SIZE / 2;
        int baseZ = player.blockPosition().getZ() - SIZE / 2;

        int index = 0;

        BlockPos.MutableBlockPos pos =
            new BlockPos.MutableBlockPos();

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {

                    pos.set(
                        baseX + x,
                        NETHER_SOURCE_Y + y,
                        baseZ + z
                    );

                    netherBlocks[index++] = Block.getId(
                        nether.getBlockState(pos)
                    );
                }
            }
        }

        WorldStackNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new WorldStackPacket(
                baseX,
                baseZ,
                netherBlocks,
                new int[0]
            )
        );
    }
}
