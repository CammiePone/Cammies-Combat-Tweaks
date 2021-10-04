package dev.cammiescorner.combattweaks;

import dev.cammiescorner.combattweaks.common.data.AttributeReloadListener;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
import dev.cammiescorner.combattweaks.core.utils.EventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class CombatTweaks implements ModInitializer {
	public static final String MOD_ID = "combattweaks";
	public static ConfigHolder<CombatTweaksConfig> configHolder;

	@Override
	public void onInitialize() {
		AutoConfig.register(CombatTweaksConfig.class, JanksonConfigSerializer::new);
		configHolder = AutoConfig.getConfigHolder(CombatTweaksConfig.class);

		EventHandler.commonEvents();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new AttributeReloadListener());
	}

	public static CombatTweaksConfig getConfig() {
		return configHolder.getConfig();
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
