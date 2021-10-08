package dev.cammiescorner.combattweaks.core.mixin;

import dev.cammiescorner.combattweaks.CombatTweaks;
import dev.cammiescorner.combattweaks.core.integration.CombatTweaksConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShieldItem.class)
public abstract class ShieldItemMixin extends Item {
	public ShieldItemMixin(Settings settings) { super(settings); }

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		CombatTweaksConfig config = CombatTweaks.getConfig();
		CombatTweaksConfig.ShieldTweaks shields = config.shields;

		if(getMaxUseTime(stack) - remainingUseTicks <= shields.minTicksHasToBeUp && user instanceof PlayerEntity player)
			player.getItemCooldownManager().set(this, shields.disableShieldOnLetGoTicks);
	}
}
