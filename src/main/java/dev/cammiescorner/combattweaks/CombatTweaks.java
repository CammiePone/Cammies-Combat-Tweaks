package dev.cammiescorner.combattweaks;

import dev.cammiescorner.combattweaks.common.data.AttributeReloadListener;
import dev.cammiescorner.combattweaks.core.utils.EventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class CombatTweaks implements ModInitializer {
	public static final String MOD_ID = "combattweaks";

	@Override
	public void onInitialize() {
		EventHandler.commonEvents();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new AttributeReloadListener());
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
