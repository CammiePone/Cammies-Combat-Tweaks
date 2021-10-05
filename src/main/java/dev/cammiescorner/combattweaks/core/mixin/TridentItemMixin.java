package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public class TridentItemMixin {
	@Unique public boolean isRaining = false;
	@Unique public boolean isFallFlying = false;
	@Unique public Vec3d velocity = Vec3d.ZERO;

	@Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"
	))
	public boolean activateRiptide(PlayerEntity playerEntity, ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		return true;
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
		Vec3d toAdd = new Vec3d(args.get(0), args.get(1), args.get(2));

		if(!isRaining)
			toAdd = toAdd.multiply(0.5D);

		Vec3d addedVelocity = velocity.add(toAdd);

		if(addedVelocity.lengthSquared() > 6.25D)
			addedVelocity = addedVelocity.normalize().multiply(3).subtract(velocity);

		args.setAll(addedVelocity.x, addedVelocity.y, addedVelocity.z);
	}

	@Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
			target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"
	))
	public void aaaaa(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
		System.out.println(user.getVelocity().length());
	}

	@Inject(method = "use", at = @At(value = "RETURN",
			ordinal = 1
	), cancellable = true)
	public void activateRiptide(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
		user.setCurrentHand(hand);
		info.setReturnValue(TypedActionResult.consume(info.getReturnValue().getValue()));
	}
}
