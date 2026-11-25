package com.example.examplemod;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class MultitoolClientExtensions implements IClientItemExtensions {

    private final BlockEntityWithoutLevelRenderer renderer = new MultiToolRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }
}
