package dev.cammiescorner.combattweaks.common.packets.c2s;

import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.utils.CTHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SwordSweepPacket {
	public static final Identifier ID = CombatTweaks.id("sword_sweep");

	public static void send(@Nullable Entity entity) {
		PacketByteBuf buf = PacketByteBufs.create();

		if(entity != null)
			buf.writeInt(entity.getId());

		ClientPlayNetworking.send(ID, buf);
	}

	public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
		int entityId = buf.isReadable() ? buf.readInt() : -1;

		server.execute(() -> {
			float yaw = player.getYaw() * 0.017453292F;
			float sweepingMultiplier = EnchantmentHelper.getSweepingMultiplier(player);
			Vec3d pos = player.getPos().add(-MathHelper.sin(yaw) * 1.5D, player.getHeight() / 2D, MathHelper.cos(yaw) * 1.5D);
			List<LivingEntity> targets = player.world.getEntitiesByClass(LivingEntity.class, Box.from(pos).offset(-0.5D, -0.5D, -0.5D).expand(1D, 0.25D, 1D), EntityPredicates.EXCEPT_SPECTATOR.and(livingEntity -> CTHelper.raycast(player, livingEntity).getType() == HitResult.Type.MISS));
			ItemStack stack = player.getMainHandStack();
			Entity crosshairTarget = player.world.getEntityById(entityId);

			if(sweepingMultiplier > 0) {
				if(crosshairTarget == null && !targets.isEmpty())
					stack.damage(1, player, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

				targets.forEach(target -> {
					if(target != player && target != crosshairTarget) {
						float damage = (float) (sweepingMultiplier * (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + EnchantmentHelper.getAttackDamage(player.getMainHandStack(), target.getGroup())));

						target.takeKnockback(0.4D, MathHelper.sin(player.getYaw() * 0.0175F), -MathHelper.cos(player.getYaw() * 0.0175F));
						target.damage(DamageSource.player(player), damage);
					}
				});

				player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1F, 1F);
				player.spawnSweepAttackParticles();
			}
		});
	}
}
