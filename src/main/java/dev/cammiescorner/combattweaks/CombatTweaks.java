package dev.cammiescorner.combattweaks;

import dev.cammiescorner.asa.AirStrafingAttribute;
import dev.cammiescorner.combattweaks.common.data.AttributeReloadListener;
import dev.cammiescorner.combattweaks.common.packets.c2s.SwordSweepPacket;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
import dev.cammiescorner.combattweaks.core.utils.EventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class CombatTweaks implements ModInitializer {
	public static final String MOD_ID = "combattweaks";
	public static ConfigHolder<CombatTweaksConfig> configHolder;

	@Override
	public void onInitialize() {
		AutoConfig.register(CombatTweaksConfig.class, JanksonConfigSerializer::new);
		configHolder = AutoConfig.getConfigHolder(CombatTweaksConfig.class);

		StatusEffects.SPEED.addAttributeModifier(AirStrafingAttribute.getAirStrafingAttribute(), "b316f36d-eced-4205-8b99-da1f89a961c5", 0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

		EventHandler.commonEvents();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new AttributeReloadListener());
		ServerPlayNetworking.registerGlobalReceiver(SwordSweepPacket.ID, SwordSweepPacket::handle);
	}

	public static CombatTweaksConfig getConfig() {
		return configHolder.getConfig();
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
