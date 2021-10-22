package dev.cammiescorner.combattweaks.core.utils;

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
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v1.FabricLootPool;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

		//-----Use Block Callback-----//
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			BlockPos pos = hitResult.getBlockPos();
			ItemStack stack = player.getStackInHand(hand);

			if(enchantments.canMakeUnbreakableTools && player.isSneaking() && !(stack.getItem() instanceof BlockItem) && world.getBlockEntity(pos) instanceof BeaconBlockEntity beacon && beacon.level >= enchantments.unbreakableBeaconLevel) {
				if(!world.isClient()) {
					NbtCompound tag = stack.getOrCreateNbt();

					if(!tag.getBoolean("Unbreakable")) {
						boolean hasRequiredEnchants = enchantments.unbreakableRequiredEnchants.entrySet().stream().allMatch(entry -> {
							Enchantment enchantment = Registry.ENCHANTMENT.get(entry.getKey());

							return enchantment == null || EnchantmentHelper.getLevel(enchantment, stack) >= entry.getValue();
						});

						if(hasRequiredEnchants) {
							LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
							NbtList enchTag = tag.getList("Enchantments", NbtElement.COMPOUND_TYPE);

							lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos.up()));
							lightning.setCosmetic(true);
							world.spawnEntity(lightning);
							tag.remove("Damage");
							tag.putBoolean("Unbreakable", true);

							Iterator<NbtElement> enchants = enchTag.iterator();

							while(enchants.hasNext()) {
								NbtCompound enchant = (NbtCompound) enchants.next();
								String id = enchant.getString("id");

								if(enchantments.unbreakableRemovesEnchants.contains(id))
									enchants.remove();
							}

							if(enchTag.isEmpty())
								tag.remove("Enchantments");

							world.playSound(null, pos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.BLOCKS, 1F, 1F);
						}
					}
				}

				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
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
	}
}
