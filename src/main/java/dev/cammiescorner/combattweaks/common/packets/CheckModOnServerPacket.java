package dev.cammiescorner.combattweaks.common.packets;

import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.client.CombatTweaksClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CheckModOnServerPacket {
	public static final Identifier ID = CombatTweaks.id("check_mod_on_server");

	public static void send(PacketSender sender) {
		sender.sendPacket(ID, PacketByteBufs.create());
	}

	@Environment(EnvType.CLIENT)
	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender sender) {
		client.submit(() -> CombatTweaksClient.isEnabled = true);
	}
}
