package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {
	@ModifyVariable(method = "canApplyUpdateEffect", slice = @Slice(from = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/effect/StatusEffects;REGENERATION:Lnet/minecraft/entity/effect/StatusEffect;")
	), at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	public int combattweaks$fixRegen(int i, int duration, int amplifier) {
		return CombatTweaks.getConfig().potions.regenBaseTimer >> amplifier;
	}

	@ModifyVariable(method = "canApplyUpdateEffect", slice = @Slice(from = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/effect/StatusEffects;POISON:Lnet/minecraft/entity/effect/StatusEffect;")
	), at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	public int combattweaks$fixPoison(int i, int duration, int amplifier) {
		return CombatTweaks.getConfig().potions.poisonBaseTimer >> amplifier;
	}

	@ModifyVariable(method = "canApplyUpdateEffect", slice = @Slice(from = @At(value = "FIELD",
			target = "Lnet/minecraft/entity/effect/StatusEffects;WITHER:Lnet/minecraft/entity/effect/StatusEffect;")
	), at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	public int combattweaks$fixWither(int i, int duration, int amplifier) {
		return CombatTweaks.getConfig().potions.witherBaseTimer >> amplifier;
	}
}
