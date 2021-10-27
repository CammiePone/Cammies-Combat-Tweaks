package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Unique public boolean canCrouchJump = false;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo info) {
		if(!isOnGround() && CombatTweaks.getConfig().general.playersCanCrouchJump) {
			if(isSneaking() && canCrouchJump) {
				double scale = getHeight() / 1.8D;
				canCrouchJump = false;
				addVelocity(0, 0.125 * scale, 0);
			}
		}
		else
			canCrouchJump = false;
	}

	@Inject(method = "jump", at = @At("HEAD"))
	public void jump(CallbackInfo info) {
		if(!isSneaking())
			canCrouchJump = true;
	}

	@Redirect(method = "attack", at = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/player/PlayerEntity;onGround:Z",
			ordinal = 1
	))
	public boolean preventSweepAttack(PlayerEntity player) {
		return false;
	}

	@ModifyVariable(method = "spawnSweepAttackParticles", at = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/player/PlayerEntity;world:Lnet/minecraft/world/World;",
			ordinal = 1
	), ordinal = 0)
	public double spawnParticlesX(double value) {
		return value * 1.5D;
	}

	@ModifyVariable(method = "spawnSweepAttackParticles", at = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/player/PlayerEntity;world:Lnet/minecraft/world/World;",
			ordinal = 1
	), ordinal = 1)
	public double spawnParticlesZ(double value) {
		return value * 1.5D;
	}
}
