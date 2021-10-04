package dev.cammiescorner.combattweaks.core.utils;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CTHelper {
	public static final Map<Pair<Item, EquipmentSlot>, Multimap<EntityAttribute, EntityAttributeModifier>> ATTRIBUTE_OVERRIDES = new Object2ObjectOpenHashMap<>();
	public static final Map<Pair<Item, EquipmentSlot>, Set<UUID>> ATTRIBUTE_REMOVALS = new Object2ObjectOpenHashMap<>();

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
}
