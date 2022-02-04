package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ImpaledTridentItem.class)
public class ImpaledTridentItemMixin {
	@Unique public boolean isRaining = false;
	@Unique public boolean isFallFlying = false;
	@Unique public Vec3d velocity = Vec3d.ZERO;

	@Inject(method = "canRiptide", at = @At("RETURN"), cancellable = true)
	public void cct$canRiptide(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> info) {
		if(CombatTweaks.getConfig().tridents.riptideWorksOutsideWater)
			info.setReturnValue(true);
	}

	@Inject(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"
	))
	public void setRaining(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
		isRaining = user.isTouchingWaterOrRain();
		isFallFlying = user.isFallFlying();
		velocity = user.getVelocity();
	}

	@ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"
	))
	public void modifyVelocity(Args args) {
		CombatTweaksConfig.TridentTweaks tridents = CombatTweaks.getConfig().tridents;
		Vec3d toAdd = new Vec3d(args.get(0), args.get(1), args.get(2));

		if(!isRaining)
			toAdd = toAdd.multiply(tridents.riptideEffectivenessOutsideWater);

		Vec3d addedVelocity = velocity.add(toAdd);

		if(tridents.capRiptideAndElytraSpeed && addedVelocity.lengthSquared() > 6.25D)
			addedVelocity = addedVelocity.normalize().multiply(3).subtract(velocity);

		args.setAll(addedVelocity.x, addedVelocity.y, addedVelocity.z);
	}
}
