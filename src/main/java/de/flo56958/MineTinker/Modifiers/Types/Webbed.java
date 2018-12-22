package de.flo56958.MineTinker.Modifiers.Types;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;

public class Webbed extends Modifier implements Craftable {

    private int duration;
    private double durationMultiplier;
    private int effectAmplifier;

    public Webbed() {
        super(ModifierType.WEBBED,
                ChatColor.WHITE,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
    }
    
    public void reload() {
        FileConfiguration config = getConfig();
        
        init(getConfig().getString("Webbed.name"),
                "[" + getConfig().getString("Webbed.name_modifier") + "] " + getConfig().getString("Webbed.description"),
                getConfig().getInt("Webbed.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.COBWEB, ChatColor.WHITE + getConfig().getString("Webbed.name_modifier"), 1, Enchantment.DAMAGE_ALL, 1));
        
        this.duration = config.getInt("Webbed.Duration");
        this.durationMultiplier = config.getDouble("Webbed.DurationMultiplier");
        this.effectAmplifier = config.getInt("Webbed.EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return Modifier.checkAndAdd(p, tool, this, "webbed", isCommand);
    }

    public void effect(Player p, ItemStack tool, Entity e) {
        if (!p.hasPermission("minetinker.modifiers.webbed.use")) { return; }
        if (e.isDead()) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (!(e instanceof LivingEntity)) { return; }

        int level = modManager.getModLevel(tool, this);

        LivingEntity ent = (LivingEntity) e;
        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
        int amplifier = this.effectAmplifier * (level - 1) / 2;

        ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, amplifier, false, false));
        ChatWriter.log(false, p.getDisplayName() + " triggered Webbed on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Webbed", "Modifier_Webbed");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(Modifiers_Config.Webbed);
    }
    
    public boolean isAllowed() {
    	return getConfig().isBoolean("Webbed.allowed");
    }
}
