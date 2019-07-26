package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ToolLevelUpEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.*;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.nms.NBTHandler;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ModManager {

    //TODO: AUTO-DISCOVER RECIPES
    public final ArrayList<NamespacedKey> recipe_Namespaces = new ArrayList<>();

    private static FileConfiguration config;
    private static FileConfiguration layout;
    private final static NBTHandler nbt;

    static {
        nbt = NBTUtils.getHandler();
        config = Main.getPlugin().getConfig();

        layout = ConfigurationManager.getConfig("layout.yml");
        layout.options().copyDefaults(true);
        layout.addDefault("UseRomans.Level", true);
        layout.addDefault("UseRomans.Exp", false);
        layout.addDefault("UseRomans.FreeSlots", false);
        layout.addDefault("UseRomans.ModifierLevels", true);

        ArrayList<String> loreLayout = new ArrayList<>();
        loreLayout.add("%GOLD%Level %WHITE%%LEVEL%");
        loreLayout.add("%GOLD%Exp: %WHITE%%EXP% / %NEXT_LEVEL_EXP%");
        loreLayout.add("%WHITE%Free Modifier Slots: %FREE_SLOTS%");
        loreLayout.add("%WHITE%Modifiers:");
        loreLayout.add("%MODIFIERS%");
        layout.addDefault("LoreLayout", loreLayout);

        layout.addDefault("ModifierLayout", "%MODIFIER% %WHITE%%MODLEVEL%");

        ConfigurationManager.saveConfig(layout);
    }
    /**
     * stores the list of all MineTinker modifiers
     */
    private final ArrayList<Modifier> allMods = new ArrayList<>();
    /**
     * stores the list of allowed modifiers
     */
    private final ArrayList<Modifier> mods = new ArrayList<>();

    /**
     * sublist of mods which contains all modifiers that are crafted through the bookshelf
     */
    private final ArrayList<Modifier> enchantableMods = new ArrayList<>();

    private static ModManager instance;

    private List<String> loreScheme;
    private String modifierLayout;

    private boolean allowBookConvert;

    /**
     * Class constructor (no parameters)
     */
    private ModManager() {
        this.loreScheme = layout.getStringList("LoreLayout");

        for (int i = 0; i < loreScheme.size(); i++) {
            loreScheme.set(i, ChatWriter.addColors(loreScheme.get(i)));
        }

        this.modifierLayout = ChatWriter.addColors(layout.getString("ModifierLayout"));
    }

    public NBTHandler getNBTHandler() {
        return nbt;
    }

    /**
     * get the instance that contains the modifier list (VERY IMPORTANT)
     *
     * @return the instance
     */
    public synchronized static ModManager instance() {
        synchronized (ModManager.class) {
            if (instance == null) {
                instance = new ModManager();
                instance.init();
            }
        }

        return instance;
    }

    public void reload() {
        config = Main.getPlugin().getConfig();
        layout = ConfigurationManager.getConfig("layout.yml");

    	for (Modifier m : allMods) {
    		m.reload();
    		
    		if (m.isAllowed()) {
                register(m);
            } else {
                unregister(m);
            }
    	}

    	allMods.sort(Comparator.comparing(Modifier::getName));
        mods.sort(Comparator.comparing(Modifier::getName));


        this.enchantableMods.clear();
    	
    	for (Modifier m : this.mods) {
            if (m instanceof Enchantable) {
                this.enchantableMods.add(m);
            }

            m.registerCraftingRecipe();
        }

        this.loreScheme = layout.getStringList("LoreLayout");

        for (int i = 0; i < loreScheme.size(); i++) {
            loreScheme.set(i, ChatWriter.addColors(loreScheme.get(i)));
        }

        this.modifierLayout = ChatWriter.addColors(layout.getString("ModifierLayout"));
        this.allowBookConvert = config.getBoolean("ConvertBookToModifier");
    }

    /**
     * checks and loads all modifiers with configurations settings into memory
     */
    private void init() {
        allMods.add(AntiArrowPlating.instance());
        allMods.add(AntiBlastPlating.instance());
        allMods.add(Insulating.instance());
        allMods.add(Aquaphilic.instance());
    	allMods.add(AutoSmelt.instance());
    	allMods.add(Beheading.instance());
    	allMods.add(Channeling.instance());
    	allMods.add(Directing.instance());
    	allMods.add(Ender.instance());
    	allMods.add(Experienced.instance());
    	allMods.add(ExtraModifier.instance());
    	allMods.add(Fiery.instance());
    	allMods.add(Freezing.instance());
    	allMods.add(Glowing.instance());
    	allMods.add(Haste.instance());
    	allMods.add(Infinity.instance());
    	allMods.add(Knockback.instance());
    	allMods.add(Lifesteal.instance());
    	allMods.add(LightWeight.instance());
        allMods.add(Luck.instance());
        allMods.add(Melting.instance());
        allMods.add(Poisonous.instance());
        allMods.add(Power.instance());
        allMods.add(Propelling.instance());
        allMods.add(Protecting.instance());
        allMods.add(Reinforced.instance());
        allMods.add(SelfRepair.instance());
        allMods.add(Sharpness.instance());
        allMods.add(Shulking.instance());
        allMods.add(SilkTouch.instance());
        allMods.add(Smite.instance());
        allMods.add(Soulbound.instance());
        allMods.add(Speedy.instance());
        allMods.add(SpidersBane.instance());
        allMods.add(Sweeping.instance());
        allMods.add(Tanky.instance());
        allMods.add(Thorned.instance());
        allMods.add(Timber.instance());
        allMods.add(Webbed.instance());

        if (NBTUtils.isOneFourteenCompatible()) {
            allMods.add(Piercing.instance());
            allMods.add(MultiShot.instance());
        }

        ConfigurationManager.reload(); //To load all Modifier-Configurations in
        reload();
    }

    /**
     * register a new modifier to the list
     *
     * @param mod the modifier instance
     */
    public void register(Modifier mod) {
    	if (!mods.contains(mod)) {
	        mods.add(mod);

	        String mes = "%GREEN%Registered the %MOD% %GREEN%modifier from %PLUGIN%.";
	        mes = ChatWriter.addColors(mes);
	        mes = mes.replaceAll("%MOD%", mod.getColor() + mod.getName());
	        mes = mes.replaceAll("%PLUGIN%", Main.getPlugin().getName());

	        ChatWriter.logColor(mes);
    	}
    }

    /**
     * unregisters the Modifier from the list
     * @param mod the modifier instance
     */
    public void unregister(Modifier mod) {
    	 mods.remove(mod);

         String mes = "%GREEN%Unregistered the %MOD% %GREEN%modifier from %PLUGIN%.";
         mes = ChatWriter.addColors(mes);
         mes = mes.replaceAll("%MOD%", mod.getColor() + mod.getName());
         mes = mes.replaceAll("%PLUGIN%", Main.getPlugin().getName());

         ChatWriter.logColor(mes);
    }

    /**
     * should we let the player convert enchanted books to modifier items
     * @return
     */
    public boolean allowBookToModifier() {
        return this.allowBookConvert;
    }

    /**
     * get all the modifiers in the list
     *
     * @return the modifier list
     */
    public List<Modifier> getAllowedMods() {
        return this.mods;
    }

    public List<Modifier> getAllMods() {
        return this.allMods;
    }

    /**
     * get all the enchantable modifiers in the list
     *
     * @return the enchantable modifier list
     */
    public List<Modifier> getEnchantableMods() {
        return this.enchantableMods;
    }

    /**
     * add a specified modifier to a tool
     *
     * @param is the item to add the modifier to
     * @param mod the modifier to add
     */
    void addMod(ItemStack is, Modifier mod) {
        nbt.setInt(is, mod.getNBTKey(), getModLevel(is, mod) + 1);
        rewriteLore(is);
    }

    /**
     * get the level of a specified modifier on a tool
     *
     * @param is the item
     * @param mod the modifier
     */
    public int getModLevel(ItemStack is, Modifier mod) {
        return nbt.getInt(is, mod.getNBTKey());
    }

    /**
     * remove a modifier from a tool
     *
     * @param is the item to remove the modifier from
     * @param mod the modifier to remove
     */
    public void removeMod(ItemStack is, Modifier mod) {
        nbt.removeTag(is, mod.getNBTKey());
        mod.removeMod(is);
        rewriteLore(is);
    }

    /**
     * gets how many free slots the tool has
     *
     * @param is the item to get the information from
     */
    public int getFreeSlots(ItemStack is) {
        return nbt.getInt(is, "FreeSlots");
    }

    /**
     * sets how many free slots the tool has
     *
     * @param is the item to set the information to
     */
    public void setFreeSlots(ItemStack is, int freeSlots) {
        nbt.setInt(is, "FreeSlots", freeSlots);
        rewriteLore(is);
    }

    /**
     * gets what level the tool has
     *
     * @param is the item to get the information from
     */
    public int getLevel(ItemStack is) {
        return nbt.getInt(is, "Level");
    }

    /**
     * sets the level of the tool
     *
     * @param is the item to get the information from
     */
    private void setLevel(ItemStack is, int level) {
        nbt.setInt(is, "Level", level);
    }

    /**
     * gets the amount of exp the tool has
     *
     * @param is the item to get the information from
     */
    public long getExp(ItemStack is) {
        return nbt.getLong(is, "Exp");
    }

    /**
     * sets the exp amount of the tool
     *
     * @param is the item to get the information from
     */
    private void setExp(ItemStack is, long exp) {
        nbt.setLong(is, "Exp", exp);
    }

    /**
     * @param tool The Tool that is checked
     * @param mod The modifier that is checked in tool
     * @return if the tool has the mod
     */
    public boolean hasMod(ItemStack tool, Modifier mod) {
        return mod.isAllowed() && nbt.hasTag(tool, mod.getNBTKey());
    }

    /**
     * calculates the required exp for the given level
     * @param level
     * @return long value of the exp required
     */
    public long getNextLevelReq(int level) {
        if (config.getBoolean("ProgressionIsLinear")) {
            return (long) (Main.getPlugin().getConfig().getInt("LevelStep") * Main.getPlugin().getConfig().getDouble("LevelFactor") * (level - 1));
        } else {
            return (long) (Main.getPlugin().getConfig().getInt("LevelStep") * Math.pow(Main.getPlugin().getConfig().getDouble("LevelFactor"), (double) (level - 1)));
        }
    }

    /**
     *
     * @param p Player that uses the tool
     * @param tool tool that needs to get exp
     * @param amount how much exp should the tool get
     */
    public void addExp(Player p, ItemStack tool, int amount) {
        if (amount == 0) {
            return;
        }

        boolean LevelUp = false;

        int level = this.getLevel(tool);
        long exp = this.getExp(tool);

        if (level == -1 || exp == -1) {
            return;
        }

        if (exp + 1 < 0 || level + 1 < 0) {
            if (Main.getPlugin().getConfig().getBoolean("ResetAtIntOverflow")) { //secures a "good" exp-system if the Values get to big
                level = 1;
                setLevel(tool, level);
                exp = 0;
                LevelUp = true;
            } else {
                return;
            }
        }

        exp = exp + amount;
        if (exp >= getNextLevelReq(level)) { //tests for a level up
            level++;
            setLevel(tool, level);
            LevelUp = true;
        }

        if (config.getBoolean("actionbar-on-exp-gain")) {
            ChatWriter.sendActionBar(p, ChatColor.translateAlternateColorCodes('&', "&a+" + amount + " exp gained"));
        }

        setExp(tool, exp);
        rewriteLore(tool);

        if (LevelUp) {
            Bukkit.getPluginManager().callEvent(new ToolLevelUpEvent(p, tool));
        }
    }

    /**
     * @param armor the ItemStack
     * @return if the ItemStack is viable as MineTinker-Armor
     */
    public boolean isArmorViable(ItemStack armor) {
        return armor != null && nbt.hasTag(armor, "IdentifierArmor");
    }

    /**
     * @param tool the ItemStack
     * @return if the ItemStack is viable as MineTinker-Tool
     */
    public boolean isToolViable(ItemStack tool) {
        return tool != null && nbt.hasTag(tool, "IdentifierTool");
    }

    /**
     * @param wand the ItemStack
     * @return if the ItemStack is viable as MineTinker-Builderswand
     */
    public boolean isWandViable(ItemStack wand) {
        return wand != null && nbt.hasTag(wand, "IdentifierBuilderswand");
    }

    /**
     * Updates the lore of the Item as everything is stored in the NBT-Data
     * @param is
     */
    private void rewriteLore(ItemStack is) {
        if (!Main.getPlugin().getConfig().getBoolean("EnableLore")) {
            return;
        }

        ArrayList<String> lore = new ArrayList<>(this.loreScheme);

        long exp = getExp(is);
        int level = getLevel(is);
        long nextLevelReq = getNextLevelReq(level);
        int freeSlots = getFreeSlots(is);

        String exp_ = layout.getBoolean("UseRomans.Exp") ? ChatWriter.toRomanNumerals((int) exp) : String.valueOf(exp);
        String level_ = layout.getBoolean("UseRomans.Level") ? ChatWriter.toRomanNumerals(level) : String.valueOf(level);
        String nextLevelReq_ = layout.getBoolean("UseRomans.Exp") ? ChatWriter.toRomanNumerals((int) nextLevelReq) : String.valueOf(nextLevelReq);
        String freeSlots_ = layout.getBoolean("UseRomans.FreeSlots") ? ChatWriter.toRomanNumerals(freeSlots) : String.valueOf(freeSlots);


        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            s = s.replaceAll("%EXP%", "" + exp_);
            s = s.replaceAll("%LEVEL%", "" + level_);
            s = s.replaceAll("%NEXT_LEVEL_EXP%", "" + nextLevelReq_);
            s = s.replaceAll("%FREE_SLOTS%", "" + freeSlots_);

            lore.set(i, s);
        }

        int index = -1;
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);

            if (s.contains("%MODIFIERS%")) {
                index = i;
                break;
            }
        }

        if (index == -1) return;

        lore.remove(index);

        for (Modifier m : this.mods) {
            if (nbt.hasTag(is, m.getNBTKey())) {
                int modLevel = getModLevel(is, m);
                String modLevel_ = layout.getBoolean("UseRomans.ModifierLevels") ? ChatWriter.toRomanNumerals(modLevel) : String.valueOf(modLevel);

                String s = this.modifierLayout;
                s = s.replaceAll("%MODIFIER%", m.getColor() + m.getName());
                s = s.replaceAll("%MODLEVEL%", modLevel_);

                lore.add(index++, s);
            }
        }

        ItemMeta meta = is.getItemMeta();

        /*
         * For mcMMO-Superbreaker and other Skills
         */

        if (meta != null) {
            ArrayList<String> oldLore = (ArrayList<String>) meta.getLore();

            if (oldLore != null && oldLore.size() > 0 && oldLore.get(oldLore.size() - 1).equals("mcMMO Ability Tool")) {
                lore.add("mcMMO Ability Tool");
            }

            meta.setLore(lore);
            is.setItemMeta(meta);
        }
    }

    /**
     * converts a given ItemStack into its MineTinker equivalent
     * @param is the MineTinker equivalent
     */
    public void convertItemStack(ItemStack is) {
        Material m = is.getType();

        if ((ToolType.AXE.contains(m)
                || ToolType.BOW.contains(m)
                || ToolType.CROSSBOW.contains(m)
                || ToolType.HOE.contains(m)
                || ToolType.PICKAXE.contains(m)
                || ToolType.SHOVEL.contains(m)
                || ToolType.SWORD.contains(m)
                || ToolType.TRIDENT.contains(m)
                || ToolType.SHEARS.contains(m)
                || ToolType.FISHINGROD.contains(m)) && !isWandViable(is)) {
            nbt.setInt(is, "IdentifierTool", 0);
        } else if (ToolType.BOOTS.contains(m)
                || ToolType.CHESTPLATE.contains(m)
                || ToolType.HELMET.contains(m)
                || ToolType.LEGGINGS.contains(m)
                || ToolType.ELYTRA.contains(m)) {
            nbt.setInt(is, "IdentifierArmor", 0);
        } else return;

        setExp(is, 0);
        setLevel(is, 1);
        setFreeSlots(is, config.getInt("StartingModifierSlots"));
        rewriteLore(is);

        ItemMeta meta = is.getItemMeta();

        if (meta != null) {
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                Modifier modifier = getModifierFromEnchantment(entry.getKey());

                if (modifier == null) {
                    continue;
                }

                meta.removeEnchant(entry.getKey());

                for (int i = 0; i < entry.getValue(); i++) {
                    addMod(is, modifier);
                }
            }

            addArmorAttributes(is);

            if (meta.getAttributeModifiers() == null) {
                return;
            }

            for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : meta.getAttributeModifiers().asMap().entrySet()) {
                Modifier modifier = getModifierFromAttribute(entry.getKey());

                if (modifier == null) {
                    continue;
                }

                meta.removeAttributeModifier(entry.getKey());

                addMod(is, modifier);
            }
        }
    }

    public void addArmorAttributes(ItemStack is) {
        double armor;
        double toughness = 0.0d;

        switch (is.getType()) {
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case GOLDEN_BOOTS:
            case LEATHER_HELMET:
                armor = 1.0d;
                break;
            case IRON_BOOTS:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case TURTLE_HELMET:
            case LEATHER_LEGGINGS:
                armor = 2.0d;
                break;
            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
                armor = 3.0d;
                toughness = 2.0d;
                break;
            case LEATHER_CHESTPLATE:
            case GOLDEN_LEGGINGS:
                armor = 3.0d;
                break;
            case CHAINMAIL_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case IRON_LEGGINGS:
                armor = 5.0d;
                break;
            case IRON_CHESTPLATE:
                armor = 6.0d;
                break;
            case DIAMOND_CHESTPLATE:
                armor = 8.0d;
                toughness = 2.0d;
                break;
            case CHAINMAIL_LEGGINGS:
                armor = 4.0d;
                break;
            case DIAMOND_LEGGINGS:
                armor = 6.0d;
                toughness = 2.0d;
                break;
            default:
                return;
        }

        ItemMeta meta = is.getItemMeta();

        if (meta != null) {
            AttributeModifier armorAM;
            AttributeModifier toughnessAM;

            if (ToolType.BOOTS.contains(is.getType())) {
                armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
                toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
            } else if (ToolType.CHESTPLATE.contains(is.getType())) {
                armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
                toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
            } else if (ToolType.HELMET.contains(is.getType())) {
                armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
                toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
            } else if (ToolType.LEGGINGS.contains(is.getType())) {
                armorAM = new AttributeModifier(UUID.randomUUID(), "generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
                toughnessAM = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
            } else return;

            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorAM);

            if (toughness > 0.0d) {
                meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
                meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessAM);
            }

            if (Main.getPlugin().getConfig().getBoolean("HideAttributes")) {
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }

            is.setItemMeta(meta);
        }
    }

    public ItemStack createModifierItem(Material m, String name, String description, Modifier mod) {
        ItemStack is = new ItemStack(m, 1);
        ItemMeta meta = is.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);

            ArrayList<String> lore = new ArrayList<>();
            lore.add(description);
            meta.setLore(lore);

            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

            is.setItemMeta(meta);
        }

        nbt.setString(is, "modifierItem", mod.getNBTKey());
        nbt.setStringList(is, "CanPlaceOn", "minecraft:air");

        return is;
    }

    /**
     * @param item the ItemStack
     * @return if the ItemStack is viable as MineTinker-Modifier-Item
     */
    public boolean isModifierItem(ItemStack item) {
        return nbt.hasTag(item, "modifierItem") || item.getType().equals(Experienced.instance().getModItem().getType()) || item.getType().equals(ExtraModifier.instance().getModItem().getType());
    }

    /**
     * @param item the ItemStack
     * @return the Modifier of the Modifier-Item (NULL if not found)
     */
    public Modifier getModifierFromItem(ItemStack item) {
        if (!isModifierItem(item)) {
            return null;
        }

        if (item.getType().equals(Experienced.instance().getModItem().getType())) {
            return Experienced.instance();
        }

        if (item.getType().equals(ExtraModifier.instance().getModItem().getType())
                && !nbt.hasTag(item, "modifierItem")) {
            return ExtraModifier.instance();
        }

        if (!nbt.hasTag(item, "modifierItem")) {
            return null;
        }

        String name = Objects.requireNonNull(nbt.getString(item, "modifierItem"));

        for (Modifier m : mods) {
            if (m.getNBTKey() != null && m.getNBTKey().equals(name)) {
                return m;
            }
        }

        return null;
    }

    /**
     * Gets the first found modifier that applies the supplied enchantment.
     *
     * @param enchantment
     * @return
     */
    public Modifier getModifierFromEnchantment(Enchantment enchantment) {
        for (Modifier modifier : getAllMods()) {
            if (modifier.getAppliedEnchantments().contains(enchantment)) {
                return modifier;
            }
        }

        return null;
    }

    /**
     * Gets the first found modifier that applies the supplied attribute.
     *
     * @param attribute
     * @return
     */
    public Modifier getModifierFromAttribute(Attribute attribute) {
        for (Modifier modifier : getAllMods()) {
            if (modifier.getAppliedAttributes().contains(attribute)) {
                return modifier;
            }
        }

        return null;
    }

    /**
     * Checks the durability of the Tool
     * @param e the Event (implements Cancelable)
     * @param p the Player
     * @param tool the Tool
     * @return false: if broken; true: if enough durability
     */
    public boolean durabilityCheck(Cancellable e, Player p, ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();

        if (meta instanceof Damageable) {
            if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 2 && config.getBoolean("UnbreakableTools")) {
                e.setCancelled(true);

                if (config.getBoolean("Sound.OnBreaking")) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
                }

                return false;
            }
        }

        return true;
    }
}