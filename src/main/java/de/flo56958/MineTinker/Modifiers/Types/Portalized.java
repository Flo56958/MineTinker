package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTProjectileHitEvent;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Craftable;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.Modifiers_Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * THIS MODIFIER IS FAR FROM FINISHED AND SHOULD NOT BE ENABLED!
 */
public class Portalized extends Modifier implements Craftable, Listener {

    //TODO: Add special Arrows
    //TODO: Add special behaviour when hitting entities

    private static Portalized instance;

    public static Portalized instance() {
        if (instance == null) instance = new Portalized();
        return instance;
    }

    private HashMap<ArmorStand, ArmorStand> portals = new HashMap<>();
    private HashMap<Player, LinkedList<ArmorStand>> playerPortals = new HashMap<>();

    private ItemStack portalHead = new ItemStack(Material.NETHER_PORTAL);

    private Portalized() {
        super(ModifierType.PORTALIZED,
                new ArrayList<>(Collections.singletonList(ToolType.BOW)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public void registerCraftingRecipe() {

    }

    @Override
    public ItemStack applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, Ender.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return null;
        }
        return Modifier.checkAndAdd(p, tool, this, "portalized", isCommand);
    }

    @Override
    public void removeMod(ItemStack tool) { }

    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);

        String key = "Portalized";
        config.addDefault(key + ".allowed", false);
        config.addDefault(key + ".name", key);
        config.addDefault(key + ".name_modifier", "Portalizing Eye");
        config.addDefault(key + ".modifier_item", "ENDER_EYE"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Creates Portals!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Portalizing-Modifier");
        config.addDefault(key + ".Color", "%WHITE%");
        config.addDefault(key + ".MaxLevel", 1);
        config.addDefault(key + ".Sound", true); //#Enderman-Teleport-Sound
        config.addDefault(key + ".Recipe.Enabled", false);

        ConfigurationManager.saveConfig(config);

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] " + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @EventHandler
    public void onArrowLand(MTProjectileHitEvent e) {
        if (!this.isAllowed()) return;

        Player p = e.getPlayer();

        LinkedList<ArmorStand> portals = playerPortals.get(p);

        if (portals == null) {
            portals = new LinkedList<>();
        }

        if (e.getEvent().getHitBlock() == null) return;

        ArmorStand newPortal = (ArmorStand) p.getWorld().spawnEntity(e.getEvent().getHitBlock().getLocation(), EntityType.ARMOR_STAND);
        newPortal.setHelmet(portalHead);

        newPortal.setInvulnerable(true);
        newPortal.setGravity(false);
        newPortal.setCanPickupItems(false);
        newPortal.setSilent(true);
        newPortal.setMetadata("MineTinker", new FixedMetadataValue(Main.getPlugin(), "Portal"));
        newPortal.setVisible(false);
        newPortal.setCustomNameVisible(true);

        switch (portals.size()) {
            case 0:
                portals.add(newPortal);
                playerPortals.put(p, portals);
                break;
            case 1:
                portals.addLast(newPortal);
                break;
            case 2:
                ArmorStand toRemove = portals.removeFirst();
                portals.addLast(newPortal);
                toRemove.remove();
                this.portals.remove(toRemove);
                this.portals.put(portals.getFirst(), newPortal);
                this.portals.put(newPortal, portals.getFirst());
                break;
        }

        newPortal.setCustomName(p.getDisplayName() + " : " + portals.getFirst().getLocation().toString());
        portals.getFirst().setCustomName(p.getDisplayName() + " : " + newPortal.getLocation().toString());
    }
    //TODO: Löschen wenn neugeladen wird

    @EventHandler
    public void onPortalClick(EntityDamageByEntityEvent e) {
        //if (!(e.getDamager() instanceof Player)) return;
        //if (!(e.getEntity() instanceof ArmorStand)) return;

        //System.out.println("hi!");
    }

    @EventHandler
    public void PlayerInPortal(AreaEffectCloudApplyEvent e) {
        //System.out.println("hiww");
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Portalized.allowed");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Portalized);
    }

}
