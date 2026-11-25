package com.example.examplemod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public record LaserCooldown(long cooldownStart, float initialHeatLevel) {

    public LaserCooldown(ItemStack stack, @Nullable Level level) {
        this(stack, level == null ? 0 : level.getGameTime());
    }

    public LaserCooldown(ItemStack stack, long gameTime) {
        this(gameTime, MultitoolItem.getExhaustionStatic(stack, gameTime, 0));
    }

    public static final Codec<LaserCooldown> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Util.longRange(0, Long.MAX_VALUE).fieldOf("cooldown_start").forGetter(LaserCooldown::cooldownStart),
            Codec.floatRange(0, 1).fieldOf("initial_heat_level").forGetter(LaserCooldown::initialHeatLevel)
    ).apply(inst, LaserCooldown::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LaserCooldown> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, LaserCooldown::cooldownStart,
            ByteBufCodecs.FLOAT, LaserCooldown::initialHeatLevel,
            LaserCooldown::new
    );

}
