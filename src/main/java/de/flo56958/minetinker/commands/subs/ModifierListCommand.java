package de.flo56958.minetinker.commands.subs;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.SubCommand;
import de.flo56958.minetinker.api.gui.ButtonAction;
import de.flo56958.minetinker.api.gui.GUI;
import de.flo56958.minetinker.commands.ArgumentType;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.PluginReloadEvent;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Syntax of /mt modifiers:
 * 		/mt modifiers {-t}
 *  -t will paste information in the chat instead of using the GUI
 *
 * Legend:
 * 		{ }: not necessary
 * 		[ ]: necessary
 */
public class ModifierListCommand implements SubCommand, Listener {

	private GUI modGUI;

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
		if (sender instanceof Player) {
			if (!(args.length >= 2 && args[1].equalsIgnoreCase("-t"))) {
				this.modGUI.show((Player) sender);
				return true;
			}
		}

		ChatWriter.sendMessage(sender, ChatColor.GOLD, LanguageManager.getInstance().getString("Commands.ModList"));

		int index = 1;

		for (Modifier m : ModManager.getInstance().getAllowedMods()) {
			ChatWriter.sendMessage(sender, ChatColor.WHITE, index++ + ". " + m.getColor() + m.getName()
																		+ ChatColor.WHITE + ": " + m.getDescription());
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length == 2) {
			result.add("-t");
		}
		return result;
	}

	@Override
	public @NotNull String getName() {
		return "modifiers";
	}

	@Override
	public @NotNull List<String> getAliases(boolean withName) {
		ArrayList<String> aliases = new ArrayList<>();
		if (withName) aliases.add(getName());
		aliases.add("mods");
		return aliases;
	}

	@Override
	public @NotNull String getPermission() {
		return "minetinker.commands.modifiers";
	}

	@Override
	public @NotNull Map<Integer, List<ArgumentType>> getArgumentsToParse() {
		return new HashMap<>();
	}

	@Override
	public @NotNull String syntax() {
		return "/mt modifiers {-t}";
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onReload(PluginReloadEvent event) {
		if (modGUI != null) modGUI.close();

		ItemStack backOtherMenuStack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);
		ItemMeta backOtherMenuMeta = backOtherMenuStack.getItemMeta();

		if (backOtherMenuMeta != null) {
			backOtherMenuMeta.setDisplayName(ChatColor.YELLOW + LanguageManager.getInstance().getString("GUIs.BackOtherMenu"));
			backOtherMenuStack.setItemMeta(backOtherMenuMeta);
		}

		int pageNo = 0;
		modGUI = new GUI(MineTinker.getPlugin());
		GUI modRecipes = new GUI(MineTinker.getPlugin());
		GUI.Window currentPage = modGUI.addWindow(6, LanguageManager.getInstance().getString("GUIs.Modifiers.Title")
				.replaceFirst("%pageNo", String.valueOf(++pageNo)));

		int i = 0;

		GUI.addNavigationButtons(currentPage);

		for (Modifier m : ModManager.getInstance().getAllowedMods()) {
			ItemStack item = m.getModItem().clone();
			ItemMeta meta = item.getItemMeta();

			if (meta != null) {
				meta.setDisplayName(m.getColor() + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + m.getName());
				ArrayList<String> lore = new ArrayList<>();

				String modifierItemName = Objects.requireNonNull(m.getModItem().getItemMeta()).getDisplayName();
				if (!modifierItemName.equals("")) {
					lore.add(ChatColor.WHITE + modifierItemName);
					lore.add("");
				}

				List<String> descList = ChatWriter.splitString(m.getDescription(), 30);
				for (String descPart : descList) {
					lore.add(ChatColor.WHITE + descPart);
				}

				lore.add("");

				// Max level
				String maxLevel = ChatColor.WHITE + ChatWriter.toRomanNumerals(m.getMaxLvl()) + ChatColor.GOLD;
				lore.add(ChatColor.GOLD + LanguageManager.getInstance().getString("GUIs.Modifiers.MaxLevel")
						.replaceFirst("%maxLevel", maxLevel));

				// Enchant Cost
				if (m.isEnchantable()) {
					String cost = ChatColor.YELLOW + LanguageManager.getInstance().getString("GUIs.Modifiers.EnchantCost");
					lore.add(cost.replaceFirst("%enchantCost", ChatWriter.toRomanNumerals(m.getEnchantCost())));
					lore.addAll(ChatWriter.splitString(LanguageManager.getInstance().getString("GUIs.Modifiers.BlockToEnchant")
							.replace("%block", ChatColor.ITALIC
									+ MineTinker.getPlugin().getConfig().getString("BlockToEnchantModifiers", "")
									+ ChatColor.RESET + "" + ChatColor.WHITE)
							.replace("%mat", m.getModItem().getType().name()).replace("%key",
									LanguageManager.getInstance().getString("GUIs.RightClick")), 30));
				} else if (m.hasRecipe()) {
					lore.addAll(ChatWriter.splitString(LanguageManager.getInstance().getString("GUIs.Modifiers.ClickToRecipe")
							.replace("%key", LanguageManager.getInstance().getString("GUIs.LeftClick")), 30));
				}

				//Slot cost
				if (m.getSlotCost() > 0) {
					lore.add(ChatColor.WHITE + LanguageManager.getInstance().getString("GUIs.Modifiers.SlotCost")
							.replaceFirst("%amount", String.valueOf(m.getSlotCost())));
				}

				lore.add("");

				//Modifier incompatibilities
				List<Modifier> incomp = new ArrayList<>(ModManager.getInstance().getIncompatibilities(m));
				incomp.removeIf(mod -> !mod.isAllowed());
				if (!incomp.isEmpty()) {
					incomp.sort(Comparator.comparing(Modifier::getName));
					StringBuilder incompatibilities = new StringBuilder();
					for (Modifier in : incomp) {
						incompatibilities.append(in.getName()).append(", ");
					}

					lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD
							+ LanguageManager.getInstance().getString("GUIs.Modifiers.IncompatibleWith"));

					lore.addAll(ChatWriter.splitString(incompatibilities.toString()
							.substring(0, incompatibilities.length() - 2), 30));
				}

				// Applied Enchantments
				List<Enchantment> enchants = m.getAppliedEnchantments();
				if (!enchants.isEmpty()) {
					enchants.sort(Comparator.comparing(e -> LanguageManager.getInstance().getString("Enchantment." + e.getKey().getKey())));
					lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + LanguageManager.getInstance().getString("GUIs.Modifiers.CanApply"));

					StringBuilder e = new StringBuilder();
					for (Enchantment enchant : enchants) {
						e.append(LanguageManager.getInstance().getString("Enchantment." + enchant.getKey().getKey())).append(", ");
					}

					List<String> lines = ChatWriter.splitString(e.toString().substring(0, e.length() - 2),30);
					lore.addAll(lines);
				}

				// Allowed Tools
				lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + LanguageManager.getInstance().getString("GUIs.Modifiers.WorksOn"));

				StringBuilder builder = new StringBuilder();

				builder.append(ChatColor.WHITE);

				List<ToolType> types = m.getAllowedTools();
				types.sort(Comparator.comparing(t -> LanguageManager.getInstance().getString("ToolType." + t.name())));

				for (ToolType toolType : types) {
					builder.append(LanguageManager.getInstance().getString("ToolType." + toolType.name())).append(", ");
				}

				List<String> lines = ChatWriter.splitString(builder.toString().substring(0, builder.length() - 2),30);
				lore.addAll(lines);

				// Apply lore changes
				meta.setLore(lore);
				item.setItemMeta(meta);

				// Setup click actions
				GUI.Window.Button modButton = currentPage.addButton((i % 7) + 1, (i / 7) + 1, item);
				//GiveModifierItem-Action
				modButton.addAction(ClickType.SHIFT_LEFT, new ButtonAction.RUN_RUNNABLE_ON_PLAYER(modButton,
						(player, input) -> {
							if (player.hasPermission("minetinker.commands.givemodifieritem")) {
								if (player.getInventory().addItem(m.getModItem()).size() != 0) { //adds items to (full) inventory
									player.getWorld().dropItem(player.getLocation(), m.getModItem());
								} // no else as it gets added in if-clause
							}
						}));

				//Recipe Action
				Recipe rec = null;

				Iterator<Recipe> it = Bukkit.getServer().recipeIterator();

				while (it.hasNext()) {
					Recipe temp = it.next();
					if (temp.getResult().equals(m.getModItem())) {
						rec = temp;
						break;
					}
				}

				if (rec != null) {
					GUI.Window modRecipe = modRecipes.addWindow(3, m.getColor() + m.getName());
					if (rec instanceof ShapedRecipe) {
						ShapedRecipe srec = (ShapedRecipe) rec;
						ItemStack modItem = m.getModItem().clone();
						DataHandler.setTag(modItem, "Showcase", (int) Math.round(Math.random() * 1000), PersistentDataType.INTEGER, false);
						GUI.Window.Button result = modRecipe.addButton(6, 1, modItem);
						result.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(result, currentPage));

						int slot = -1;

						for (String s : srec.getShape()) {
							if (s.length() == 1 || s.length() == 2) {
								slot++;
							}

							for (char c : s.toCharArray()) {
								slot++;

								if (c == ' ') {
									continue;
								}

								try {
									ItemStack resItem = srec.getIngredientMap().get(c).clone();
									DataHandler.setTag(resItem, "MT-MODSRecipeItem",
											Math.round(Math.random() * 42), PersistentDataType.LONG, false);
									modRecipe.addButton((slot % 3) + 2, (slot / 3), resItem);
								} catch (NullPointerException ignored) {
								}
							}

							if (s.length() == 1) {
								slot++;
							}
						}
					}
					modButton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(modButton, modRecipe));
					GUI.Window.Button returnButton = modRecipe.addButton(8, 2, backOtherMenuStack.clone());
					returnButton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(returnButton, currentPage));
				}

				i++;

				if (i % 28 == 0) {
					currentPage = modGUI.addWindow(6, LanguageManager.getInstance().getString("GUIs.Modifiers.Title")
							.replace("%pageNo", String.valueOf(++pageNo)));

					GUI.addNavigationButtons(currentPage);
					i = 0;
				}
			}
		}
	}
}
