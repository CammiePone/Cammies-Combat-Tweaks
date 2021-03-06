package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public class TridentItemMixin {
	@Unique public boolean isRaining = false;
	@Unique public boolean isFallFlying = false;
	@Unique public Vec3d velocity = Vec3d.ZERO;

	@Inject(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRiptide(Lnet/minecraft/item/ItemStack;)I"
	))
	public void combattweaks$setRiptideValueJank(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
		if(CombatTweaks.getConfig().tridents.riptideWorksOutsideWater)
			CombatTweaks.jankyPieceOfShit = true;
	}

	@Inject(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"
	))
	public void combattweaks$setRaining(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo info) {
		isRaining = user.isTouchingWaterOrRain();
		isFallFlying = user.isFallFlying();
		velocity = user.getVelocity();
	}

	@ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"
	))
	public void combattweaks$modifyVelocity(Args args) {
		CombatTweaksConfig.TridentTweaks tridents = CombatTweaks.getConfig().tridents;
		Vec3d toAdd = new Vec3d(args.get(0), args.get(1), args.get(2));

		if(!isRaining)
			toAdd = toAdd.multiply(tridents.riptideEffectivenessOutsideWater);

		Vec3d addedVelocity = velocity.add(toAdd);

		if(tridents.capRiptideAndElytraSpeed && addedVelocity.lengthSquared() > 6.25D)
			addedVelocity = addedVelocity.normalize().multiply(3).subtract(velocity);

		args.setAll(addedVelocity.x, addedVelocity.y, addedVelocity.z);
	}

	@Inject(method = "use", at = @At(value = "RETURN",
			ordinal = 1
	), cancellable = true)
	public void combattweaks$activateRiptide(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
		if(CombatTweaks.getConfig().tridents.riptideWorksOutsideWater) {
			user.setCurrentHand(hand);
			info.setReturnValue(TypedActionResult.consume(info.getReturnValue().getValue()));
		}
	}
}
