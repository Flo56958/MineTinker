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

public class Webbed extends Modifier implements Craftable {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    private final int duration;
    private final double durationMultiplier;
    private final int effectAmplifier;

    public Webbed() {
        super(config.getString("Modifiers.Webbed.name"),
                "[Compressed Cobweb] Slowes Foes!",
                ModifierType.WEBBED,
                ChatColor.WHITE,
                config.getInt("Modifiers.Webbed.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.COBWEB, ChatColor.WHITE + config.getString("Modifiers.Webbed.name_modifier"), 1, Enchantment.DAMAGE_ALL, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD,
                                                ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA)),
                Main.getPlugin());
        this.duration = config.getInt("Modifiers.Webbed.Duration");
        this.durationMultiplier = config.getDouble("Modifiers.Webbed.DurationMultiplier");
        this.effectAmplifier = config.getInt("Modifiers.Webbed.EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, modManager.get(ModifierType.POWER))) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return null;
        }

        return Modifier.checkAndAdd(p, tool, this, "timber", isCommand);
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
        try {
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "Modifier_Webbed"), modManager.get(ModifierType.WEBBED).getModItem()); //init recipe
            newRecipe.shape("WWW", "WWW", "WWW"); //makes recipe
            newRecipe.setIngredient('W', Material.COBWEB); //set ingredients
            Main.getPlugin().getServer().addRecipe(newRecipe); //adds recipe
        } catch (Exception e) {
            ChatWriter.log(true, "Could not register recipe for the Webbed-Modifier!"); //executes if the recipe could not initialize
        }
    }
}
