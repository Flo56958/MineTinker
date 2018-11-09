package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
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

public class Ender extends Modifier {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    private final boolean hasSound;

    public Ender() {
        super(config.getString("Modifiers.Ender.name"),
                "[Special Endereye] Teleports you while sneaking to the arrow location!",
                ModifierType.ENDER,
                ChatColor.DARK_GREEN,
                1,
                ItemGenerator.itemEnchanter(Material.ENDER_EYE, ChatColor.DARK_GREEN + config.getString("Modifiers.Ender.name_modifier"), 1, Enchantment.DURABILITY, 1),
                new ArrayList<>(Collections.singletonList(ToolType.BOW)),
                Main.getPlugin());
        this.hasSound = config.getBoolean("Modifiers.Ender.Sound");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
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
                }
            }
        }
    }
}
