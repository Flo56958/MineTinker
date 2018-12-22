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
import de.flo56958.MineTinker.Modifiers.Enchantable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.modifiers_Config;

public class Poisonous extends Modifier implements Enchantable, Craftable {
	
    private final int duration;
    private final double durationMultiplier;
    private final int effectAmplifier;

    public Poisonous() {
        super(ModifierType.POISONOUS,
                ChatColor.DARK_GREEN,
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        
        FileConfiguration config = getConfig();
        
        init(config.getString("Poisonous.name"),
                "[" + config.getString("Poisonous.name_modifier") + "] " + config.getString("Poisonous.description"),
                config.getInt("Poisonous.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.ROTTEN_FLESH, ChatColor.DARK_GREEN + config.getString("Poisonous.name_modifier"), 1, Enchantment.DURABILITY, 1));
        
        this.duration = config.getInt("Poisonous.Duration");
        this.durationMultiplier = config.getDouble("Poisonous.DurationMultiplier");
        this.effectAmplifier = config.getInt("Poisonous.EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "poisonous", isCommand);
    }

    public void effect(Player p, ItemStack tool, Entity e) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.use")) { return; }
        if (!modManager.hasMod(tool, this)) { return; }
        if (!(e instanceof LivingEntity)) { return; }

        int level = modManager.getModLevel(tool, this);
        LivingEntity ent = (LivingEntity) e;
        int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
        int amplifier = this.effectAmplifier * (level - 1);
        ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
        ChatWriter.log(false, p.getDisplayName() + " triggered Poisonous on " + ItemGenerator.getDisplayName(tool) + ChatColor.GRAY + " (" + tool.getType().toString() + ")!");
    }

    @Override
    public void enchantItem(Player p, ItemStack item) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.craft")) { return; }
        _createModifierItem(getConfig(), p, this, "Poisonous");
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Poisonous", "Modifier_Poisonous");
    }
    
    private static FileConfiguration getConfig() {
    	return Main.getConfigurations().getConfig(modifiers_Config.Poisonous);
    }
}
