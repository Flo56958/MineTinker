package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.events.ModifierApplyEvent;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.events.ToolLevelUpEvent;
import de.flo56958.minetinker.events.ToolUpgradeEvent;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.modifiers.types.ExtraModifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TinkerListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	@EventHandler
	public void onToolUpgrade(@NotNull final ToolUpgradeEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();

		final FileConfiguration config = MineTinker.getPlugin().getConfig();

		if (event.isSuccessful()) {
			if (config.getBoolean("Sound.OnUpgrade")) {
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
			}

			ChatWriter.sendActionBar(player,
					LanguageManager.getString("TinkerListener.ToolUpgrade", player)
							.replace("%tool", ChatWriter.getDisplayName(tool) + ChatColor.WHITE)
							.replace("%type", tool.getType().toString().split("_")[0]));
			ChatWriter.log(false, player.getDisplayName() + " upgraded " + ChatWriter.getDisplayName(tool)
					+ ChatColor.WHITE + " (" + tool.getType().toString() + ") to " + tool.getType().toString() + "!");
		} else {
			if (config.getBoolean("Sound.OnUpgrade")) {
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
			}
			ChatWriter.sendActionBar(player, LanguageManager.getString("Alert.ToolUpgradeFailure", player));
		}
	}

	@EventHandler
	public void onModifierApply(@NotNull final ModifierApplyEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Modifier mod = event.getMod();

		if (MineTinker.getPlugin().getConfig().getBoolean("Sound.OnModding")) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 0.5F);
		}

		ChatWriter.sendActionBar(player,
				LanguageManager.getString("TinkerListener.ModifierApply", player)
						.replace("%tool", ChatWriter.getDisplayName(tool) + ChatColor.WHITE)
						.replace("%mod", mod.getColor() + mod.getName() + ChatColor.WHITE)
						.replace("%slots", String.valueOf(event.getSlotsRemaining())));
		ChatWriter.log(false, player.getDisplayName() + " modded " + ChatWriter.getDisplayName(tool)
				+ ChatColor.GRAY + " (" + tool.getType().toString() + ") with " + mod.getColor() + mod.getName()
				+ ChatColor.GRAY + " " + modManager.getModLevel(tool, mod) + "!");
	}

	@EventHandler
	public void onModifierFail(@NotNull final ModifierFailEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Modifier mod = event.getMod();

		if (MineTinker.getPlugin().getConfig().getBoolean("Sound.OnFail")) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 0.5F);
		}

		if (!event.isCommand()) {
			ChatWriter.sendActionBar(player,
					LanguageManager.getString("TinkerListener.ModifierFail", player)
							.replace("%mod", mod.getColor() + mod.getName() + ChatColor.WHITE)
							.replace("%tool", ChatWriter.getDisplayName(tool) + ChatColor.WHITE)
							.replace("%cause", event.getFailCause().toString(player)));
			ChatWriter.log(false, player.getDisplayName() + " failed to apply " + mod.getColor()
					+ mod.getName() + ChatColor.GRAY + " " + (modManager.getModLevel(tool, mod) + 1) + " on "
					+ ChatWriter.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ") ("
					+ event.getFailCause().toString() + ")");
		}
	}

	@EventHandler
	public void onToolLevelUp(@NotNull final ToolLevelUpEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();

		final FileConfiguration config = ConfigurationManager.getConfig("config.yml");
		boolean appliedRandomMod = false;

		if (player != null) {
			if (config.getBoolean("LevelUpEvents.enabled")) {
				final Random rand = new Random();

				if (config.getBoolean("LevelUpEvents.DurabilityRepair.enabled")) {
					final int n = rand.nextInt(100);

					if (n <= config.getInt("LevelUpEvents.DurabilityRepair.percentage")) {
						final Damageable dam = (Damageable) tool.getItemMeta();

						if (dam != null) {
							dam.setDamage(0);
							tool.setItemMeta((ItemMeta) dam);
						}
					}
				}

				if (config.getBoolean("LevelUpEvents.DropLoot.enabled")) {
					final int n = rand.nextInt(100);

					if (n <= config.getInt("LevelUpEvents.DropLoot.percentage")) {
						final int index = rand.nextInt(Lists.DROPLOOT.size());
						final Material m = Material.getMaterial(Lists.DROPLOOT.get(index));

						if (m != null) {
							int max = config.getInt("LevelUpEvents.DropLoot.maximumDrop");
							int min = config.getInt("LevelUpEvents.DropLoot.minimumDrop");
							int amount = 0;

							if (max == min) {
								amount = min;
							} else if (max < min) { //if the user has configured the options wrongly
								config.set("LevelUpEvents.DropLoot.maximumDrop", min);
								config.set("LevelUpEvents.DropLoot.minimumDrop", max);
								ConfigurationManager.saveConfig(config);

								int temp = min;
								min = max;
								max = temp;
							}

							if (amount == 0 && max - min > 0) {
								amount = rand.nextInt(max - min) + min;
							}

							final ItemStack drop = new ItemStack(m, amount);

							if (player.getInventory().addItem(drop).size() != 0) { //adds items to (full) inventory
								player.getWorld().dropItem(player.getLocation(), drop); //drops item when inventory is full
							} // no else as it gets added in if
						}
					}
				}

				if (config.getBoolean("LevelUpEvents.RandomModifier.enabled")) {
					if (rand.nextInt(100) <= config.getInt("LevelUpEvents.RandomModifier.percentage")) {
						int max = rand.nextInt(config.getInt("LevelUpEvents.RandomModifier.MaximumAmountOfModifiers")) + 1;
						for (int j = 0; j < max; j++) {
							final List<Modifier> mods = new ArrayList<>(modManager.getAllowedMods());
							//necessary as the failed modifiers get removed from the list (so a copy is in order)

							if (!config.getBoolean("LevelUpEvents.RandomModifier.AllowExtraModifier")) {
								mods.remove(ExtraModifier.instance());
							}

							int index;
							do {
								if (mods.isEmpty()) {
									break;
								} //Secures that the while will terminate after some time (if all modifiers were removed)

								index = rand.nextInt(mods.size());
								final Modifier mod = mods.get(index);
								if (config.getBoolean("LevelUpEvents.RandomModifier.DropAsItem", false)) {
									appliedRandomMod = true;
									if (player.getInventory().addItem(mod.getModItem()).size() != 0) { //adds items to (full) inventory
										player.getWorld().dropItem(player.getLocation(), mod.getModItem()); //drops item when inventory is full
									} // no else as it gets added in if
								} else {
									appliedRandomMod = modManager.addMod(player, tool, mod, true, true, false, true);
								}
								if (!appliedRandomMod) {
									mods.remove(index); //Remove the failed modifier from the the list of the possibles
								}
							} while (!appliedRandomMod);
						}
					}
				}

				if (config.getBoolean("LevelUpEvents.DropXP.enabled")) {
					final int n = rand.nextInt(100);

					if (n <= config.getInt("LevelUpEvents.DropXP.percentage")) {
						final ExperienceOrb orb = player.getWorld().spawn(player.getLocation(), ExperienceOrb.class);
						orb.setExperience(config.getInt("LevelUpEvents.DropXP.amount"));
					}
				}
			}

			if (config.getBoolean("Sound.OnLevelUp")) {
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
			}

			ChatWriter.sendActionBar(player,
					LanguageManager.getString("TinkerListener.ToolLevelUp", player)
							.replace("%tool", ChatWriter.getDisplayName(tool))
							.replace("%level", String.valueOf(modManager.getLevel(tool))));
			ChatWriter.log(false, player.getDisplayName() + " leveled up " + ChatWriter.getDisplayName(tool) + ChatColor.WHITE + " (" + tool.getType().toString() + ")!");
		}

		final int amount = config.getInt("AddModifierSlotsPerLevel");

		if (amount > 0 && !(config.getBoolean("LevelUpEvents.RandomModifier.DisableAddingNewSlots") && appliedRandomMod)) {
			int slots = modManager.getFreeSlots(tool);

			if (!(slots == Integer.MAX_VALUE || slots < 0)) {
				slots += amount;
			} else {
				slots = Integer.MAX_VALUE;
			}

			modManager.setFreeSlots(tool, slots);
		}
	}
}
