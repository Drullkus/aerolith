package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class MultiToolRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ModelResourceLocation MATTER_MANIPULATOR_MODEL = ModelResourceLocation.standalone(ExampleMod.modid("item/matter_manipulator"));
    private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    private static final Set<ItemDisplayContext> ALLOWED_TRANSFORMS = Set.of(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);

    public MultiToolRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(MATTER_MANIPULATOR_MODEL);
        Minecraft.getInstance().getItemRenderer().render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, model);
        poseStack.popPose();

        if (!ALLOWED_TRANSFORMS.contains(displayContext)) return;
        if (!stack.has(ExampleMod.LASER_HIT)) return;

        poseStack.pushPose();

        poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
        poseStack.translate(0, -1/4d, -3/32d);

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
                0.05F,
                0.0625F
        );

        poseStack.popPose();
    }
}
