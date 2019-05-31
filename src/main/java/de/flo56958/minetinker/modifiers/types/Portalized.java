package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.data.ModifierFailCause;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTProjectileHitEvent;
import de.flo56958.minetinker.events.ModifierFailEvent;
import de.flo56958.minetinker.Main;
import de.flo56958.minetinker.modifiers.Craftable;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utilities.ChatWriter;
import de.flo56958.minetinker.utilities.ConfigurationManager;
import de.flo56958.minetinker.utilities.Modifiers_Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

/**
 * THIS MODIFIER IS FAR FROM FINISHED AND SHOULD NOT BE ENABLED!
 * Intended effect:
 *    - Bow should shoot portals that a player can pass through (compare to Portal [Videogame])
 */
public class Portalized extends Modifier implements Craftable, Listener {

    //TODO: Add special Arrows
    //TODO: Add special behaviour when hitting entities

    private static Portalized instance;

    public static Portalized instance() {
        synchronized (Portalized.class) {
            if (instance == null) instance = new Portalized();
        }
        return instance;
    }

    private final HashMap<ArmorStand, ArmorStand> portals = new HashMap<>();
    private final HashMap<Player, LinkedList<ArmorStand>> playerPortals = new HashMap<>();

    private final ItemStack portalHead = new ItemStack(Material.NETHER_PORTAL);

    private Portalized() {
        super(ModifierType.PORTALIZED,
                new ArrayList<>(Collections.singletonList(ToolType.BOW)),
                Main.getPlugin());
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        return new ArrayList<>();
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

        String name = config.getString(key + ".name");
        String modName = config.getString(key + ".name_modifier");
        String description = config.getString(key + ".description");
        String color = config.getString(key + ".Color");
        int maxLevel = config.getInt(key + ".MaxLevel");
        String modItem = config.getString(key + ".modifier_item");
        String modDescription = config.getString(key + ".description_modifier");

        if (name == null || modName == null || description == null || color == null || modItem == null || modDescription == null) return;

        init(name, "[" + modName + "] " + description, ChatWriter.getColor(color), maxLevel,
                modManager.createModifierItem(Material.getMaterial(modItem), ChatWriter.getColor(color) + modName,
                        ChatWriter.addColors(modDescription), this));

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
        newPortal.setMetadata("minetinker", new FixedMetadataValue(Main.getPlugin(), "Portal"));
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
    //TODO: Delete Portals when server gets reloaded or closed

    @EventHandler
    public void onPortalClick(EntityDamageByEntityEvent e) {
        if (!this.isAllowed()) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof ArmorStand)) return;
    }

    @EventHandler
    public void PlayerInPortal(AreaEffectCloudApplyEvent e) {
        if (!this.isAllowed()) return;
    }

    @Override
    public boolean isAllowed() {
        return getConfig().getBoolean("Portalized.allowed");
    }

    private static FileConfiguration getConfig() {
        return ConfigurationManager.getConfig(Modifiers_Config.Portalized);
    }

}
