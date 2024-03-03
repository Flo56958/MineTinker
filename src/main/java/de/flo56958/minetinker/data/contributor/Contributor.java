package de.flo56958.minetinker.data.contributor;

import com.google.gson.*;
import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Contributor {

	private static final ArrayList<Contributor> contributors = new ArrayList<>();

	static {
		try {
			ArrayList<JsonObject> contributorList = new ArrayList<>();
			{
				//Obtain Information over GitHub API
				String cons = new Scanner(new URL("https://api.github.com/repos/Flo56958/MineTinker/contributors").openStream(),
						StandardCharsets.UTF_8).useDelimiter("\\A").next();
				if (cons != null) {
					try {
						JsonArray json = JsonParser.parseString(cons).getAsJsonArray();
						json.forEach(e -> contributorList.add(e.getAsJsonObject()));
					} catch (JsonSyntaxException ignored) {}
				}
			}
			{
				JsonArray json = JsonParser.parseString(new Scanner(new URL("https://raw.githubusercontent.com/Flo56958/MineTinker/master/contributors.json").openStream(),
						StandardCharsets.UTF_8).useDelimiter("\\A").next()).getAsJsonArray();
				json.forEach(e -> {
					JsonObject o = e.getAsJsonObject();
					String github = null;
					if (!o.get("github").isJsonNull()) github = o.get("github").getAsString();
					String finalGithub = github;
					Optional<JsonObject> gh = contributorList.stream().filter(el -> el.get("login").getAsString().equals(finalGithub)).findAny();
					JsonObject g = null;
					if (gh.isPresent()) g = gh.get();
					JsonElement transifex = o.get("transifex");
					JsonElement minecraft = o.get("minecraft");
					JsonElement discord = o.get("discord");
					JsonElement other = o.get("other");
					contributors.add(
							new Contributor((minecraft.isJsonNull()) ? null : UUID.fromString(minecraft.getAsString()), g,
									(transifex.isJsonNull()) ? null : transifex.getAsString(),
									o.getAsJsonArray("transifex_languages"),
									(discord.isJsonNull()) ? null : discord.getAsString(),
									(other.isJsonNull()) ? null : other.getAsJsonArray()));
				});
			}
			contributorList.removeIf(o -> o.get("login").getAsString().contains("[bot]")); //filter out bots

			//Add all GitHub Contributers that don't have Minecraft or Transifex
			contributorList.removeIf(o -> contributors.stream().anyMatch(c -> o.get("login").getAsString().equals(c.getGithubUsername())));
			contributorList.forEach(o -> contributors.add(new Contributor(null, o, null, null, null, null)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Contributor> getContributors() {
		return contributors;
	}

	public UUID getMcUUID() {
		return mcUUID;
	}

	public String getGithubUsername() {
		return githubUsername;
	}

	public int getCommits() {
		return commits;
	}

	public ItemStack getPlayerHead() {
		return playerHead;
	}

	private final UUID mcUUID;

	private String githubUsername;
	private final ArrayList<String> languages = new ArrayList<>();
	private int commits;

	private final ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);

	private Contributor(UUID mcUUID, JsonObject github, String transifex, JsonArray languages, String discord, JsonArray other) {
		this.mcUUID = mcUUID;

		final ItemMeta itemMeta = playerHead.getItemMeta();
		final ArrayList<String> lore = new ArrayList<>();
		String displayName = "";
		if (mcUUID != null) {
			if (itemMeta instanceof SkullMeta) {
				try {
					JsonObject mcLookup = JsonParser.parseString(new Scanner(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + mcUUID).openStream(),
							StandardCharsets.UTF_8).useDelimiter("\\A").next()).getAsJsonObject();
					String name = mcLookup.get("name").getAsString();
					displayName += name + "/";
					try {
						((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(mcUUID));
					} catch (NullPointerException ignored) {}
				} catch (IOException | NoSuchElementException e) {
					ChatWriter.logError(LanguageManager.getString("Alert.MinecraftAPI"));
					if (MineTinker.getPlugin().getConfig().getBoolean("logging.debug")) {
						System.out.println("This error is not a bug or serious error. MT caught the error and just prints the information in the console!");
						e.printStackTrace();
					}
				}
			}
		}
		if (github != null) {
			this.githubUsername = github.get("login").getAsString();
			this.commits = github.get("contributions").getAsInt();
			displayName += this.githubUsername + "/";
			lore.add(ChatColor.GOLD + "GitHub-Contributions: " + ChatColor.WHITE + this.commits);
		}
		if (transifex != null) {
			displayName += transifex + "/";
			if (languages != null) {
				languages.forEach(o -> this.languages.add(o.getAsString()));
				this.languages.sort(String::compareToIgnoreCase);
				StringBuilder sb = new StringBuilder();
				for(String s : this.languages) {
					sb.append(s).append(", ");
				}
				lore.add(ChatColor.GOLD + "Translating Languages: " + ChatColor.WHITE + sb.substring(0, sb.length() - 2));
			}
		}

		if (discord != null)
			lore.add(ChatColor.GOLD + "Discord-Username: " + ChatColor.WHITE + discord);

		if (other != null) {
			other.forEach(e -> {
				if (!e.isJsonNull()) {
					lore.add(ChatColor.WHITE + e.getAsString());
				}
			});
		}

		if (!displayName.isEmpty())
			itemMeta.setDisplayName(displayName.substring(0, displayName.length() - 1));

		itemMeta.setLore(lore);

		playerHead.setItemMeta(itemMeta);
	}
}
