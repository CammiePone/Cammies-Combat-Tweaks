package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BowItem.class)
public class BowItemMixin {
	@ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setProperties(Lnet/minecraft/entity/Entity;FFFFF)V"
	), index = 5)
	public float removeDeviation(float divergence) {
		return 0F;
	}
}
