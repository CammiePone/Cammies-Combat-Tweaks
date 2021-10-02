package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnbreakingEnchantment.class)
public abstract class UnbreakingEnchantmentMixin extends Enchantment {
	protected UnbreakingEnchantmentMixin(Rarity rarity, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) { super(rarity, enchantmentTarget, equipmentSlots); }

	@Inject(method = "isAcceptableItem", at = @At("TAIL"), cancellable = true)
	public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if(stack.hasNbt())
			info.setReturnValue(info.getReturnValueZ() && !stack.getNbt().getBoolean("Unbreakable"));
	}
}
