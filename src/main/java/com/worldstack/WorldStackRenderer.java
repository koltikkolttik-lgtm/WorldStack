package com.worldstack;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = "worldstack",
    value = Dist.CLIENT,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class WorldStackRenderer {

    private static final int SIZE = 16;

    private static final int RENDER_Y = -96;

    private static final int ACTIVATION_Y = -40;

    private static final int FULL_BRIGHT = 15728880;

    @SubscribeEvent
    public static void renderWorld(RenderLevelStageEvent event) {

        if (event.getStage() !=
            RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) {
            ClientDimensionData.clear();
            return;
        }

        if (mc.level.dimension() != Level.OVERWORLD) {
            ClientDimensionData.clear();
            return;
        }

        // Показываем Незер только возле нижней границы
        if (mc.player.getY() >= ACTIVATION_Y) {
            return;
        }

        int[] blocks = ClientDimensionData.getNetherBlocks();

        if (blocks.length != SIZE * SIZE * SIZE) {
            return;
        }

        int baseX = ClientDimensionData.getBaseX();
        int baseZ = ClientDimensionData.getBaseZ();

        Camera camera = event.getCamera();

        double cameraX = camera.getPosition().x;
        double cameraY = camera.getPosition().y;
        double cameraZ = camera.getPosition().z;

        PoseStack poseStack = event.getPoseStack();

        BlockRenderDispatcher renderer =
            mc.getBlockRenderer();

        MultiBufferSource.BufferSource buffers =
            mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        poseStack.translate(
            baseX - cameraX,
            RENDER_Y - cameraY,
            baseZ - cameraZ
        );

        int index = 0;

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {

                    int blockId = blocks[index++];

                    BlockState state = Block.stateById(blockId);

                    if (state.isAir()) {
                        continue;
                    }

                    poseStack.pushPose();

                    poseStack.translate(x, y, z);

                    renderer.renderSingleBlock(
                        state,
                        poseStack,
                        buffers,
                        FULL_BRIGHT,
                        0
                    );

                    poseStack.popPose();
                }
            }
        }

        poseStack.popPose();

        buffers.endBatch();
    }
}
