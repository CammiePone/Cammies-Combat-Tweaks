package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "isAcceptableItem", at = @At("TAIL"), cancellable = true)
	public void combattweaks$isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		Enchantment enchant = (Enchantment) (Object) this;
		if(stack.hasNbt() && (enchant instanceof UnbreakingEnchantment || enchant instanceof MendingEnchantment))
			info.setReturnValue(info.getReturnValueZ() && !stack.getNbt().getBoolean("Unbreakable"));
	}
}
