package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow protected int itemUseTimeLeft;
	@Shadow protected ItemStack activeItemStack;
	@Unique private float damageAmount;

	public LivingEntityMixin(EntityType<?> entityType, World world) { super(entityType, world); }

	@ModifyConstant(method = "blockedByShield", constant = @Constant(doubleValue = 0.0D, ordinal = 1))
	public double shieldArc(double shieldArc) {
		return -4D / 9D;
	}

	@Inject(method = "isBlocking", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
	public void isBlocking(CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(true);
	}

	@Inject(method = "damage", at = @At("HEAD"))
	public void resetHurtTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		damageAmount = amount;

		if(source.isProjectile() || source.isExplosive() || source.isFallingBlock() ||
				source.getSource() instanceof ProjectileEntity || source.getAttacker() instanceof ProjectileEntity ||
				source.getSource() instanceof PlayerEntity || source.getAttacker() instanceof PlayerEntity)
			this.timeUntilRegen = 0;
	}

	@ModifyVariable(method = "damage", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/damage/DamageSource;isProjectile()Z"
	), ordinal = 0)
	public float modifyShieldDamageProtection(float amount, DamageSource source) {
		return damageAmount - 5F;
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "damage", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/damage/DamageSource;getSource()Lnet/minecraft/entity/Entity;"
	))
	public void shieldParry(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		Item item = activeItemStack.getItem();

		if(item.getMaxUseTime(activeItemStack) - itemUseTimeLeft <= 5) {
			if((LivingEntity) (Object) this instanceof PlayerEntity player) {
				player.getItemCooldownManager().set(item, 20);
				player.clearActiveItem();
			}

			if(source.getSource() instanceof PlayerEntity attacker)
				attacker.getItemCooldownManager().set(attacker.getMainHandStack().getItem(), 80);
		}
	}
}
