package de.flo56958.MineTinker.Modifiers;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ToolLevelUpEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.*;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModManager {

    public ArrayList<NamespacedKey> recipe_Namespaces = new ArrayList<>();

    private static FileConfiguration config;
    private static FileConfiguration layout;

    static {
        config = Main.getPlugin().getConfig();

        layout = ConfigurationManager.getConfig("layout.yml");
        layout.options().copyDefaults(true);

        ArrayList<String> loreLayout = new ArrayList<>();
        loreLayout.add("%GOLD%Level: %WHITE%%LEVEL%");
        loreLayout.add("%GOLD%Exp: %WHITE%%EXP% / %NEXT_LEVEL_EXP%");
        loreLayout.add("%WHITE%Free Modifier Slots: %FREE_SLOTS%");
        loreLayout.add("%WHITE%Modifiers:");
        loreLayout.add("%MODIFIERS%");
        layout.addDefault("LoreLayout", loreLayout);

        layout.addDefault("ModifierLayout", "%MODIFIER%: %WHITE%%MODLEVEL%");

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
     * sublist of mods which contains all modifiers that can be crafted (if enabled)
     */
    private final ArrayList<Modifier> craftableMods = new ArrayList<>();
    /**
     * sublist of mods which contains all modifiers that are crafted through the bookshelf
     */
    private final ArrayList<Modifier> enchantableMods = new ArrayList<>();

    private static ModManager instance;

    private List<String> loreScheme;
    private String modifierLayout;

    /**
     * Class constructor (no parameters)
     */
    private ModManager() {
        this.loreScheme = (List<String>) layout.getList("LoreLayout");
        for (int i = 0; i < loreScheme.size(); i++) {
            loreScheme.set(i, ChatWriter.addColors(loreScheme.get(i)));
        }
        this.modifierLayout = ChatWriter.addColors(layout.getString("ModifierLayout"));
    }

    /**
     * get the instance that contains the modifier list (VERY IMPORTANT)
     *
     * @return the instance
     */
    public synchronized static ModManager instance() {
        if(instance == null) {
            instance = new ModManager();
            instance.init();
        }
        return instance;
    }

    public void reload() {
        config = Main.getPlugin().getConfig();
        layout = ConfigurationManager.getConfig("layout.yml");

    	for(Modifier m : allMods) {
    		m.reload();
    		
    		if(m.isAllowed()) {
                register(m);

            } else {
                unregister(m);
            }
    	}
    	
    	this.craftableMods.clear();
        this.enchantableMods.clear();
    	
    	for (Modifier m : this.mods) {
            if (m instanceof Craftable) {
                this.craftableMods.add(m);
            }
            if (m instanceof Enchantable) {
                this.enchantableMods.add(m);
            }
        }

        for (Modifier m : this.craftableMods) {
            ((Craftable) m).registerCraftingRecipe();
        }

        this.loreScheme = (List<String>) layout.getList("LoreLayout");
        for (int i = 0; i < loreScheme.size(); i++) {
            loreScheme.set(i, ChatWriter.addColors(loreScheme.get(i)));
        }
        this.modifierLayout = ChatWriter.addColors(layout.getString("ModifierLayout"));
    }

    /**
     * checks and loads all modifiers with configurations settings into memory
     */
    private void init() {
        allMods.add(new Aquaphilic());
    	allMods.add(new AutoSmelt());
    	allMods.add(new Beheading());
    	allMods.add(new Directing());
    	allMods.add(new Ender());
    	allMods.add(new Experienced());
    	allMods.add(new ExtraModifier());
    	allMods.add(new Fiery());
    	allMods.add(new Freezing());
    	allMods.add(new Glowing());
    	allMods.add(new Haste());
    	allMods.add(new Infinity());
    	allMods.add(new Knockback());
    	allMods.add(new Lifesteal());
    	allMods.add(new LightWeight());
        allMods.add(new Luck());
        allMods.add(new Melting());
        allMods.add(new Poisonous());
        allMods.add(new Power());
        allMods.add(new Propelling());
        allMods.add(new Protecting());
        allMods.add(new Reinforced());
        allMods.add(new SelfRepair());
        allMods.add(new Sharpness());
        allMods.add(new Shulking());
        allMods.add(new SilkTouch());
        allMods.add(new Soulbound());
        allMods.add(new Sweeping());
        allMods.add(new Timber());
        allMods.add(new Webbed());
        
        reload();
    }

    /**
     * register a new modifier to the list
     *
     * @param mod the modifier instance
     */
    public void register(Modifier mod) {
    	if(!mods.contains(mod)) {
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
     * get all the modifiers in the list
     *
     * @return the modifier list
     */
    public List<Modifier> getAllowedMods() {
        return this.mods;
    }

    public List<Modifier> getAllMods() { return this.allMods; }

    /**
     * get all the craftable modifiers in the list
     *
     * @return the craftable modifier list
     */
    public List<Modifier> getCraftableMods() { return this.craftableMods; }

    /**
     * get all the enchantable modifiers in the list
     *
     * @return the enchantable modifier list
     */
    public List<Modifier> getEnchantableMods() { return this.enchantableMods; }

    /**
     * get a specific modifier instance
     *
     * @param type the modifiertype
     * @return the modifier instance, null if modifier is not allowed or loaded
     */
    public Modifier get(ModifierType type) {
        for(Modifier m : mods) {
            if(m.getType().equals(type)) {
                return m;
            }
        }
        return null;
    }

    /**
     * get a specific modifier instance even the not allowed ones
     *
     * @param type the modifiertype
     * @return the modifier instance, null if modifier is not allowed or loaded
     */
    public Modifier getAdmin(ModifierType type) {
        for(Modifier m : allMods) {
            if(m.getType().equals(type)) {
                return m;
            }
        }
        return null;
    }

    /**
     * add a specified modifier to a tool
     *
     * @param is the item to add the modifier to
     * @param mod the modifier to add
     */
    void addMod(ItemStack is, Modifier mod) {
        setNBTTag(is, mod.getType().getNBTKey(), new NBTTagInt(getModLevel(is, mod) + 1));
        rewriteLore(is);
    }

    /**
     * get the level of a specified modifier on a tool
     *
     * @param is the item
     * @param mod the modifier
     */
    public int getModLevel(ItemStack is, Modifier mod) {
        NBTTagInt tag = (NBTTagInt) getNBTTag(is, mod.getType().getNBTKey());
        if (tag == null) { return 0; }
        return tag.asInt();
    }

    /**
     * remove a modifier from a tool
     *
     * @param is the item to remove the modifier from
     * @param mod the modifier to remove
     */
    public void removeMod(ItemStack is, Modifier mod) {
        removeNBTTag(is, mod.getType().getNBTKey());
        mod.removeMod(is);
        rewriteLore(is);
    }

    /**
     * gets how many free slots the tool has
     *
     * @param is the item to get the information from
     */
    public int getFreeSlots(ItemStack is) {
        return ((NBTTagInt) getNBTTag(is, "FreeSlots")).asInt();
    }

    /**
     * sets how many free slots the tool has
     *
     * @param is the item to set the information to
     */
    public void setFreeSlots(ItemStack is, int freeSlots) {
        setNBTTag(is, "FreeSlots", new NBTTagInt(freeSlots));
        rewriteLore(is);
    }

    /**
     * gets what level the tool has
     *
     * @param is the item to get the information from
     */
    private int getLevel(ItemStack is) {
        return ((NBTTagInt) getNBTTag(is, "Level")).asInt();
    }

    /**
     * sets the level of the tool
     *
     * @param is the item to get the information from
     */
    private void setLevel(ItemStack is, int level) {
        setNBTTag(is, "Level", new NBTTagInt(level));
    }

    /**
     * gets the amount of exp the tool has
     *
     * @param is the item to get the information from
     */
    private long getExp(ItemStack is) {
        return ((NBTTagLong) getNBTTag(is, "Exp")).asLong();
    }

    /**
     * sets the exp amount of the tool
     *
     * @param is the item to get the information from
     */
    private void setExp(ItemStack is, long exp) {
        setNBTTag(is, "Exp", new NBTTagLong(exp));
    }

    /**
     * @param tool The Tool that is checked
     * @param mod The modifier that is checked in tool
     * @return if the tool has the mod
     */
    public boolean hasMod(ItemStack tool, Modifier mod) {
        return hasNBTTag(tool, mod.getType().getNBTKey());
    }

    /**
     * calculates the required exp for the given level
     * @param level
     * @return long value of the exp required
     */
    private long getNextLevelReq(int level) {
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
        boolean LevelUp = false;

        int level = this.getLevel(tool);
        long exp = this.getExp(tool);

        if (level == -1 || exp == -1) { return; }

        if (exp == Long.MAX_VALUE || exp + 1 < 0 || level + 1 < 0) {
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
        return armor != null && hasNBTTag(armor, "IdentifierArmor");
    }

    /**
     * @param tool the ItemStack
     * @return if the ItemStack is viable as MineTinker-Tool
     */
    public boolean isToolViable(ItemStack tool) {
        return tool != null && hasNBTTag(tool, "IdentifierTool");
    }

    /**
     * @param wand the ItemStack
     * @return if the ItemStack is viable as MineTinker-Builderswand
     */
    public boolean isWandViable(ItemStack wand) { return wand != null && hasNBTTag(wand, "IdentifierBuilderswand"); }

    public void setNBTTag(ItemStack is, String key, NBTBase value) {
        net.minecraft.server.v1_13_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound comp = nmsItem.getTag();
        if (comp == null) { comp = new NBTTagCompound(); }
        comp.set(key, value);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        is.setItemMeta(meta);
    }

    private void removeNBTTag(ItemStack is, String key) {
        net.minecraft.server.v1_13_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound comp = nmsItem.getTag();
        if (comp == null) { comp = new NBTTagCompound(); }
        comp.remove(key);
        nmsItem.setTag(comp);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        is.setItemMeta(meta);
    }

    private NBTBase getNBTTag(ItemStack is, String key) {
        net.minecraft.server.v1_13_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound comp = nmsItem.getTag();
        if (comp == null) { return null; }
        return comp.get(key);
    }

    private boolean hasNBTTag(ItemStack is, String key) {
        return getNBTTag(is, key) != null;
    }

    /**
     * Updates the lore of the Item as everything is stored in the NBT-Data
     * @param is
     */
    //TODO: Add option for roman numerals instead of arabic
    private void rewriteLore(ItemStack is) {
        ArrayList<String> lore = new ArrayList<>(this.loreScheme);
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            s = s.replaceAll("%EXP%", "" + getExp(is));
            s = s.replaceAll("%LEVEL%", "" + getLevel(is));
            s = s.replaceAll("%NEXT_LEVEL_EXP%", "" + getNextLevelReq(getLevel(is)));
            s = s.replaceAll("%FREE_SLOTS%", "" + getFreeSlots(is));
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

        if (index == -1) { return; }

        lore.remove(index);

        for (Modifier m : this.mods) {
            if (hasNBTTag(is, m.getType().getNBTKey())) {
                String s = this.modifierLayout;
                s = s.replaceAll("%MODIFIER%", m.getColor() + m.getName());
                s = s.replaceAll("%MODLEVEL%", "" + getModLevel(is, m));
                lore.add(index++, s);
            }
        }

        ItemMeta meta = is.getItemMeta();

        /*
         * For mcMMO-Superbreaker and other Skills
         */
        ArrayList<String> oldLore = (ArrayList<String>) meta.getLore();
        if (oldLore != null && oldLore.size() > 0 && oldLore.get(oldLore.size() - 1).equals("mcMMO Ability Tool")) {
            lore.add("mcMMO Ability Tool");
        }

        meta.setLore(lore);
        is.setItemMeta(meta);
    }

    /**
     * converts a given ItemStack into its MineTinker equivalent
     * @param is the MineTinker equivalent
     */
    public void convertItemStack(ItemStack is) {
        Material m = is.getType();
        if ((ToolType.AXE.getMaterials().contains(m)
                || ToolType.BOW.getMaterials().contains(m)
                || ToolType.HOE.getMaterials().contains(m)
                || ToolType.PICKAXE.getMaterials().contains(m)
                || ToolType.SHOVEL.getMaterials().contains(m)
                || ToolType.SWORD.getMaterials().contains(m)
                || ToolType.TRIDENT.getMaterials().contains(m)
                || ToolType.SHEARS.getMaterials().contains(m)
                || ToolType.FISHINGROD.getMaterials().contains(m)) && !isWandViable(is)) {
            setNBTTag(is, "IdentifierTool", new NBTTagInt(0));
        } else if (ToolType.BOOTS.getMaterials().contains(m)
                || ToolType.CHESTPLATE.getMaterials().contains(m)
                || ToolType.HELMET.getMaterials().contains(m)
                || ToolType.LEGGINGS.getMaterials().contains(m)
                || ToolType.ELYTRA.getMaterials().contains(m)) {
            setNBTTag(is, "IdentifierArmor", new NBTTagInt(0));
        } else { return; }
        setExp(is, 0);
        setLevel(is, 1);
        setFreeSlots(is, config.getInt("StartingModifierSlots"));
        rewriteLore(is);
    }

    public ItemStack createModifierItem(Material m, String name, String description, Modifier mod) {
        ItemStack is = new ItemStack(m, 1);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(description);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        is.setItemMeta(meta);

        setNBTTag(is, "modifierItem", new NBTTagString(mod.getType().getNBTKey()));

        NBTTagList list = new NBTTagList();
        list.add(new NBTTagString("minecraft:air"));
        setNBTTag(is, "CanPlaceOn", list);

        return is;
    }

    /**
     * @param item the ItemStack
     * @return if the ItemStack is viable as MineTinker-Modifier-Item
     */
    public boolean isModifierItem(ItemStack item) {
        return hasNBTTag(item, "modifierItem") || item.getType().equals(get(ModifierType.EXPERIENCED).getModItem().getType()) || item.getType().equals(get(ModifierType.EXTRA_MODIFIER).getModItem().getType());
    }

    /**
     * @param item the ItemStack
     * @return the Modifier of the Modifier-Item (NULL if not found)
     */
    public Modifier getModifierFromItem(ItemStack item) {
        if (!isModifierItem(item)) { return null; }
        if (item.getType().equals(get(ModifierType.EXPERIENCED).getModItem().getType())) { return get(ModifierType.EXPERIENCED); }
        if (item.getType().equals(get(ModifierType.EXTRA_MODIFIER).getModItem().getType())
                && !hasNBTTag(item, "modifierItem")) { return get(ModifierType.EXTRA_MODIFIER); }
        if (!hasNBTTag(item, "modifierItem")) { return null; }
        String name = Objects.requireNonNull(getNBTTag(item, "modifierItem")).asString();
        for (Modifier m : mods) {
            if (m.getType().getNBTKey() != null && m.getType().getNBTKey().equals(name)) {
                return m;
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
        if (tool.getType().getMaxDurability() - ((Damageable) meta).getDamage() <= 2 && config.getBoolean("UnbreakableTools")) {
            e.setCancelled(true);
            if (config.getBoolean("Sound.OnBreaking")) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.5F);
            }
            return false;
        }

        return true;
    }
}