package dev.cammiescorner.combattweaks.core.utils;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import dev.cammiescorner.combattweaks.CombatTweaks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CTHelper {
	public static final Map<Pair<Item, EquipmentSlot>, Multimap<EntityAttribute, EntityAttributeModifier>> ATTRIBUTE_OVERRIDES = new Object2ObjectOpenHashMap<>();
	public static final Map<Pair<Item, EquipmentSlot>, Set<UUID>> ATTRIBUTE_REMOVALS = new Object2ObjectOpenHashMap<>();
	public static final TagKey<EntityType<?>> NO_IFRAME_BYPASS = TagKey.of(Registry.ENTITY_TYPE_KEY, CombatTweaks.id("no_iframe_bypass"));

	/**
	 * Mojang hates modders :despair:
	 */
	public static UUID fixMojank(UUID uuid){
		if(uuid.equals(Item.ATTACK_DAMAGE_MODIFIER_ID))
			return Item.ATTACK_DAMAGE_MODIFIER_ID;
		if(uuid.equals(Item.ATTACK_SPEED_MODIFIER_ID))
			return Item.ATTACK_SPEED_MODIFIER_ID;

		return uuid;
	}

	public static HitResult raycast(Entity origin, Entity target) {
		Vec3d startPos = origin.getCameraPosVec(1F);
		Vec3d endPos = target.getPos().add(0, Math.min(target.getHeight() / 2, origin.getEyeY()), 0);
		HitResult hitResult = origin.world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, origin));

		if(hitResult == null)
			return BlockHitResult.createMissed(endPos, Direction.UP, new BlockPos(endPos));

		return hitResult;
	}
}
