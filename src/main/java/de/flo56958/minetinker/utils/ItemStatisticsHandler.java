package de.flo56958.minetinker.utils;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTEntityDamageEvent;
import de.flo56958.minetinker.api.events.MTEntityDeathEvent;
import de.flo56958.minetinker.api.gui.ButtonAction;
import de.flo56958.minetinker.api.gui.GUI;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.data.DataHandler;
import de.flo56958.minetinker.utils.data.EnumMapTagType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class ItemStatisticsHandler implements Listener {

	public static final String keyStart = "stats_";

	@NotNull
	public static GUI getGUI(final ItemStack item) {
		GUI gui = new GUI(MineTinker.getPlugin());
		Bukkit.getScheduler().runTaskLater(gui.getPlugin(), gui::close, 5 * 60 * 20);
		List<Modifier> mods = ModManager.instance().getAllowedMods();
		mods.sort(Comparator.comparing(Modifier::getName));

		GUI.Window window = gui.addWindow(1,
				LanguageManager.getString("GUIs.Statistics.Title")
						.replaceAll("%tool", ChatWriter.getDisplayName(item)));

		ItemStack backStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		ItemMeta backStackmeta = backStack.getItemMeta();
		backStackmeta.setDisplayName(LanguageManager.getString("GUIs.BackOtherMenu"));
		backStack.setItemMeta(backStackmeta);

		{
			ItemStack itemStack = new ItemStack(item.getType());
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta != null) {
				ArrayList<String> lore = new ArrayList<>();
				OfflinePlayer player = ModManager.instance().getCreator(item);
				if (player != null) lore.add(ChatColor.WHITE
						+ LanguageManager.getString("GUIs.Statistics.General.Creator")
						.replace("%player", player.getName()));
				Long date = DataHandler.getTag(item, "creation_date", PersistentDataType.LONG);
				if (date != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
					Date date_ = new Date(date);
					lore.add(ChatColor.WHITE + LanguageManager.getString("GUIs.Statistics.General.Created")
							.replace("%date", formatter.format(date_)));
				}
				itemMeta.setLore(lore);
				itemMeta.setDisplayName(ChatColor.GOLD + LanguageManager.getString("GUIs.Statistics.General.Title"));
				itemStack.setItemMeta(itemMeta);
			}

			//Setting up ModifierStats GUI
			GUI modifierStats = new GUI(MineTinker.getPlugin());
			{
				int pageNo = 0;
				GUI.Window currentPage = modifierStats.addWindow(6,
						LanguageManager.getString("GUIs.Statistics.Modifiers.TitlePage")
								.replace("%tool", ChatWriter.getDisplayName(item))
								.replaceFirst("%pageNo", String.valueOf(++pageNo)));

				int i = 0;

				GUIs.addNavigationButtons(currentPage);
				GUI.Window.Button backbutton = currentPage.addButton(4, 5, backStack);
				backbutton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backbutton, window));

				for (final Modifier m : mods) {
					if (!ModManager.instance().hasMod(item, m)) continue;
					final ItemStack is = m.getModItem().clone();
					final ItemMeta meta = is.getItemMeta();

					if (meta != null) {
						meta.setDisplayName(m.getColor() + m.getName());
						meta.setLore(m.getStatistics(item));
						is.setItemMeta(meta);
					}

					currentPage.addButton((i % 7) + 1, (i / 7) + 1, is);
					i++;

					if (i % 28 == 0) {
						currentPage = modifierStats.addWindow(6,
								LanguageManager.getString("GUIs.Statistics.Modifiers.TitlePage")
										.replace("%tool", ChatWriter.getDisplayName(item))
										.replaceFirst("%pageNo", String.valueOf(++pageNo)));

						GUIs.addNavigationButtons(currentPage);
						backbutton = currentPage.addButton(4, 5, backStack);
						backbutton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backbutton, window));
						i = 0;
					}
				}
			}

			GUI.Window.Button button = window.addButton(3, itemStack);
			button.addAction(ClickType.LEFT,
					new ButtonAction.PAGE_GOTO(button, modifierStats.getWindow(0)));
		}

		{
			EnumMap<Material, Long> map = DataHandler.getTag(item, keyStart + "blocks_broken",
					new EnumMapTagType<>(new EnumMap<>(Material.class)));
			if (map != null) {
				ItemStack itemStack = new ItemStack(Material.STONE);
				ItemMeta itemMeta = itemStack.getItemMeta();
				long totalBlocks = 0;

				//Setting up BlockStats GUI
				GUI blockStats = new GUI(MineTinker.getPlugin());
				{
					int pageNo = 0;
					GUI.Window currentPage = blockStats.addWindow(6,
							LanguageManager.getString("GUIs.Statistics.Blocks.TitlePage")
									.replace("%tool", ChatWriter.getDisplayName(item))
							.replaceFirst("%pageNo", String.valueOf(++pageNo)));

					int i = 0;

					GUIs.addNavigationButtons(currentPage);
					GUI.Window.Button backbutton = currentPage.addButton(4, 5, backStack);
					backbutton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backbutton, window));

					ArrayList<Material> materials = new ArrayList<>(map.keySet());
					materials.sort(Comparator.comparing(map::get));
					Collections.reverse(materials);
					for (Material m : materials) {
						ItemStack is = new ItemStack(m);
						ItemMeta meta = is.getItemMeta();

						long amount = map.get(m);
						totalBlocks += amount;

						if (meta != null) {
							meta.setLore(Collections.singletonList(ChatColor.WHITE
									+ LanguageManager.getString("GUIs.Statistics.Blocks.AmountBroken")
									.replace("%amount", String.valueOf(amount))));
							is.setItemMeta(meta);
						}

						currentPage.addButton((i % 7) + 1, (i / 7) + 1, is);
						i++;

						if (i % 28 == 0) {
							currentPage = blockStats.addWindow(6,
									LanguageManager.getString("GUIs.Statistics.Blocks.TitlePage")
											.replace("%tool", ChatWriter.getDisplayName(item))
									.replace("%pageNo", String.valueOf(++pageNo)));

							GUIs.addNavigationButtons(currentPage);
							backbutton = currentPage.addButton(4, 5, backStack);
							backbutton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backbutton, window));
							i = 0;
						}
					}
				}

				if (itemMeta != null) {
					itemMeta.setDisplayName(ChatColor.GOLD + LanguageManager.getString("GUIs.Statistics.Blocks.Title"));
					itemMeta.setLore(Collections.singletonList(ChatColor.WHITE + LanguageManager.getString("GUIs.Statistics.Blocks.TotalAmount")
							.replaceAll("%amount", String.valueOf(totalBlocks))));
					itemStack.setItemMeta(itemMeta);
				}

				GUI.Window.Button button = window.addButton(4, itemStack);
				button.addAction(ClickType.LEFT,
						new ButtonAction.PAGE_GOTO(button, blockStats.getWindow(0)));
			}
		}

		{
			EnumMap<EntityType, Long> map = DataHandler.getTag(item, keyStart + "entity_killed",
					new EnumMapTagType<>(new EnumMap<>(EntityType.class)));
			double damageDealt = DataHandler.getTagOrDefault(item, keyStart + "damage_dealt",
					PersistentDataType.DOUBLE, 0.0D);

			double damageReceived = DataHandler.getTagOrDefault(item, keyStart + "damage_received",
					PersistentDataType.DOUBLE, 0.0D);

			ItemStack itemStack = new ItemStack(Material.SKELETON_SKULL);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta != null) {
				itemMeta.setDisplayName(ChatColor.GOLD + LanguageManager.getString("GUIs.Statistics.Combat.Title"));
				final ArrayList<String> lore = new ArrayList<>();
				lore.add(ChatColor.WHITE
						+ LanguageManager.getString("GUIs.Statistics.Combat.DamageDealt")
						.replace("%amount", String.valueOf(Math.round(damageDealt))));
				lore.add(ChatColor.WHITE
						+ LanguageManager.getString("GUIs.Statistics.Combat.DamageReceived")
						.replace("%amount", String.valueOf(Math.round(damageReceived))));
				itemMeta.setLore(lore);
				itemStack.setItemMeta(itemMeta);
			}

			GUI.Window.Button button = window.addButton(5, itemStack);

			if (map != null) {
				//Setting up CombatStats GUI
				GUI combatStats = new GUI(MineTinker.getPlugin());
				{
					int pageNo = 0;
					GUI.Window currentPage = combatStats.addWindow(6,
							LanguageManager.getString("GUIs.Statistics.Combat.TitlePage")
									.replace("%tool", ChatWriter.getDisplayName(item))
							.replace("%pageNo", String.valueOf(++pageNo)));

					int i = 0;

					GUIs.addNavigationButtons(currentPage);
					GUI.Window.Button backbutton = currentPage.addButton(4, 5, backStack);
					backbutton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backbutton, window));

					ArrayList<EntityType> types = new ArrayList<>(map.keySet());
					types.sort(Comparator.comparing(map::get));
					Collections.reverse(types);
					for (EntityType t : types) {
						Material mat = Material.getMaterial(t.toString().toUpperCase() + "_SPAWN_EGG");
						ItemStack is = new ItemStack((mat != null) ? mat : Material.DIAMOND_SWORD);
						ItemMeta meta = is.getItemMeta();

						if (meta != null) {
							if (mat == null) meta.setDisplayName(ChatColor.WHITE + t.toString());
							meta.setLore(Collections.singletonList(ChatColor.WHITE
									+ LanguageManager.getString("GUIs.Statistics.Combat.AmountKilled")
									.replace("%amount", String.valueOf(map.get(t)))));
							is.setItemMeta(meta);
						}

						currentPage.addButton((i % 7) + 1, (i / 7) + 1, is);
						i++;

						if (i % 28 == 0) {
							currentPage = combatStats.addWindow(6,
									LanguageManager.getString("GUIs.Statistics.Combat.TitlePage")
											.replace("%tool", ChatWriter.getDisplayName(item))
									.replace("%pageNo", String.valueOf(++pageNo)));

							GUIs.addNavigationButtons(currentPage);
							backbutton = currentPage.addButton(4, 5, backStack);
							backbutton.addAction(ClickType.LEFT, new ButtonAction.PAGE_GOTO(backbutton, window));
							i = 0;
						}
					}
				}
				button.addAction(ClickType.LEFT,
						new ButtonAction.PAGE_GOTO(button, combatStats.getWindow(0)));
			}
		}

		return gui;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(MTBlockBreakEvent event) {
		final ItemStack tool = event.getTool();

		final Material blockType = event.getBlock().getType();
		final EnumMap<Material, Long> map = DataHandler.getTagOrDefault(tool, keyStart + "blocks_broken",
				new EnumMapTagType<>(new EnumMap<>(Material.class)), new EnumMap<>(Material.class));

		long count = map.getOrDefault(blockType, 0L);
		count++;
		map.put(blockType, count);
		DataHandler.setTag(tool, keyStart + "blocks_broken", map,
				new EnumMapTagType<>(new EnumMap<>(Material.class)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDeath(MTEntityDeathEvent event) {
		final ItemStack tool = event.getTool();

		final EntityType entityType = event.getEvent().getEntity().getType();
		final EnumMap<EntityType, Long> map = DataHandler.getTagOrDefault(tool, keyStart + "entity_killed",
				new EnumMapTagType<>(new EnumMap<>(EntityType.class)), new EnumMap<>(EntityType.class));

		long count = map.getOrDefault(entityType, 0L);
		count++;
		map.put(entityType, count);
		DataHandler.setTag(tool, keyStart + "entity_killed", map,
				new EnumMapTagType<>(new EnumMap<>(EntityType.class)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamage(@NotNull final MTEntityDamageEvent event) {
		final ItemStack armor = event.getTool();
		if (ModManager.instance().isArmorViable(armor)) {
			final double finalDamage = event.getEvent().getFinalDamage();
			final double old = DataHandler.getTagOrDefault(armor, keyStart + "damage_received",
					PersistentDataType.DOUBLE, 0.0D);
			DataHandler.setTag(armor, keyStart + "damage_received", old + finalDamage,
					PersistentDataType.DOUBLE);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamage(@NotNull final MTEntityDamageByEntityEvent event) {
		final ItemStack tool = event.getTool();

		if (event.getPlayer().equals(event.getEvent().getEntity())) { //Player is attacked
			final double finalDamage = event.getEvent().getFinalDamage();
			final double old = DataHandler.getTagOrDefault(tool, keyStart + "damage_received",
					PersistentDataType.DOUBLE, 0.0D);
			DataHandler.setTag(tool, keyStart + "damage_received", old + finalDamage,
					PersistentDataType.DOUBLE);
		} else if (event.getPlayer().equals(event.getEvent().getDamager())) { //Player attacked
			final double finalDamage = event.getEvent().getFinalDamage();
			final double old = DataHandler.getTagOrDefault(tool, keyStart + "damage_dealt",
					PersistentDataType.DOUBLE, 0.0D);
			DataHandler.setTag(tool, keyStart + "damage_dealt", old + finalDamage,
					PersistentDataType.DOUBLE);
		}
	}
}
