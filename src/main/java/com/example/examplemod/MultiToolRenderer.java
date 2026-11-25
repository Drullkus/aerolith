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

import java.util.Set;

public class MultiToolRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    private static final Set<ItemDisplayContext> ALLOWED_TRANSFORMS = Set.of(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);

    public MultiToolRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!ALLOWED_TRANSFORMS.contains(displayContext)) return;
        if (!stack.getOrDefault(ExampleMod.LASER_ACTIVE, false)) return;

        poseStack.pushPose();

        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        poseStack.translate(0, -10, 0);

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
    }
}
