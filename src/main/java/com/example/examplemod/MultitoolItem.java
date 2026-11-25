package com.example.examplemod;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

public class MultitoolItem extends Item {

    public final int hitDistance;
    private final int useDurationTicks;
    private final int ticksBeforeOverheat;

    public MultitoolItem(Properties properties) {
        super(properties);

        this.useDurationTicks = 72000;

        this.hitDistance = 32;
        int useSeconds = 5;
        this.ticksBeforeOverheat = useSeconds * 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.useDurationTicks;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        itemstack.set(ExampleMod.LASER_HIT, this.getNearestHit(player).getLocation().toVector3f());

        float heatPenalty = this.getExhaustion(itemstack, level.getGameTime(), 0f); // Didn't wait for cooldown to finish
        int tickPenalty = Mth.ceil(Math.clamp(heatPenalty, 0L, 1L) * this.ticksBeforeOverheat);

        itemstack.set(ExampleMod.LASER_START_TIME, level.getGameTime() - tickPenalty);
        itemstack.remove(ExampleMod.LASER_COOLDOWN);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity wielder, ItemStack stack, int remainingUseDuration) {
        if (this.getExhaustion(stack, level.getGameTime(), 0f) >= 1) {
            if (!level.isClientSide) stack.remove(ExampleMod.LASER_HIT);
            return;
        }

        HitResult nearestHit = this.getNearestHit(wielder);

        if (nearestHit.getType() == HitResult.Type.MISS) return;

        Vec3 hitLocation = nearestHit.getLocation();

        if (level.isClientSide) {
            BlockState blockParticle = nearestHit instanceof BlockHitResult blockHitResult ? level.getBlockState(blockHitResult.getBlockPos()) : Blocks.RED_STAINED_GLASS.defaultBlockState();

            Vec3 particlePos = hitLocation.add(wielder.getLookAngle().scale(-0.0125f));
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockParticle), particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
        } else {
            Vector3f laserHitPos = hitLocation.toVector3f();
            if (!laserHitPos.equals(stack.get(ExampleMod.LASER_HIT))) {
                stack.set(ExampleMod.LASER_HIT, laserHitPos);
            }

            if (nearestHit instanceof BlockHitResult blockHitResult) {
                level.destroyBlock(blockHitResult.getBlockPos(), true, wielder);
            }
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int useDurationTicks) {
        stack.set(ExampleMod.LASER_COOLDOWN, new LaserCooldown(stack, entity.level()));

        stack.remove(ExampleMod.LASER_HIT);
        stack.remove(ExampleMod.LASER_START_TIME);
    }

    private HitResult getNearestHit(LivingEntity wielder) {
        HitResult levelPick = wielder.pick(hitDistance, 0, false);

        Vec3 eyePosition = wielder.getEyePosition();
        Vec3 stopPos = wielder.getLookAngle().scale(hitDistance).add(eyePosition);
        AABB scanBox = new AABB(eyePosition, stopPos);

        EntityHitResult entityCastResult = ProjectileUtil.getEntityHitResult(wielder, eyePosition, stopPos, scanBox, e -> !e.isSpectator() && e.isPickable(), hitDistance);
        if (entityCastResult == null) {
            return levelPick;
        }

        double blockRayLength = levelPick.getLocation().subtract(eyePosition).length();
        double entityRayLength = entityCastResult.getLocation().subtract(eyePosition).length();

        return entityRayLength < blockRayLength ? entityCastResult : levelPick;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
    }

    public float getExhaustion(ItemStack stack, long gameTime, float deltaTime) {
        if (stack.has(ExampleMod.LASER_COOLDOWN)) {
            LaserCooldown laserCooldown = stack.get(ExampleMod.LASER_COOLDOWN);
            long timeSinceCoolingStart = gameTime - laserCooldown.cooldownStart();
            float percentCooled = (timeSinceCoolingStart - deltaTime) / this.ticksBeforeOverheat;
            float ret = laserCooldown.initialHeatLevel() - percentCooled;
            return Mth.clamp(ret, 0, 1);
        }

        if (!stack.has(ExampleMod.LASER_START_TIME)) return 0;

        long timeSinceLaserStart = gameTime - stack.getOrDefault(ExampleMod.LASER_START_TIME, gameTime);
        return Mth.clamp((timeSinceLaserStart + deltaTime) / this.ticksBeforeOverheat, 0, 1);
    }

    public static float getExhaustionStatic(ItemStack stack, long gameTime, float deltaTime) {
        return stack.getItem() instanceof MultitoolItem item ? item.getExhaustion(stack, gameTime, deltaTime) : 0f;
    }
}
