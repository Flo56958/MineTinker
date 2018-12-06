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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class Glowing extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();
    private static final FileConfiguration recipesConfig = Main.getMain().getRecipeConfig();

    private final int duration;
    private final double durationMultiplier;


    public Glowing() {
        super(config.getString("Modifiers.Glowing.name"),
                "[Ender-Glowstone] Makes Enemies glow!",
                ModifierType.GLOWING,
                ChatColor.YELLOW,
                config.getInt("Modifiers.Glowing.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.GLOWSTONE, ChatColor.YELLOW + config.getString("Modifiers.Glowing.name_modifier"), 1, Enchantment.DURABILITY, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
        this.duration = config.getInt("Modifiers.Glowing.Duration");
        this.durationMultiplier = config.getDouble("Modifiers.Glowing.DurationMultiplier");
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
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Glowing"), modManager.get(ModifierType.GLOWING).getModItem()); //init recipe
            String top = recipesConfig.getString("Recipes.Glowing.Top");
            String middle = recipesConfig.getString("Recipes.Glowing.Middle");
            String bottom = recipesConfig.getString("Recipes.Glowing.Bottom");
            ConfigurationSection materials = recipesConfig.getConfigurationSection("Recipes.Glowing.Materials");
            newRecipe.shape(top, middle, bottom); //makes recipe
            for (String key : materials.getKeys(false)) {
                newRecipe.setIngredient(key.charAt(0), Material.getMaterial(materials.getString(key)));
            }
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Glowing-Modifier!"); //executes if the recipe could not initialize
        }
    }
}
