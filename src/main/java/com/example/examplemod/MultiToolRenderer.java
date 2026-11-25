package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MultiToolRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    public MultiToolRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        poseStack.translate(0, -10, 0);

        poseStack.pushPose();
        BeaconRenderer.renderBeaconBeam(
                poseStack,
                buffer,
                BEAM_LOCATION,
                Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(),
                1,
                Minecraft.getInstance().level.getGameTime(),
                0,
                128,
                0xff_00_00,
                0.2F,
                0.25F
        );
        poseStack.popPose();

        poseStack.popPose();
    }
}
