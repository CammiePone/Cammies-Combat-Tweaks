package dev.cammiescorner.combattweaks.common.packets.s2c;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.datafixers.util.Pair;
import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.utils.CTHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class SyncAttributeOverridesPacket {
	public static final Identifier ID = CombatTweaks.id("sync_attribute_overrides");

	public static void send(PacketSender sender) {
		PacketByteBuf buf = PacketByteBufs.create();

		// Map<Pair<Item, EquipmentSlot>, Multimap<EntityAttribute, EntityAttributeModifier>>
		buf.writeVarInt(CTHelper.ATTRIBUTE_OVERRIDES.size());
		CTHelper.ATTRIBUTE_OVERRIDES.forEach((pair, map) -> {
			buf.writeInt(Registry.ITEM.getRawId(pair.getFirst()));
			buf.writeEnumConstant(pair.getSecond());
			buf.writeVarInt(map.size());

			map.keySet().forEach(attribute -> {
				Collection<EntityAttributeModifier> modifiers = map.get(attribute);
				buf.writeInt(Registry.ATTRIBUTE.getRawId(attribute));
				buf.writeVarInt(modifiers.size());

				modifiers.forEach(modifier -> {
					buf.writeUuid(modifier.getId());
					buf.writeString(modifier.getName());
					buf.writeDouble(modifier.getValue());
					buf.writeEnumConstant(modifier.getOperation());
				});
			});
		});

		// Map<Pair<Item, EquipmentSlot>, Set<UUID>>
		buf.writeVarInt(CTHelper.ATTRIBUTE_REMOVALS.size());
		CTHelper.ATTRIBUTE_REMOVALS.forEach((pair, uuids) -> {
			buf.writeInt(Registry.ITEM.getRawId(pair.getFirst()));
			buf.writeEnumConstant(pair.getSecond());
			buf.writeVarInt(uuids.size());
			uuids.forEach(buf::writeUuid);
		});

		sender.sendPacket(ID, buf);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Environment(EnvType.CLIENT)
	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		Map<Pair<Item, EquipmentSlot>, Multimap<EntityAttribute, EntityAttributeModifier>> attributeOverrides = new Object2ObjectOpenHashMap<>();
		Map<Pair<Item, EquipmentSlot>, Set<UUID>> attributeRemovals = new Object2ObjectOpenHashMap<>();
		int attributeOverridesSize = buf.readVarInt();

		for(int i = 0; i < attributeOverridesSize; i++) {
			Item item = Registry.ITEM.get(buf.readInt());
			EquipmentSlot slot = buf.readEnumConstant(EquipmentSlot.class);
			int attributeMapSize = buf.readVarInt();
			Multimap<EntityAttribute, EntityAttributeModifier> modifiersMap = MultimapBuilder.linkedHashKeys().linkedListValues().build();

			for(int j = 0; j < attributeMapSize; j++) {
				EntityAttribute attribute = Registry.ATTRIBUTE.get(buf.readInt());
				int modifiersSize = buf.readVarInt();

				for(int k = 0; k < modifiersSize; k++) {
					UUID uuid = CTHelper.fixMojank(buf.readUuid());
					String name = buf.readString();
					double value = buf.readDouble();
					EntityAttributeModifier.Operation operation = buf.readEnumConstant(EntityAttributeModifier.Operation.class);

					modifiersMap.put(attribute, new EntityAttributeModifier(uuid, name, value, operation));
				}
			}

			attributeOverrides.put(new Pair<>(item, slot), modifiersMap);
		}

		int attributeRemovalsSize = buf.readVarInt();

		for(int i = 0; i < attributeRemovalsSize; i++) {
			Item item = Registry.ITEM.get(buf.readInt());
			EquipmentSlot slot = buf.readEnumConstant(EquipmentSlot.class);
			int uuidSize = buf.readVarInt();
			Set<UUID> uuids = new HashSet<>();

			for(int j = 0; j < uuidSize; j++) {
				UUID uuid = buf.readUuid();
				uuids.add(uuid);
			}

			attributeRemovals.put(new Pair<>(item, slot), uuids);
		}

		client.submit(() -> {
			CTHelper.ATTRIBUTE_OVERRIDES.clear();
			CTHelper.ATTRIBUTE_REMOVALS.clear();

			CTHelper.ATTRIBUTE_OVERRIDES.putAll(attributeOverrides);
			CTHelper.ATTRIBUTE_REMOVALS.putAll(attributeRemovals);
		});
	}
}
