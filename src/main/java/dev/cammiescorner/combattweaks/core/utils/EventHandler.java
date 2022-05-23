package dev.cammiescorner.combattweaks.core.utils;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.client.CombatTweaksClient;
import dev.cammiescorner.combattweaks.common.packets.s2c.CheckModOnServerPacket;
import dev.cammiescorner.combattweaks.common.packets.s2c.SyncAttributeOverridesPacket;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
import dev.cammiescorner.combattweaks.core.registry.ModCommands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.fabricmc.fabric.api.loot.v1.FabricLootPool;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EventHandler {
	private static final Identifier SHIPWRECK_MAP = new Identifier("minecraft", "chests/shipwreck_map");
	private static final Identifier TRIDENT_ADDON = CombatTweaks.id("trident_addon");

	@Environment(EnvType.CLIENT)
	public static void clientEvents() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> client.submit(() -> CombatTweaksClient.isEnabled = false));
	}

	public static void commonEvents() {
		CombatTweaksConfig config = CombatTweaks.getConfig();
		CombatTweaksConfig.EnchantmentTweaks enchantments = config.enchantments;

		//-----Join World Callback-----//
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			CheckModOnServerPacket.send(sender);
			SyncAttributeOverridesPacket.send(sender);
		});

		//-----End Datapack Reload Callback-----//
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {
			if(success)
				PlayerLookup.all(server).forEach(player -> SyncAttributeOverridesPacket.send(ServerPlayNetworking.getSender(player)));
		});

		//-----Loot Table Callback-----//
		LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
			if(id.equals(SHIPWRECK_MAP)) {
				FabricLootSupplier fSupplier = (FabricLootSupplier) supplier.build();
				List<LootPool> pools = ((FabricLootSupplier) manager.getTable(TRIDENT_ADDON)).getPools();

				if(!pools.isEmpty()) {
					if(!fSupplier.getPools().isEmpty()) {
						FabricLootPoolBuilder builder = FabricLootPoolBuilder.of(fSupplier.getPools().get(0));
						List<LootPool> poolsCopy = new ArrayList<>(fSupplier.getPools());

						pools.forEach(lootPool -> ((FabricLootPool) lootPool).getEntries().forEach(builder::withEntry));
						poolsCopy.set(0, builder.build());
						setter.set(FabricLootSupplierBuilder.builder().withPools(poolsCopy).withFunctions(fSupplier.getFunctions()).build());
					}
					else {
						supplier.withPools(pools);
					}
				}
			}
		});

		//-----Command Callback-----//
		CommandRegistrationCallback.EVENT.register(ModCommands::init);

		ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, modifiers) -> {
			Pair<Item, EquipmentSlot> itemSlotPair = new Pair<>(stack.getItem(), slot);
			Multimap<EntityAttribute, EntityAttributeModifier> overrides = CTHelper.ATTRIBUTE_OVERRIDES.get(itemSlotPair);
			Set<UUID> removals = CTHelper.ATTRIBUTE_REMOVALS.get(itemSlotPair);

			if(removals != null || overrides != null) {
				if(removals != null)
					modifiers.forEach((attribute, modifier) -> {
						if(!removals.contains(modifier.getId()))
							overrides.put(attribute, modifier);
					});
				else
					overrides.putAll(modifiers);

				if(overrides != null) {
					modifiers.clear();
					modifiers.putAll(overrides);
				}
			}
		});
	}
}
