package dev.cammiescorner.combattweaks.core.mixin.client;

import dev.cammiescorner.combattweaks.client.CombatTweaksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow @Nullable public ClientPlayerEntity player;
	@Shadow protected int attackCooldown;

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;doAttack()V",
			ordinal = 0
	), cancellable = true)
	public void onLeftClick(CallbackInfo info) {
		if(player != null && (player.getAttackCooldownProgress(0.5F) < 1F || player.getItemCooldownManager().isCoolingDown(player.getMainHandStack().getItem())))
			info.cancel();
	}

	@Inject(method = "doAttack", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;resetLastAttackedTicks()V"
	))
	public void doAttack(CallbackInfo info) {
		if(CombatTweaksClient.isEnabled)
			attackCooldown = 0;
	}
}
