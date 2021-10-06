package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
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
