package com.worldstack;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "worldstack")
public class DimensionChunkManager {

    private static final int UPDATE_INTERVAL = 100;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        if (!(event.player instanceof ServerPlayer player)) return;

        // Проверяем раз в 5 секунд
        if (player.tickCount % UPDATE_INTERVAL != 0) return;

        ServerLevel current = player.getLevel();

        // Пока проверяем только обычный мир
        if (current.dimension() != Level.OVERWORLD) return;

        ServerLevel nether = player.server.getLevel(Level.NETHER);
        ServerLevel end = player.server.getLevel(Level.END);

        if (nether == null || end == null) return;

        int chunkX = player.blockPosition().getX() >> 4;
        int chunkZ = player.blockPosition().getZ() >> 4;

        // Получаем чанк Незера
        LevelChunk netherChunk = nether.getChunk(chunkX, chunkZ);

        // Получаем чанк Энда
        LevelChunk endChunk = end.getChunk(chunkX, chunkZ);

        // Проверка загрузки
        if (netherChunk != null && endChunk != null) {

            System.out.println(
                "[WorldStack] Neighbor chunks loaded at "
                + chunkX + ", " + chunkZ
            );
        }
    }
}
