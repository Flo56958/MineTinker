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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class Poisonous extends Modifier {

    private static final ModManager modManager = Main.getModManager();
    private static PluginManager pluginManager = Bukkit.getPluginManager();
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    private final int duration;
    private final double durationMultiplier;
    private final int effectAmplifier;


    public Poisonous() {
        super(config.getString("Modifiers.Poisonous.name"),
                "[Enchanted Rotten Flesh] Poisons enemies!",
                ModifierType.POISONOUS,
                ChatColor.DARK_GREEN,
                config.getInt("Modifiers.Poisonous.MaxLevel"),
                ItemGenerator.itemEnchanter(Material.ROTTEN_FLESH, ChatColor.DARK_GREEN + config.getString("Modifiers.Poisonous.name_modifier"), 1, Enchantment.DURABILITY, 1),
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.SWORD)),
                Main.getPlugin());
        this.duration = config.getInt("Modifiers.Poisonous.Duration");
        this.durationMultiplier = config.getDouble("Modifiers.Poisonous.DurationMultiplier");
        this.effectAmplifier = config.getInt("Modifiers.Poisonous.EffectAmplifier");
    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        return checkAndAdd(p, tool, this, "poisonous", isCommand);
    }

    public void effect(Player p, ItemStack tool, EntityDamageByEntityEvent e) {
        if (!p.hasPermission("minetinker.modifiers.poisonous.use")) { return; }
        if (modManager.hasMod(tool, this)) {
            int level = modManager.getModLevel(tool, this);
            if (!e.getEntity().isDead()) {
                if (e.getEntity() instanceof LivingEntity) {
                    LivingEntity ent = (LivingEntity) e.getEntity();
                    int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
                    int amplifier = this.effectAmplifier * (level - 1);
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, false));
                }
            }
        }
    }
}
