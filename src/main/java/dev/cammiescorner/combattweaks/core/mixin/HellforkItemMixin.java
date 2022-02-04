package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import ladysnake.impaled.common.item.HellforkItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HellforkItem.class)
public class HellforkItemMixin {
	@Inject(method = "canRiptide", at = @At("RETURN"), cancellable = true)
	public void cct$canRiptide(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> info) {
		if(CombatTweaks.getConfig().tridents.riptideWorksOutsideWater)
			info.setReturnValue(true);
	}

	@Inject(method = "use", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	public void cct$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
		if(CombatTweaks.getConfig().tridents.riptideWorksOutsideWater) {
			user.setCurrentHand(hand);
			info.setReturnValue(TypedActionResult.consume(user.getStackInHand(hand)));
		}
	}
}
