package com.worldstack;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("worldstack")
public class WorldStack {

    private static final String COOLDOWN = "WorldStackCooldown";

    public WorldStack() {
        MinecraftForge.EVENT_BUS.register(this);
        WorldStackNetwork.register();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        // Защита от частых переходов
        var data = player.getPersistentData();

        int cooldown = data.getInt(COOLDOWN);

        if (cooldown > 0) {
            data.putInt(COOLDOWN, cooldown - 1);
            return;
        }

        ServerLevel current = player.getLevel();
        ResourceKey<Level> dimension = current.dimension();

        double y = player.getY();

        ResourceKey<Level> target = null;
        int targetY = 80;

        // ОБЫЧНЫЙ МИР
        if (dimension == Level.OVERWORLD) {

            if (y < -120) {
                target = Level.NETHER;
                targetY = 105;
            }

            else if (y > 318) {
                target = Level.END;
                targetY = 80;
            }
        }

        // НЕЗЕР
        else if (dimension == Level.NETHER) {

            if (y > 120) {
                target = Level.OVERWORLD;
                targetY = -50;
            }
        }

        // ЭНД
        else if (dimension == Level.END) {

            if (y < 0) {
                target = Level.OVERWORLD;
                targetY = 300;
            }
        }

        if (target == null) return;

        ServerLevel destination = player.server.getLevel(target);

        if (destination == null) return;

        double x = player.getX();
        double z = player.getZ();

        BlockPos pos = new BlockPos(x, targetY, z);

        // Создаём безопасную площадку 3x3
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {

                destination.setBlock(
                    pos.offset(dx, -1, dz),
                    Blocks.STONE.defaultBlockState(),
                    3
                );

                // Освобождаем место для игрока
                for (int dy = 0; dy <= 2; dy++) {

                    destination.setBlock(
                        pos.offset(dx, dy, dz),
                        Blocks.AIR.defaultBlockState(),
                        3
                    );
                }
            }
        }

        // Задержка 5 секунд
        data.putInt(COOLDOWN, 100);

        // Телепортация
        player.teleportTo(
            destination,
            x,
            targetY,
            z,
            player.getYRot(),
            player.getXRot()
        );
    }
}
