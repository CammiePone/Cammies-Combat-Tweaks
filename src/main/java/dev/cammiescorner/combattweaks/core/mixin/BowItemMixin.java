package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BowItem.class)
public class BowItemMixin {
	@ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"
	), index = 5)
	public float combattweaks$removeDeviation(float divergence) {
		return CombatTweaks.getConfig().projectiles.randomDeviation;
	}
}
