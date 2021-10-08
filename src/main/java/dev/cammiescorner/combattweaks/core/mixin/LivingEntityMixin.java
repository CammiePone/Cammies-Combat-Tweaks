package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow protected int itemUseTimeLeft;
	@Shadow protected ItemStack activeItemStack;
	@Shadow public float knockbackVelocity;
	@Shadow protected abstract void initDataTracker();
	@Shadow private @Nullable DamageSource lastDamageSource;

	@Unique private float damageAmount;

	public LivingEntityMixin(EntityType<?> entityType, World world) { super(entityType, world); }

	@ModifyConstant(method = "blockedByShield", constant = @Constant(doubleValue = 0.0D,
			ordinal = 1
	))
	public double shieldArc(double shieldArc) {
		return -((4D / 9D) * (100 / CombatTweaks.getConfig().shields.maxShieldArc));
	}

	@Inject(method = "isBlocking", at = @At(value = "RETURN",
			ordinal = 2
	), cancellable = true)
	public void isBlocking(CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(true);
	}

	@Inject(method = "damage", at = @At("HEAD"))
	public void resetHurtTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		CombatTweaksConfig config = CombatTweaks.getConfig();
		CombatTweaksConfig.GeneralTweaks general = config.general;
		lastDamageSource = source;
		damageAmount = amount;

		if(general.playersBypassInvulTicks && (source.isProjectile() || source.isExplosive() || source.isFallingBlock() ||
				source.getSource() instanceof ProjectileEntity || source.getAttacker() instanceof ProjectileEntity ||
				source.getSource() instanceof PlayerEntity || source.getAttacker() instanceof PlayerEntity))
			this.timeUntilRegen = 0;
	}

	@ModifyVariable(method = "damage", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/damage/DamageSource;isProjectile()Z"
	), ordinal = 0)
	public float modifyShieldDamageProtection(float amount, DamageSource source) {
		return damageAmount - CombatTweaks.getConfig().shields.maxDamageBlocked;
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "damage", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/damage/DamageSource;getSource()Lnet/minecraft/entity/Entity;"
	))
	public void shieldParry(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		CombatTweaksConfig config = CombatTweaks.getConfig();
		CombatTweaksConfig.ShieldTweaks shields = config.shields;

		if(shields.canParry) {
			Item item = activeItemStack.getItem();

			if(item.getMaxUseTime(activeItemStack) - itemUseTimeLeft <= shields.parryWithinTicks) {
				if((LivingEntity) (Object) this instanceof PlayerEntity player) {
					player.getItemCooldownManager().set(item, shields.disableShieldOnParryTicks);
					player.clearActiveItem();
				}

				if(source.getSource() instanceof PlayerEntity attacker)
					attacker.getItemCooldownManager().set(attacker.getMainHandStack().getItem(), shields.disableWeaponOnParryTicks);
			}
		}
	}

	@Inject(method = "damage", at = @At(value = "FIELD",
			shift = At.Shift.AFTER,
			target = "Lnet/minecraft/entity/LivingEntity;knockbackVelocity:F"
	))
	public void noThornsKnockbackPart1(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		if(CombatTweaks.getConfig().enchantments.thornsDealsNoKnockback && source instanceof EntityDamageSource source1 && source1.isThorns())
			this.knockbackVelocity = 0F;
	}

	@Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
	public void noThornsKnockbackPart2(double strength, double x, double z, CallbackInfo info) {
		if(CombatTweaks.getConfig().enchantments.thornsDealsNoKnockback && lastDamageSource instanceof EntityDamageSource source && source.isThorns())
			info.cancel();
	}
}
