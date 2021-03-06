package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweepingEnchantment.class)
public abstract class SweepingEnchantmentMixin extends Enchantment {
	@Shadow public abstract int getMaxLevel();

	protected SweepingEnchantmentMixin(Rarity rarity, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) { super(rarity, enchantmentTarget, equipmentSlots); }

	@Inject(method = "getMultiplier", at = @At("HEAD"), cancellable = true)
	private static void combattweaks$getMultiplier(int level, CallbackInfoReturnable<Float> info) {
		info.setReturnValue(CombatTweaks.getConfig().enchantments.maxSweepingEdgeMult * (level / Enchantments.SWEEPING.getMaxLevel()));
	}
}
