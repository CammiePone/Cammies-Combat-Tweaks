package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.MultishotEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultishotEnchantment.class)
public abstract class MultishotEnchantmentMixin extends Enchantment {
	protected MultishotEnchantmentMixin(Rarity rarity, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) { super(rarity, enchantmentTarget, equipmentSlots); }

	@Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
	public void combattweaks$canAccept(Enchantment other, CallbackInfoReturnable<Boolean> info) {
		if(CombatTweaks.getConfig().crossbows.multishotAndPiercingWorkTogether)
			info.setReturnValue(super.canAccept(other));
	}
}
