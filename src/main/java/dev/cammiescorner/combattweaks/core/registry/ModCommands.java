package dev.cammiescorner.combattweaks.core.registry;

import com.google.common.collect.Multimap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.cammiescorner.combattweaks.CombatTweaks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

public class ModCommands {
	public static void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("modifiers")
				.requires(source -> source.hasPermissionLevel(3))
				.executes(ItemAttributesCommand::listItemAttributes));
	}

	private static class ItemAttributesCommand {
		public static int listItemAttributes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = context.getSource().getPlayer();
			ItemStack stack = player.getMainHandStack().isEmpty() ? player.getOffHandStack() : player.getMainHandStack();
			MutableText text = Text.translatable(stack.getTranslationKey()).append(" ").append(Text.translatable(CombatTweaks.MOD_ID + ".list_item_attributes")).formatted(Formatting.YELLOW);

			if(!stack.isEmpty()) {
				for(EquipmentSlot slot : EquipmentSlot.values()) {
					Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = stack.getAttributeModifiers(slot);

					if(!attributeModifiers.isEmpty()) {
						text.append(Text.literal("\n｜ ").formatted(Formatting.GRAY, Formatting.BOLD)).append(Text.literal(slot.getName()).formatted(Formatting.BLUE));

						attributeModifiers.keySet().forEach(attribute -> attributeModifiers.get(attribute).forEach(modifier -> {
							String attributeId = Registry.ATTRIBUTE.getId(attribute).toString();
							String uuid = modifier.getId().toString();
							String name = modifier.getName();
							String value = String.valueOf(modifier.getValue());
							String operation = modifier.getOperation().name().toLowerCase();

							text.append(Text.literal("\n｜ ｜ ").formatted(Formatting.GRAY, Formatting.BOLD)).append(Text.literal(attributeId).styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, attributeId)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click")))));
							text.append(Text.literal("\n｜ ｜ ｜ ").formatted(Formatting.GRAY, Formatting.BOLD)).append(Text.literal("UUID: ").formatted(Formatting.AQUA)).append(Text.literal(uuid).styled(style -> style.withColor(Formatting.GOLD).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click")))));
							text.append(Text.literal("\n｜ ｜ ｜ ").formatted(Formatting.GRAY, Formatting.BOLD)).append(Text.literal("Name: ").formatted(Formatting.AQUA)).append(Text.literal(name));
							text.append(Text.literal("\n｜ ｜ ｜ ").formatted(Formatting.GRAY, Formatting.BOLD)).append(Text.literal("Value: ").formatted(Formatting.AQUA)).append(Text.literal(value).formatted(Formatting.GOLD));
							text.append(Text.literal("\n｜ ｜ ｜ ").formatted(Formatting.GRAY, Formatting.BOLD)).append(Text.literal("Operation: ").formatted(Formatting.AQUA)).append(Text.literal(operation).formatted(Formatting.GOLD));
							text.append(Text.literal("\n｜ ｜ ").formatted(Formatting.GRAY, Formatting.BOLD));
						}));

						text.append(Text.literal("\n｜ ").formatted(Formatting.GRAY, Formatting.BOLD));
					}
				}
			}
			else {
				player.sendMessage(Text.translatable(CombatTweaks.MOD_ID + ".no_item_error").formatted(Formatting.RED), false);
				return Command.SINGLE_SUCCESS;
			}

			player.sendMessage(text, false);
			return Command.SINGLE_SUCCESS;
		}
	}
}
