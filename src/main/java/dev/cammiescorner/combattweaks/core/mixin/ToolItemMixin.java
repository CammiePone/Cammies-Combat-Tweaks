package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ToolItem.class)
public class ToolItemMixin {
	@ModifyVariable(method = "<init>", at = @At("HEAD"))
	private static Item.Settings doubleGoldDurability(Item.Settings settings, ToolMaterial material) {
		if(material == ToolMaterials.GOLD)
			return settings.maxDamageIfAbsent(material.getDurability() * 2);

		return settings;
	}
}
