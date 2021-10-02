package dev.cammiescorner.combattweaks.common.data;

import com.google.common.collect.HashMultimap;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.utils.CTHelper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class AttributeReloadListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
	public static final Identifier ID = CombatTweaks.id("attribute_overrides");
	private static final Gson GSON = new GsonBuilder().create();

	public AttributeReloadListener() {
		super(GSON, "attribute_overrides");
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		CTHelper.ATTRIBUTE_OVERRIDES.clear();
		CTHelper.ATTRIBUTE_REMOVALS.clear();

		prepared.forEach((identifier, jsonElement) -> {
			Item item = Registry.ITEM.get(identifier);

			if(item != Items.AIR) {
				JsonObject json = JsonHelper.asObject(jsonElement, "attribute_overrides");

				for(EquipmentSlot equipmentSlot : EquipmentSlot.values())
					if(json.has(equipmentSlot.getName())) {
						JsonObject slot = JsonHelper.getObject(json, equipmentSlot.getName());
						HashSet<UUID> removedUUIDs = new HashSet<>();

						if(slot.has("removed")) {
							JsonArray removed = JsonHelper.getArray(slot, "removed");

							for(int i = 0; i < removed.size(); i++)
								removedUUIDs.add(UUID.fromString(JsonHelper.asString(removed.get(i), "removed_modifier_" + i)));
						}

						if(slot.has("modifiers")) {
							JsonArray modifiers = JsonHelper.getArray(slot, "modifiers");

							for(int i = 0; i < modifiers.size(); i++) {
								JsonObject modifier = JsonHelper.asObject(modifiers.get(i), "modifier_" + i);
								EntityAttribute attribute = Registry.ATTRIBUTE.get(new Identifier(JsonHelper.getString(modifier, "attribute")));

								if(attribute != null) {
									UUID uuid = UUID.fromString(JsonHelper.getString(modifier, "uuid"));

									// Mojang hates modders :despair:
									if(uuid.equals(Item.ATTACK_DAMAGE_MODIFIER_ID))
										uuid = Item.ATTACK_DAMAGE_MODIFIER_ID;
									if(uuid.equals(Item.ATTACK_SPEED_MODIFIER_ID))
										uuid = Item.ATTACK_SPEED_MODIFIER_ID;

									String name = JsonHelper.getString(modifier, "name", uuid.toString());
									EntityAttributeModifier.Operation operation = switch(JsonHelper.getString(modifier, "operation", "add")) {
										case "add" -> EntityAttributeModifier.Operation.ADDITION;
										case "multiply_base" -> EntityAttributeModifier.Operation.MULTIPLY_BASE;
										case "multiply_total" -> EntityAttributeModifier.Operation.MULTIPLY_TOTAL;
										default -> throw new IllegalStateException("Unexpected value: " + JsonHelper.getString(modifier, "operation"));
									};
									double value = JsonHelper.getDouble(modifier, "value", 1);

									CTHelper.ATTRIBUTE_OVERRIDES.computeIfAbsent(new Pair<>(item, equipmentSlot), pair -> HashMultimap.create()).put(attribute, new EntityAttributeModifier(uuid, name, value, operation));
									removedUUIDs.add(uuid);
								}
							}
						}

						if(!removedUUIDs.isEmpty())
							CTHelper.ATTRIBUTE_REMOVALS.put(new Pair<>(item, equipmentSlot), removedUUIDs);
					}
			}
		});
	}
}
