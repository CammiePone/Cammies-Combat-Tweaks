package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(method = "isTouchingWaterOrRain", at = @At("HEAD"), cancellable = true)
	public void makeTridentWorkiePle(CallbackInfoReturnable<Boolean> info) {
		if(CombatTweaks.jankyPieceOfShit) {
			info.setReturnValue(true);
			CombatTweaks.jankyPieceOfShit = false;
		}
	}
}
