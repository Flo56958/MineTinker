package de.flo56958.MineTinker;

import de.flo56958.MineTinker.Commands.CommandManager;
import de.flo56958.MineTinker.Data.GUIs;
import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Listeners.*;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Types.Power;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.LanguageManager;
import de.flo56958.MineTinker.Utilities.Updater;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import de.flo56958.MineTinker.api.gui.GUI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin {

	private static JavaPlugin plugin;

	public static Plugin getPlugin() { // necessary to do getConfig() in other classes
		return plugin;
	}

	@Override
	public void onEnable() {
		plugin = this;
		ChatWriter.log(false, "Setting up internals...");
		if (!NBTUtils.init()) {
			Bukkit.getPluginManager().disablePlugin(this); //Disable Plugin for safety
			return;
		}

		loadConfig(); //load Main config
		LanguageManager.reload(); //Load Language system

		ConfigurationManager.reload();
		BuildersWandListener.init();

		ChatWriter.reload();

		ModManager.instance();

		if (getConfig().getBoolean("PluginIncompatibility.Check")) {
			incompatibilityCheck();
		}

		TabExecutor cmd = new CommandManager();
		this.getCommand("minetinker").setExecutor(cmd); // must be after internals as it would throw a NullPointerException
		this.getCommand("minetinker").setTabCompleter(cmd);

		ChatWriter.logInfo(LanguageManager.getString("StartUp.Commands"));

		if (getConfig().getBoolean("AllowCrafting")) {
			Bukkit.getPluginManager().registerEvents(new CreateToolListener(), this);
		}

		if (getConfig().getBoolean("AllowConverting")) {
			Bukkit.getPluginManager().registerEvents(new ConvertToolListener(), this);
		}

		Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
		Bukkit.getPluginManager().registerEvents(new ArmorListener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getPluginManager().registerEvents(new CraftItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new TinkerListener(), this);
		Bukkit.getPluginManager().registerEvents(new TridentListener(), this);
		if (NBTUtils.isOneFourteenCompatible())
			Bukkit.getPluginManager().registerEvents(new GrindstoneListener(), this);

		FileConfiguration elytraConf = ConfigurationManager.getConfig("Elytra.yml");
		elytraConf.options().copyDefaults(true);
		elytraConf.addDefault("ExpChanceWhileFlying", 10);
		ConfigurationManager.saveConfig(elytraConf);

		if (getConfig().getBoolean("ConvertEnchantmentsOnEnchant")) {
			Bukkit.getPluginManager().registerEvents(new EnchantingListener(), this);
		}

		if (ConfigurationManager.getConfig("BuildersWand.yml").getBoolean("enabled")) {
			Bukkit.getPluginManager().registerEvents(new BuildersWandListener(), this);
			BuildersWandListener.reload();
			ChatWriter.log(false, LanguageManager.getString("StartUp.BuildersWands"));
		}

		if (getConfig().getBoolean("EasyHarvest.enabled")) {
			Bukkit.getPluginManager().registerEvents(new EasyHarvestListener(), this);
			ChatWriter.log(false, LanguageManager.getString("StartUp.EasyHarvest"));
		}

		if (getConfig().getBoolean("actionbar-on-exp-gain", false)) {
			Bukkit.getPluginManager().registerEvents(new ActionBarListener(), this);
		}

		ChatWriter.log(false, LanguageManager.getString("StartUp.Events"));

		if (getConfig().getBoolean("logging.metrics", true)) {
			Metrics met = new Metrics(this);
			met.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfig().getString("Language", "en_US")));
		}

		ChatWriter.log(false, LanguageManager.getString("StartUp.GUIs"));
		GUIs.reload();

		ChatWriter.log(false, LanguageManager.getString("StartUp.StdLogging"));
		ChatWriter.log(true, LanguageManager.getString("StartUp.DebugLogging"));

		for (Player current : Bukkit.getServer().getOnlinePlayers()) {
			Power.HASPOWER.computeIfAbsent(current, player -> new AtomicBoolean(false));
			Lists.BLOCKFACE.put(current, null);
		}

		if (getConfig().getBoolean("CheckForUpdates")) {
			Bukkit.getScheduler().scheduleAsyncDelayedTask(this, Updater::checkForUpdate, 20);
		}
	}

	/**
	 * Method for searching for known incompatibilities with other Plugins and fixing them if possible automatically (e.g. disable Lore for certain plugins)
	 */
	private void incompatibilityCheck() {
		ChatWriter.logInfo(LanguageManager.getString("StartUp.Incompatible.Start"));
		List<String> skipped = getConfig().getStringList("PluginIncompatibility.SkippedPlugins");

		FileConfiguration layout = ConfigurationManager.getConfig("layout.yml");

		Zenchantments:
		{
			String name = "Zenchantments";
			if (skipped.contains(name)) break Zenchantments;
			if (Bukkit.getServer().getPluginManager().isPluginEnabled(name) || Bukkit.getPluginManager().getPlugin(name) != null) {
				ChatWriter.logColor(ChatColor.RED + LanguageManager.getString("StartUp.Incompatible.Found").replace("%plugin", name));
				layout.set("UsePatternMatcher", true);
				ChatWriter.logColor(ChatColor.WHITE + " - UsePatternMatcher -> true");
			}
		}
		EliteMobs:
		{
			String name = "EliteMobs";
			if (skipped.contains(name)) break EliteMobs;
			if (Bukkit.getServer().getPluginManager().isPluginEnabled(name) || Bukkit.getPluginManager().getPlugin(name) != null) {
				ChatWriter.logColor(ChatColor.RED + LanguageManager.getString("StartUp.Incompatible.Found").replace("%plugin", name));
				layout.set("UsePatternMatcher", true);
				ChatWriter.logColor(ChatColor.WHITE + " - UsePatternMatcher -> true");
			}
		}
		mcMMO:
		{
			String name = "mcMMO";
			if (skipped.contains(name)) break mcMMO;
			if (Bukkit.getServer().getPluginManager().isPluginEnabled(name) || Bukkit.getPluginManager().getPlugin(name) != null) {
				ChatWriter.logColor(ChatColor.RED + LanguageManager.getString("StartUp.Incompatible.Found").replace("%plugin", name));
				layout.set("UsePatternMatcher", true);
				ChatWriter.logColor(ChatColor.WHITE + " - UsePatternMatcher -> true");
			}
		}
		Multitool:
		{
			String name = "Multitool";
			if (skipped.contains(name)) break Multitool;
			if (Bukkit.getServer().getPluginManager().isPluginEnabled(name) || Bukkit.getPluginManager().getPlugin(name) != null) {
				ChatWriter.logColor(ChatColor.RED + LanguageManager.getString("StartUp.Incompatible.Found").replace("%plugin", name));
				layout.set("UsePatternMatcher", true);
				ChatWriter.logColor(ChatColor.WHITE + " - UsePatternMatcher -> true");
			}
		}
		DeadSouls:
		{
			String name = "DeadSouls";
			if (skipped.contains(name)) break DeadSouls;
			if (Bukkit.getServer().getPluginManager().isPluginEnabled(name) || Bukkit.getPluginManager().getPlugin(name) != null) {
				ChatWriter.logColor(ChatColor.RED + LanguageManager.getString("StartUp.Incompatible.Found").replace("%plugin", name));
				getConfig().set("ItemBehaviour.ApplyOnPlayerDeath", false);
				ChatWriter.logColor(ChatColor.WHITE + " - ItemBehaviour.ApplyOnPlayerDeath -> false");
			}
		}
		DeathBarrel:
		{
			String name = "DeathBarrel";
			if (skipped.contains(name)) break DeathBarrel;
			if (Bukkit.getServer().getPluginManager().isPluginEnabled(name) || Bukkit.getPluginManager().getPlugin(name) != null) {
				ChatWriter.logColor(ChatColor.RED + LanguageManager.getString("StartUp.Incompatible.Found").replace("%plugin", name));
				getConfig().set("ItemBehaviour.ApplyOnPlayerDeath", false);
				ChatWriter.logColor(ChatColor.WHITE + " - ItemBehaviour.ApplyOnPlayerDeath -> false");
			}
		}

		ConfigurationManager.saveConfig(layout);
		saveConfig();
	}

	public void onDisable() {
		ChatWriter.logInfo("Shutting down!");
		LanguageManager.cleanup();
		GUI.guis.forEach(GUI::close); //To negate exploit that you could use the Items in the GUIs
	}

	/**
	 * loads the main config of MineTinker
	 */
	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();

		ChatWriter.log(false, "Main-Configuration loaded!");
	}
}
