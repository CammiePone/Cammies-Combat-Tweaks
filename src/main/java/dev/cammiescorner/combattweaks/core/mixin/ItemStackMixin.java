package dev.cammiescorner.combattweaks.core.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow public abstract Item getItem();

//	@Redirect(method = "getAttributeModifiers", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
//	public Multimap<EntityAttribute, EntityAttributeModifier> combattweaks$getAttributeModifiers(Item item, EquipmentSlot slot) {
//		Multimap<EntityAttribute, EntityAttributeModifier> originalAttributes = getItem().getAttributeModifiers(slot);
//		Pair<Item, EquipmentSlot> itemSlotPair = new Pair<>(item, slot);
//		Multimap<EntityAttribute, EntityAttributeModifier> overrides = CTHelper.ATTRIBUTE_OVERRIDES.get(itemSlotPair);
//		Set<UUID> removals = CTHelper.ATTRIBUTE_REMOVALS.get(itemSlotPair);
//
//		if(removals != null || overrides != null) {
//			ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
//
//			if(removals != null)
//				originalAttributes.forEach((attribute, modifier) -> {
//					if(!removals.contains(modifier.getId()))
//						builder.put(attribute, modifier);
//				});
//			else
//				builder.putAll(originalAttributes);
//
//			if(overrides != null)
//				builder.putAll(overrides);
//
//			return builder.build();
//		}
//
//		return originalAttributes;
//	}
}
