package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

import static net.minecraft.entity.projectile.ProjectileUtil.method_37226;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
	@Inject(method = "getEntityCollision", at = @At("HEAD"), cancellable = true)
	private static void canHitFlyingEntity(World world, Entity entity, Vec3d vec3d, Vec3d vec3d2, Box box, Predicate<Entity> predicate, CallbackInfoReturnable<@Nullable EntityHitResult> info) {
		if(CombatTweaks.getConfig().projectiles.checkWiderAreaForElytraUsers) {
			EntityHitResult hitResult = method_37226(world, entity, vec3d, vec3d2, box, predicate, 0.6F);

			if(hitResult != null && hitResult.getEntity() instanceof PlayerEntity player && player.isFallFlying())
				info.setReturnValue(hitResult);
		}
	}
}
