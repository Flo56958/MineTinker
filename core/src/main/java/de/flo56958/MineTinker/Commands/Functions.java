package de.flo56958.MineTinker.Commands;

import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

class Functions {

	private static final ModManager modManager = ModManager.instance();

	/**
	 * renames the tool in the main hand
	 *
	 * @param player The player to get the item from
	 * @param args   command input of the player - parsed down from onCommand()
	 */
	static void name(Player player, String[] args) {
		if (args.length >= 2) {
			ItemStack tool = player.getInventory().getItemInMainHand();

			if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
				StringBuilder name = new StringBuilder();

				for (int i = 1; i < args.length; i++) {
					name.append(" ").append(args[i].replace('&', '§'));
				}

				name = new StringBuilder(name.substring(1));

				ItemMeta meta = tool.getItemMeta();

				if (meta != null) {
					meta.setDisplayName(name.toString());
					tool.setItemMeta(meta);
				}
			} else {
				Commands.invalidTool(player);
			}
		} else {
			Commands.invalidArgs(player);
		}
	}

	/**
	 * Removes the specified modifier (index) from a valid MineTinker-Tool/Armor in the players main hand
	 *
	 * @param player The player to get the item from
	 * @param args   command input of the player - parsed down from onCommand()
	 */
	static void removeMod(Player player, String[] args) {
		if (args.length >= 2) {
			ItemStack tool = player.getInventory().getItemInMainHand();

			if (!modManager.isToolViable(tool) && !modManager.isArmorViable(tool)) {
				Commands.invalidArgs(player);
				return;
			}

			for (Modifier m : modManager.getAllMods()) {
				if (args[1].equalsIgnoreCase(m.getName())) {
					modManager.removeMod(player.getInventory().getItemInMainHand(), m);
					return;
				}
			}

			Commands.invalidArgs(player);
		} else {
			Commands.invalidArgs(player);
		}
	}

	/**
	 * Sets the durability of a valid MineTinker-Tool/Armor in the players main hand to the specified amount
	 *
	 * @param player The player to get the item from
	 * @param args   command input of the player - parsed down from onCommand()
	 */
	static void setDurability(Player player, String[] args) {
		if (args.length == 2) {
			ItemStack tool = player.getInventory().getItemInMainHand();

			if (modManager.isToolViable(tool) || modManager.isArmorViable(tool)) {
				if (tool.getItemMeta() instanceof Damageable) {
					Damageable damageable = (Damageable) tool.getItemMeta();

					try {
						int durability = Integer.parseInt(args[1]);

						if (durability <= tool.getType().getMaxDurability()) {
							damageable.setDamage(tool.getType().getMaxDurability() - durability);
						} else {
							ChatWriter.sendMessage(player, ChatColor.RED,
									LanguageManager.getString("Commands.SetDurability.InvalidInput", player));
						}
					} catch (Exception e) {
						if (args[1].toLowerCase().equals("full") || args[1].toLowerCase().equals("f")) {
							damageable.setDamage(0);
						} else {
							ChatWriter.sendMessage(player, ChatColor.RED,
									LanguageManager.getString("Commands.SetDurability.InvalidInput", player));
						}
					}

					tool.setItemMeta((ItemMeta) damageable);
				}
			} else {
				Commands.invalidTool(player);
			}
		} else {
			Commands.invalidArgs(player);
		}
	}

	static void itemStatistics(Player p) {
		ItemStack is = p.getInventory().getItemInMainHand();
		if (!(modManager.isToolViable(is) || modManager.isArmorViable(is))) {
			return;
		}

		ChatWriter.sendMessage(p, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Head", p)
				.replaceFirst("%toolname", ItemGenerator.getDisplayName(is)));
		ChatWriter.sendMessage(p, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Level", p)
				.replaceFirst("%level", "" + modManager.getLevel(is)));
		ChatWriter.sendMessage(p, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Exp", p)
				.replaceFirst("%current", "" + modManager.getExp(is))
				.replaceFirst("%nextlevel", "" + modManager.getNextLevelReq(modManager.getLevel(is))));
		ChatWriter.sendMessage(p, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.FreeSlots", p)
				.replaceFirst("%slots", "" + modManager.getFreeSlots(is)));
		ChatWriter.sendMessage(p, ChatColor.WHITE, LanguageManager.getString("Commands.ItemStatistics.Modifiers", p));

		for (Modifier mod : modManager.getAllowedMods()) {
			if (NBTUtils.getHandler().hasTag(is, mod.getKey())) {
				ChatWriter.sendMessage(p, ChatColor.WHITE, mod.getColor() + mod.getName() + ChatColor.WHITE + " " + NBTUtils.getHandler().getInt(is, mod.getKey()));
			}
		}
	}
}
