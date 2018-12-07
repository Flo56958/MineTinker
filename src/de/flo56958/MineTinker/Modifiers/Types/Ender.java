package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collections;

public class Ender extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Ender.yml");

    private final boolean compatibleWithInfinity;
    private final boolean hasSound;

    public Ender() {
        super(config.getString("Ender.name"),
                "[Special Endereye] Teleports you while sneaking to the arrow location!",
                ModifierType.ENDER,
                ChatColor.DARK_GREEN,
                1,
                ItemGenerator.itemEnchanter(Material.ENDER_EYE, ChatColor.DARK_GREEN + config.getString("Ender.name_modifier"), 1, Enchantment.DURABILITY, 1),
                new ArrayList<>(Collections.singletonList(ToolType.BOW)),
                Main.getPlugin());
        this.hasSound = config.getBoolean("Ender.Sound");
        this.compatibleWithInfinity = config.getBoolean("Ender.CompatibleWithInfinity");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (!this.compatibleWithInfinity) {
            if (modManager.get(ModifierType.INFINITY) != null) {
                if (modManager.hasMod(tool, modManager.get(ModifierType.INFINITY))) {
                    pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
                    return null;
                }
            }
        }
        return Modifier.checkAndAdd(p, tool, this, "ender", isCommand);
    }

    public void effect(Player p, ItemStack tool, ProjectileHitEvent e) {
        if (p.hasPermission("minetinker.modifiers.ender.use")) {
            if (modManager.hasMod(tool, this)) {
                if (p.isSneaking()) {
                    Location loc = e.getEntity().getLocation().clone();
                    p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()).add(0, 1, 0));
                    if (this.hasSound) {
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                    }
                    ChatWriter.log(false, p.getDisplayName() + " triggered Ender on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
                }
            }
        }
    }

    public void effect(Player p, ItemStack tool, EntityDamageByEntityEvent e) {
        if (p.hasPermission("minetinker.modifiers.ender.use")) {
            if (modManager.hasMod(tool, this)) {
                if (p.isSneaking()) {
                    Location loc = e.getEntity().getLocation().clone();
                    e.getEntity().teleport(p.getLocation());
                    p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
                    if (this.hasSound) {
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                        p.getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
                    }
                    ChatWriter.log(false, p.getDisplayName() + " triggered Ender on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
                }
            }
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, modManager, ModifierType.ENDER, "Ender", "Modifier_Ender");
    }
}
