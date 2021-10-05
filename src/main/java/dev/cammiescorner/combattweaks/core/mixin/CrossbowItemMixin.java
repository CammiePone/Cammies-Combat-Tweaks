package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
	@ModifyArg(method = "use", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V"
	), index = 5)
	public float removeDeviation(float divergence) {
		return 0F;
	}
}
