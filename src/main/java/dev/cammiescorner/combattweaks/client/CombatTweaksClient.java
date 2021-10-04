package dev.cammiescorner.combattweaks.client;

import dev.cammiescorner.combattweaks.common.packets.CheckModOnServerPacket;
import dev.cammiescorner.combattweaks.common.packets.SyncAttributeOverridesPacket;
import dev.cammiescorner.combattweaks.core.utils.EventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CombatTweaksClient implements ClientModInitializer {
	public static boolean isEnabled = false;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(CheckModOnServerPacket.ID, CheckModOnServerPacket::handle);
		ClientPlayNetworking.registerGlobalReceiver(SyncAttributeOverridesPacket.ID, SyncAttributeOverridesPacket::handle);

		EventHandler.clientEvents();
	}
}
