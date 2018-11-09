package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Melting extends Modifier {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    private final double bonusMultiplier;

    public Melting() {
        super(config.getString("Modifiers.Melting.name"),
                "[Enchanted Magma block] Extra damage against burning enemies!",
                ModifierType.MELTING,
                ChatColor.GOLD,
                config.getInt("Modifiers.Melting.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.MAGMA_BLOCK, ChatColor.GOLD + config.getString("Modifiers.Melting.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
        this.bonusMultiplier = config.getDouble("Modifiers.Melting.BonusMultiplier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "melting", isCommand);
    }

    public void effect(Player p, ItemStack tool, EntityDamageByEntityEvent e) {
        if (!p.hasPermission("minetinker.modifiers.melting.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (e.getEntity().isDead()) { return; }
        if (!(e.getEntity() instanceof LivingEntity)) { return; }

        int level = modManager.getModLevel(tool, this);
        LivingEntity ent = (LivingEntity) e.getEntity();

        if (ent.getFireTicks() == 0) { return; }

        double damage = e.getDamage();
        damage = damage * (1 + this.bonusMultiplier * level);
        e.setDamage(damage);
    }
}
