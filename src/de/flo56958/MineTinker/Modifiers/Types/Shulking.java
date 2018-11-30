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
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class Shulking extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    private final int duration;
    private final int effectAmplifier;

    public Shulking() {
        super(config.getString("Modifiers.Shulking.name"),
                "[Special Shulkershell] Makes enemies levitate!",
                ModifierType.SHULKING,
                ChatColor.LIGHT_PURPLE,
                config.getInt("Modifiers.Shulking.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.SHULKER_SHELL, ChatColor.LIGHT_PURPLE + config.getString("Modifiers.Shulking.name_modifier"), 1, Enchantment.DURABILITY, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        this.duration = config.getInt("Modifiers.Shulking.Duration");
        this.effectAmplifier = config.getInt("Modifiers.Shulking.EffectAmplifier");
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
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Shulking"), modManager.get(ModifierType.SHULKING).getModItem()); //init recipe
            newRecipe.shape(" S ", " C ", " S "); //makes recipe
            newRecipe.setIngredient('S', Material.SHULKER_SHELL); //set ingredients
            newRecipe.setIngredient('C', Material.CHORUS_FRUIT);
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Shulking-Modifier!"); //executes if the recipe could not initialize
        }
    }
}
