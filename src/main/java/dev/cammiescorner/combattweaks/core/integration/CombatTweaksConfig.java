package dev.cammiescorner.combattweaks.core.integration;

import dev.cammiescorner.combattweaks.CombatTweaks;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;

@Config(name = CombatTweaks.MOD_ID)
public class CombatTweaksConfig implements ConfigData {
	@CollapsibleObject public GeneralTweaks general = new GeneralTweaks();
	@CollapsibleObject public SwordTweaks swords = new SwordTweaks();
	@CollapsibleObject public TridentTweaks tridents = new TridentTweaks();
	@CollapsibleObject public CrossbowTweaks crossbows = new CrossbowTweaks();
	@CollapsibleObject public ShieldTweaks shields = new ShieldTweaks();
	@CollapsibleObject public ProjectileTweaks projectiles = new ProjectileTweaks();
	@CollapsibleObject public PotionTweaks potions = new PotionTweaks();
	@CollapsibleObject public EnchantmentTweaks enchantments = new EnchantmentTweaks();
	@CollapsibleObject public RegenerationTweaks regen = new RegenerationTweaks();

	public static class GeneralTweaks {
		public float minAttackCooldown = 1F;
		public boolean undo1dot8Jank = true;
		public boolean itemCooldownAffectsLMB = true;
		public boolean playersBypassInvulTicks = true;
		public float minAttackCooldownForQueue = 0.66F;
		public boolean playersCanCrouchJump = true;
	}

	public static class SwordTweaks {
		public boolean alwaysSweepingEdge = true;
	}

	public static class TridentTweaks {
		public boolean riptideWorksOutsideWater = true;
		public float riptideEffectivenessOutsideWater = 0.5F;
		public boolean capRiptideAndElytraSpeed = true;
	}

	public static class CrossbowTweaks {
		public boolean multishotAndPiercingWorkTogether = true;
	}

	public static class ShieldTweaks {
		public boolean canParry = true;
		public int parryWithinTicks = 5;
		public int disableWeaponOnParryTicks = 100;
		public int disableShieldOnParryTicks = 20;
		public float maxShieldArc = 100;
		public int minTicksHasToBeUp = 10;
		public int disableShieldOnLetGoTicks = 40;
		public float maxDamageBlocked = 5F;
	}

	public static class ProjectileTweaks {
		public float randomDeviation = 0F;
		public boolean checkWiderAreaForElytraUsers = true;
	}

	public static class PotionTweaks {
		public boolean speedIncreasesAirStrafingSpeed = true;
		public boolean slownessDecreasesAirStrafingSpeed = true;
		public int regenBaseTimer = 50;
		public int poisonBaseTimer = 25;
		public int witherBaseTimer = 40;
	}

	public static class EnchantmentTweaks {
		public float maxSweepingEdgeMult = 1F;
		public boolean thornsDealsNoKnockback = true;
	}

	public static class RegenerationTweaks {
		public int minHungerForRegen = 16;
		public int saturationRegenTicks = 20;
		public int hungerRegenTicks = 20;
	}
}
