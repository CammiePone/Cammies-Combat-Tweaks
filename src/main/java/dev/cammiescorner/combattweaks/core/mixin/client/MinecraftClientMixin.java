package dev.cammiescorner.combattweaks.core.mixin.client;

import dev.cammiescorner.combattweaks.client.CombatTweaksClient;
import dev.cammiescorner.combattweaks.common.packets.c2s.SwordSweepPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow @Nullable public ClientPlayerEntity player;
	@Shadow protected int attackCooldown;

	@Shadow @Nullable public HitResult crosshairTarget;
	@Unique private boolean attackQueued = false;

	@Redirect(method = "handleInputEvents", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z",
			ordinal = 13
	))
	public boolean queueAttack(KeyBinding instance) {
		return instance.wasPressed() || attackQueued;
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;doAttack()V",
			ordinal = 0
	), cancellable = true)
	public void handleAttacking(CallbackInfo info) {
		if(player != null) {
			if(player.getAttackCooldownProgress(0.5F) > 0.66F)
				attackQueued = true;

			if(player.getAttackCooldownProgress(0.5F) < 1F || player.getItemCooldownManager().isCoolingDown(player.getMainHandStack().getItem()))
				info.cancel();

			if(player.getAttackCooldownProgress(0.5F) == 1F && !player.getItemCooldownManager().isCoolingDown(player.getMainHandStack().getItem()) && player.getMainHandStack().getItem() instanceof SwordItem && crosshairTarget != null) {
				SwordSweepPacket.send(crosshairTarget.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) crosshairTarget).getEntity() : null);

				if(crosshairTarget.getType() == HitResult.Type.BLOCK)
					player.resetLastAttackedTicks();
			}
		}

		if(!info.isCancelled() && attackQueued)
			attackQueued = false;
	}

	@Inject(method = "doAttack", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;resetLastAttackedTicks()V"
	))
	public void removeAttackCooldown(CallbackInfo info) {
		if(CombatTweaksClient.isEnabled)
			attackCooldown = 0;
	}
}
