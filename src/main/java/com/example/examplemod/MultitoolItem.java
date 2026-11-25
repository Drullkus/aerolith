package com.example.examplemod;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class MultitoolItem extends Item {

    private final int hitDistance;
    private final int useDurationTicks;

    public MultitoolItem(Properties properties) {
        super(properties);

        this.hitDistance = 32;
        this.useDurationTicks = 72000;
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
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity wielder, ItemStack stack, int remainingUseDuration) {
        HitResult nearestHit = this.getNearestHit(wielder);

        if (nearestHit.getType() == HitResult.Type.MISS) {
            return;
        }

        Vec3 particlePos = nearestHit.getLocation().add(wielder.getLookAngle().scale(-0.0125f));

        BlockState blockParticle = nearestHit instanceof BlockHitResult blockHitResult ? level.getBlockState(blockHitResult.getBlockPos()) : Blocks.RED_STAINED_GLASS.defaultBlockState();

        level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockParticle), particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
    }

    private HitResult getNearestHit(LivingEntity wielder) {
        HitResult levelPick = wielder.pick(hitDistance, 0, false);

        Vec3 eyePosition = wielder.getEyePosition();
        Vec3 stopPos = wielder.getLookAngle().scale(hitDistance).add(eyePosition);
        AABB scanBox = new AABB(eyePosition, stopPos);

        EntityHitResult entityCastResult = ProjectileUtil.getEntityHitResult(wielder, eyePosition, stopPos, scanBox, e -> true, hitDistance);
        if (entityCastResult == null) {
            return levelPick;
        }

        double blockRayLength = levelPick.getLocation().subtract(eyePosition).length();
        double entityRayLength = entityCastResult.getLocation().subtract(eyePosition).length();

        return entityRayLength < blockRayLength ? entityCastResult : levelPick;
    }
}
