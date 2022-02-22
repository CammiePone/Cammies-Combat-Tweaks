package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
	@ModifyConstant(method = "update", constant = @Constant(intValue = 18,
			ordinal = 0
	))
	public int combattweaks$minHungerForRegen(int regenRate) {
		return CombatTweaks.getConfig().regen.minHungerForRegen;
	}

	@ModifyConstant(method = "update", constant = @Constant(intValue = 10,
			ordinal = 0
	))
	public int combattweaks$regenRateSaturation(int regenRate) {
		return CombatTweaks.getConfig().regen.saturationRegenTicks;
	}

	@ModifyConstant(method = "update", constant = @Constant(intValue = 80,
			ordinal = 0
	))
	public int combattweaks$regenRateNoSaturation(int regenRate) {
		return CombatTweaks.getConfig().regen.hungerRegenTicks;
	}

	@ModifyArg(method = "update", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V",
			ordinal = 0
	))
	public float combattweaks$heal(float value) {
		return 1F;
	}

	@ModifyArg(method = "update", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"
	))
	public float combattweaks$exhaust(float value) {
		return 4F;
	}
}
