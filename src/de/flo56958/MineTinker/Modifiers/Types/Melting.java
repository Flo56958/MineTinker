package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
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

public class Melting extends Modifier implements Enchantable, Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Melting.yml");

    private final double bonusMultiplier;

    public Melting() {
        super(config.getString("Melting.name"),
                "[Enchanted Magma block] Extra damage against burning enemies!",
                ModifierType.MELTING,
                ChatColor.GOLD,
                config.getInt("Melting.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.MAGMA_BLOCK, ChatColor.GOLD + config.getString("Melting.name_modifier"), 1, Enchantment.FIRE_ASPECT, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.CHESTPLATE, ToolType.LEGGINGS)),
                Main.getPlugin());
        this.bonusMultiplier = config.getDouble("Melting.BonusMultiplier");
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

        ChatWriter.log(false, p.getDisplayName() + " triggered Melting on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.melting.craft")) { return; }
        ItemGenerator.createModifierItem(p, this, "Melting");
    }

    public void effect_armor(Player p, ItemStack piece, EntityDamageByEntityEvent e) {
        if (!p.hasPermission("minetinker.modifiers.melting.use")) { return; }
        if (!modManager.hasMod(piece, this)) { return; }
        if (e.getEntity().isDead()) { return; }

        int level = modManager.getModLevel(piece, this);
        if (p.getFireTicks() <= 0) { return; }

        double damage = e.getDamage();
        damage = damage * (1 - this.bonusMultiplier * level);
        e.setDamage(damage);

        p.setFireTicks(0);

        ChatWriter.log(false, p.getDisplayName() + " triggered Melting on " + ItemGenerator.getDisplayName(piece) + ChatColor.GRAY + " (" + piece.getType().toString() + ")!");
    }

    public void effect_armor(Player p, ItemStack piece) {
        if (!p.hasPermission("minetinker.modifiers.melting.use")) { return; }
        if (!modManager.hasMod(piece, this)) { return; }
        if (p.getFireTicks() > 0) {
            p.setFireTicks(0);
            ChatWriter.log(false, p.getDisplayName() + " triggered Melting on " + ItemGenerator.getDisplayName(piece) + ChatColor.GRAY + " (" + piece.getType().toString() + ")!");
        }
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, modManager, ModifierType.MELTING, "Melting", "Modifier_Melting");
    }
}
