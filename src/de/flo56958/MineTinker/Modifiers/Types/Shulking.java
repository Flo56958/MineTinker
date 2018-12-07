package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class Shulking extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getMain().getConfigurations().getConfig("Shulking.yml");

    private final int duration;
    private final int effectAmplifier;

    public Shulking() {
        super(config.getString("Shulking.name"),
                "[Special Shulkershell] Makes enemies levitate!",
                ModifierType.SHULKING,
                ChatColor.LIGHT_PURPLE,
                config.getInt("Shulking.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.SHULKER_SHELL, ChatColor.LIGHT_PURPLE + config.getString("Shulking.name_modifier"), 1, Enchantment.DURABILITY, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        this.duration = config.getInt("Shulking.Duration");
        this.effectAmplifier = config.getInt("Shulking.EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "shulking", isCommand);
    }

    public void effect(Player p, ItemStack tool, Entity e) {
        if (!p.hasPermission("minetinker.modifiers.shulking.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (!(e instanceof LivingEntity)) { return; }

        int level = modManager.getModLevel(tool, this);
        int amplifier = this.effectAmplifier * (level - 1);

        LivingEntity ent = (LivingEntity) e;
        ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, this.duration, amplifier, false, false));
        ChatWriter.log(false, p.getDisplayName() + " triggered Shulking on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(config, modManager, ModifierType.SHULKING, "Shulking", "Modifier_Shulking");
    }
}
