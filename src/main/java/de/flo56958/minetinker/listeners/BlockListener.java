package de.flo56958.minetinker.listeners;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.api.gui.ButtonAction;
import de.flo56958.minetinker.api.gui.GUI;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.modifiers.types.SilkTouch;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BlockListener implements Listener {

	private static final ModManager modManager = ModManager.instance();

	//To cancel event if Tool would be broken so other plugins can react faster to MineTinker (e.g. PyroMining)
	//onBlockBreak() has priority highest as it needs to wait on WorldGuard and other plugins to cancel event if necessary
	//TODO: Replace if Issue #111 is implemented
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak_DurabilityCheck(@NotNull final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = player.getInventory().getItemInMainHand();
		if (modManager.isToolViable(tool))
			modManager.durabilityCheck(event, player, tool);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(@NotNull final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = player.getInventory().getItemInMainHand();
		if (Lists.WORLDS.contains(player.getWorld().getName())) return;
		if (event.getBlock().getType().getHardness() == 0 && !(tool.getType() == Material.SHEARS
				|| ToolType.HOE.contains(tool.getType()))) return;
		if (!modManager.isToolViable(tool)) return;

		final FileConfiguration config = MineTinker.getPlugin().getConfig();

		//--------------------------------------EXP-CALCULATIONS--------------------------------------------
		final long cooldown = MineTinker.getPlugin().getConfig().getLong("BlockExpCooldownInSeconds", 60) * 1000;
		boolean eligible = true;
		if (cooldown > 0) {
			final List<MetadataValue> blockPlaced = event.getBlock().getMetadata("blockPlaced");
			for(final MetadataValue val : blockPlaced) {
				if (val == null) continue;
				if (!MineTinker.getPlugin().equals(val.getOwningPlugin())) continue; //Not MTs value

				Object value = val.value();
				if (value instanceof Long) {
					final long time = (long) value;
					eligible = System.currentTimeMillis() - cooldown > time;
					break;
				}
			}
		}

		if (eligible) {
			int expAmount = config.getInt("ExpPerBlockBreak");
			if (config.getBoolean("ExtraExpPerBlock.ApplicableToSilkTouch")
					|| !modManager.hasMod(tool, SilkTouch.instance())) {
				expAmount += config.getInt("ExtraExpPerBlock." + event.getBlock().getType());
				//adds 0 if not in found in config (negative values are also fine)
			}

			modManager.addExp(player, tool, expAmount, true);
		}

		Bukkit.getPluginManager().callEvent(new MTBlockBreakEvent(tool, event));
		//Event-Trigger for Modifiers
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onClick(@NotNull final PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		ItemStack norm = null;
		if (event.getHand() == EquipmentSlot.HAND) {
			norm = player.getInventory().getItemInMainHand();
		} else if (event.getHand() == EquipmentSlot.OFF_HAND) {
			norm = player.getInventory().getItemInOffHand();
		}

		if (norm == null) return;

		if (modManager.isModifierItem(norm)) {
			event.setCancelled(true);
			return;
		}

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final Block block = event.getClickedBlock();
        if (block == null) return;

        if (!player.isSneaking()) {
            final Material type = block.getType();

            if (type == Material.ANVIL || type == Material.CRAFTING_TABLE
                    || type == Material.CHEST || type == Material.ENDER_CHEST
                    || type == Material.DROPPER || type == Material.HOPPER
                    || type == Material.DISPENSER || type == Material.TRAPPED_CHEST
                    || type == Material.FURNACE || type == Material.ENCHANTING_TABLE) {
                return;
            }
        }

        if (block.getType() != Material.getMaterial(
                Objects.requireNonNull(MineTinker.getPlugin().getConfig()
                                .getString("BlockToEnchantModifiers", Material.BOOKSHELF.name()),
                        "BlockToEnchantModifiers is null!"))) return;

        final ArrayList<Modifier> modifiers = new ArrayList<>();

        for (final Modifier m : modManager.getAllowedMods()) {
            if (!m.isEnchantable()) continue;
            if (m.getModItem().getType().equals(norm.getType())) {
                modifiers.add(m);
            }
        }

        if (modifiers.isEmpty()) return;
        else if (modifiers.size() == 1) {
            final Modifier m = modifiers.remove(0);
            m.enchantItem(player, norm);
        } else {
            // Create GUI for easy choosing of Modifier to enchant
            final GUI gui = new GUI(MineTinker.getPlugin());
            Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), gui::close, 5 * 60 * 20);

            final int size = Math.min(modifiers.size() / 9 + 1, 6);
            final GUI.Window window = gui.addWindow(size, LanguageManager.getString("GUIs.Enchantable.Title", player));
            modifiers.sort(Comparator.comparing(Modifier::getName));

            int slot = 0;
            for (final Modifier mod : modifiers) {
                final GUI.Window.Button button = window.addButton(slot++, mod.getModItem());
                final ItemStack itemStack = button.getItemStack();

                final ItemMeta meta = itemStack.getItemMeta();
                assert meta != null;
                final List<String> lore = meta.getLore();
                assert lore != null;

                final String s = LanguageManager.getString("GUIs.Modifiers.EnchantCost", player)
                        .replaceFirst("%enchantCost", (mod.getEnchantCost() <= player.getLevel()
                                        ? ChatColor.WHITE
                                        : ChatColor.RED)
                                        + ChatWriter.toRomanNumerals(mod.getEnchantCost()));
                if (mod.getEnchantCost() <= player.getLevel()) {
                    lore.add(ChatColor.WHITE + s);
                    ItemStack finalNorm = norm;
                    button.addAction(ClickType.LEFT, new ButtonAction.RUN_RUNNABLE_ON_PLAYER(button,
                            (p, input) -> {
                                mod.enchantItem(p, finalNorm);
                                gui.close();
                            }));
                } else {
                    lore.add(ChatColor.RED + s);
                }
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }

            gui.show(player);
        }
        event.setCancelled(true);
    }

	@EventHandler(ignoreCancelled = true)
	public void onInteract(@NotNull final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (Lists.WORLDS.contains(player.getWorld().getName())) return;

		final ItemStack tool = player.getInventory().getItemInMainHand();
		if (!modManager.isToolViable(tool)) return;

		if (!modManager.durabilityCheck(event, player, tool)) return;

		Bukkit.getPluginManager().callEvent(new MTPlayerInteractEvent(tool, event));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlace(@NotNull final BlockPlaceEvent event) {
		event.getBlock().setMetadata("blockPlaced", new FixedMetadataValue(MineTinker.getPlugin(), System.currentTimeMillis()));
	}
}