package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
import dev.cammiescorner.combattweaks.core.utils.CTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
	@Unique private float damageAmount;
	@Shadow private @Nullable DamageSource lastDamageSource;
	@Shadow protected abstract void initDataTracker();

	public LivingEntityMixin(EntityType<?> entityType, World world) { super(entityType, world); }

	@ModifyConstant(method = "blockedByShield", constant = @Constant(doubleValue = 0.0D,
			ordinal = 1
	))
	public double combattweaks$shieldArc(double shieldArc) {
		return (CombatTweaks.getConfig().shields.maxShieldArc - 180D) / 180D;
	}

	@Inject(method = "isBlocking", at = @At(value = "RETURN",
			ordinal = 2
	), cancellable = true)
	public void combattweaks$isBlocking(CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(true);
	}

	@Inject(method = "damage", at = @At("HEAD"))
	public void combattweaks$resetHurtTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		lastDamageSource = source;
		damageAmount = amount;

		if(!getType().isIn(CTHelper.NO_IFRAME_BYPASS)) {
			CombatTweaksConfig config = CombatTweaks.getConfig();
			CombatTweaksConfig.GeneralTweaks general = config.general;

			if(general.playersBypassInvulTicks && (source.isProjectile() || source.isExplosive() || source.isFallingBlock() ||
					source.getSource() instanceof ProjectileEntity || source.getSource() instanceof PlayerEntity))
				timeUntilRegen = 0;
		}
	}

	@ModifyVariable(method = "damage", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/damage/DamageSource;isProjectile()Z"
	), ordinal = 0, argsOnly = true)
	public float combattweaks$modifyShieldDamageProtection(float amount, DamageSource source) {
		return Math.max(0, damageAmount - CombatTweaks.getConfig().shields.maxDamageBlocked);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "damage", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/damage/DamageSource;getSource()Lnet/minecraft/entity/Entity;"
	))
	public void combattweaks$shieldParry(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		CombatTweaksConfig config = CombatTweaks.getConfig();
		CombatTweaksConfig.ShieldTweaks shields = config.shields;

		if(shields.canParry) {
			Item item = activeItemStack.getItem();

			if((LivingEntity) (Object) this instanceof PlayerEntity player && item.getMaxUseTime(activeItemStack) - itemUseTimeLeft <= shields.parryWithinTicks) {
				player.getItemCooldownManager().set(item, shields.disableShieldOnParryTicks);
				player.clearActiveItem();

				if(source.getSource() instanceof PlayerEntity attacker)
					attacker.getItemCooldownManager().set(attacker.getMainHandStack().getItem(), shields.disableWeaponOnParryTicks);
				else if(source.getSource() instanceof LivingEntity attacker)
					attacker.damage(DamageSource.player(player), damageAmount * 2);

				world.playSound(null, getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1F, 1F);
			}
		}
	}

	@Inject(method = "damage", at = @At(value = "FIELD",
			shift = At.Shift.AFTER,
			target = "Lnet/minecraft/entity/LivingEntity;knockbackVelocity:F"
	))
	public void combattweaks$noThornsKnockbackPart1(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		if(CombatTweaks.getConfig().enchantments.thornsDealsNoKnockback && source instanceof EntityDamageSource source1 && source1.isThorns())
			this.knockbackVelocity = 0F;
	}

	@Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
	public void combattweaks$noThornsKnockbackPart2(double strength, double x, double z, CallbackInfo info) {
		if(CombatTweaks.getConfig().enchantments.thornsDealsNoKnockback && lastDamageSource instanceof EntityDamageSource source && source.isThorns())
			info.cancel();
	}
}
