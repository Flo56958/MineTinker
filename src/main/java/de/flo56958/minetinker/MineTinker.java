package de.flo56958.minetinker;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import de.flo56958.minetinker.commands.CommandManager;
import de.flo56958.minetinker.data.GUIs;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.listeners.*;
import de.flo56958.minetinker.modifiers.ModManager;
import de.flo56958.minetinker.modifiers.types.*;
import de.flo56958.minetinker.utils.*;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MineTinker extends JavaPlugin {

	private static JavaPlugin plugin;

	@Contract(pure = true)
	public static JavaPlugin getPlugin() { // necessary to do getConfig() in other classes
		return plugin;
	}

	private void parseMCVersion() {
		try {
			final String version = Iterables.get(Splitter.on("MC: ").split(Bukkit.getVersion()), 1).replaceAll("\\)", "");
			final List<String> ver = Splitter.on('.').splitToList(version);

			int mayor = Integer.parseInt(ver.getFirst());
			ChatWriter.log(true, "Minecraft Mayor Version: " + mayor);

			int minor = Integer.parseInt(ver.get(1));
			ChatWriter.log(true, "Minecraft Minor Version: " + minor);
		} catch (Exception e) {
			e.printStackTrace();
			ChatWriter.logError("Could not parse the Minecraft Version! Running 1.21 feature set. " +
					"If you are running a higher Version, please report this as an error.");
		}
	}

	@Override
	public void onEnable() {
		plugin = this;

		ChatWriter.log(false, "Setting up internals...");
		loadConfig(); //load Main config

		parseMCVersion();
		LanguageManager.reload(); //Load Language system

		ConfigurationManager.reload();

		ModManager.instance();
		addCoreMods();

		ChatWriter.reload();

		final TabExecutor cmd = new CommandManager();
		final PluginCommand command = this.getCommand("minetinker");
		if (command == null) {
			ChatWriter.logError("Could not register the command!");
		} else {
			command.setExecutor(cmd); // must be after internals as it would throw a NullPointerException
			command.setTabCompleter(cmd);
		}

		ChatWriter.logInfo(LanguageManager.getString("StartUp.Commands"));

		if (getConfig().getBoolean("AllowCrafting"))
			Bukkit.getPluginManager().registerEvents(new CreateToolListener(), this);

		if (getConfig().getBoolean("AllowConverting"))
			Bukkit.getPluginManager().registerEvents(new ConvertToolListener(), this);

		Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
		Bukkit.getPluginManager().registerEvents(new ArmorListener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getPluginManager().registerEvents(new CraftItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new TinkerListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInfo(), this);
		Bukkit.getPluginManager().registerEvents(new EnchantingListener(), this);
		Bukkit.getPluginManager().registerEvents(new GrindstoneListener(), this);

		if (getConfig().getBoolean("actionbar-on-exp-gain", false))
			Bukkit.getPluginManager().registerEvents(new ActionBarListener(), this);

		if (getConfig().getBoolean("ItemBehaviour.TrackStatistics", true))
			Bukkit.getPluginManager().registerEvents(new ItemStatisticsHandler(), this);

		ChatWriter.log(false, LanguageManager.getString("StartUp.Events"));

		if (getConfig().getBoolean("logging.metrics", true)) {
			final Metrics met = new Metrics(this, 2833);
			met.addCustomChart(new SimplePie("used_language", () -> getConfig().getString("Language", "en_US")));
		}

		AddSoftdependMods();

		ChatWriter.log(false, LanguageManager.getString("StartUp.GUIs"));
		GUIs.reload();

		ChatWriter.log(false, LanguageManager.getString("StartUp.StdLogging"));
		ChatWriter.log(true, LanguageManager.getString("StartUp.DebugLogging"));

		for (final Player current : Bukkit.getServer().getOnlinePlayers()) {
			Lists.BLOCKFACE.put(current, BlockFace.SELF);
		}

		if (getConfig().getBoolean("CheckForUpdates"))
			Bukkit.getScheduler().runTaskLaterAsynchronously(this, (@NotNull Runnable) Updater::checkForUpdate, 20);
	}

	private void addCoreMods() {
		final ModManager modManager = ModManager.instance();
		modManager.register(AntiArrowPlating.instance());
		modManager.register(AntiBlastPlating.instance());
		modManager.register(Aquaphilic.instance());
		modManager.register(AutoSmelt.instance());
		modManager.register(Beheading.instance());
		modManager.register(Berserk.instance());
		modManager.register(Building.instance());
		modManager.register(Channeling.instance());
		modManager.register(Dense.instance());
		modManager.register(Directing.instance());
		modManager.register(Drilling.instance());
		modManager.register(Ender.instance());
		modManager.register(Evasive.instance());
		modManager.register(Experienced.instance());
		modManager.register(Explosive.instance());
		modManager.register(ExtraModifier.instance());
		modManager.register(Farming.instance());
		modManager.register(Fiery.instance());
		modManager.register(Freezing.instance());
		modManager.register(Glowing.instance());
		modManager.register(Hardened.instance());
		modManager.register(Haste.instance());
		modManager.register(Homing.instance());
		modManager.register(Infinity.instance());
		modManager.register(Insulating.instance());
		modManager.register(KineticPlating.instance());
		modManager.register(Knockback.instance());
		modManager.register(Lifesteal.instance());
		modManager.register(LightWeight.instance());
		modManager.register(Luck.instance());
		modManager.register(Magical.instance());
		modManager.register(Melting.instance());
		modManager.register(MultiJump.instance());
		modManager.register(MultiShot.instance());
		modManager.register(Mutating.instance());
		modManager.register(Nightseeker.instance());
		modManager.register(Phasing.instance());
		modManager.register(Photosynthesis.instance());
		modManager.register(Piercing.instance());
		modManager.register(Poisonous.instance());
		modManager.register(Power.instance());
		modManager.register(Propelling.instance());
		modManager.register(Protecting.instance());
		modManager.register(Reinforced.instance());
		modManager.register(Scotopic.instance());
		modManager.register(ShadowDive.instance());
		modManager.register(SelfRepair.instance());
		modManager.register(Sharpness.instance());
		modManager.register(Shrouded.instance());
		modManager.register(Shulking.instance());
		modManager.register(SilkTouch.instance());
		modManager.register(Smite.instance());
		modManager.register(SoulSpeed.instance());
		modManager.register(Soulbound.instance());
		modManager.register(Speedy.instance());
		modManager.register(SpidersBane.instance());
		modManager.register(Sunblazer.instance());
		modManager.register(Sweeping.instance());
		modManager.register(SwiftSneaking.instance());
		modManager.register(Tanky.instance());
		modManager.register(Thorned.instance());
		modManager.register(Timber.instance());
		modManager.register(Undead.instance());
		modManager.register(Vigilant.instance());
		modManager.register(VoidNetting.instance());
		modManager.register(Webbed.instance());
		modManager.register(WildHunt.instance());
		modManager.register(Withered.instance());
		modManager.register(Zealous.instance());
	}

	public void AddSoftdependMods() {
		ModManager modManager = ModManager.instance();
		Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if (plugin != null && plugin.isEnabled()) {
			modManager.register(Echoing.instance());
		}

		GUIs.reload();
	}

	@Override
	public void onDisable() {
		ChatWriter.logInfo("Shutting down!");
		LanguageManager.cleanup(); //TODO: Replace with PluginDisableEvent
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
