package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class Glowing extends Modifier implements Craftable {

    private int duration;
    private double durationMultiplier;


    public Glowing() {
        super(ModifierType.GLOWING,
                ChatColor.YELLOW,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
    }
    
    public void reload() {
    	FileConfiguration config = getConfig();
        
        init(config.getString("Glowing.name"),
                "[" + config.getString("Glowing.name_modifier") + "] " + config.getString("Glowing.description"),
                config.getInt("Glowing.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.GLOWSTONE, ChatColor.YELLOW + config.getString("Glowing.name_modifier"), 1, Enchantment.DURABILITY, 1));
        
        this.duration = config.getInt("Glowing.Duration");
        this.durationMultiplier = config.getDouble("Glowing.DurationMultiplier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "glowing", isCommand);
    }

    public void effect(Player p, ItemStack tool, EntityDamageByEntityEvent e) {
        if (!p.hasPermission("minetinker.modifiers.glowing.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (!(e.getEntity() instanceof LivingEntity)) { return; }

        LivingEntity ent = (LivingEntity) e.getEntity();
        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (modManager.getModLevel(tool, this) - 1)));
        ent.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, false, false));
        ChatWriter.log(false, p.getDisplayName() + " triggered Glowing on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Glowing", "Modifier_Glowing");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Glowing);
    }
}
