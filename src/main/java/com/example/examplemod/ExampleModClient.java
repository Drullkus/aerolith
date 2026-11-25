package com.example.examplemod;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.io.IOException;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = ExampleMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {

    private static ShaderInstance matterManipulatorAdditionalLayer;

    private static final ResourceLocation MATTER_MANIPULATOR_RL = ExampleMod.modid("multitool/matter_manipulator");

    public static final RenderType MATTER_MANIPULATOR_RENDER_TYPE = RenderType.create(
            MATTER_MANIPULATOR_RL.toString(),
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> matterManipulatorAdditionalLayer))
                    .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
                    .setTransparencyState(RenderType.NO_TRANSPARENCY)
                    .setLightmapState(RenderType.LIGHTMAP)
                    .setOverlayState(RenderType.OVERLAY)
                    .createCompositeState(true)
    );

    public ExampleModClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new MultitoolClientExtensions(), ExampleMod.EXAMPLE_ITEM);
    }

    @SubscribeEvent
    static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(MultiToolRenderer.MATTER_MANIPULATOR_MODEL);
    }

    @SubscribeEvent
    static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), MATTER_MANIPULATOR_RL, DefaultVertexFormat.NEW_ENTITY) {

                @Override
                public void apply() {
                    super.apply();
                }
            }, initializedShader -> matterManipulatorAdditionalLayer = initializedShader);
        } catch (IOException e) {
            ExampleMod.LOGGER.error("Exception while registering shaders", e);
        }
    }

    @SubscribeEvent
    static void registerNamedRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register(MATTER_MANIPULATOR_RL, RenderType.CUTOUT, MATTER_MANIPULATOR_RENDER_TYPE);
    }

    public static void setLaserHeatUniform(float laserHeat) {
        matterManipulatorAdditionalLayer.getUniform("LaserHeat").set(1f - laserHeat);
    }

}
