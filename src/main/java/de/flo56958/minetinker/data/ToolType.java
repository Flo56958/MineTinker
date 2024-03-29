package de.flo56958.minetinker.data;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;

public enum ToolType {

	ALL,
	ARMOR,
	TOOLS,
	AXE,
	BOOTS,
	BOW,
	CHESTPLATE,
	CROSSBOW,
	ELYTRA,
	FISHINGROD,
	HELMET,
	HOE,
	INVALID,
	LEGGINGS,
	OTHER,
	PICKAXE,
	SHEARS,
	SHIELD,
	SHOVEL,
	SWORD,
	TRIDENT;

	private static final EnumMap<ToolType, HashSet<Material>> tools = new EnumMap<>(ToolType.class);

	private static final EnumMap<Material, ToolType> materialMap = new EnumMap<>(Material.class);

	static {
		tools.put(ToolType.AXE, new HashSet<>(Tag.ITEMS_AXES.getValues()));
		tools.put(ToolType.BOOTS, new HashSet<>(Arrays.asList(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS,
				Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS)));
		tools.put(ToolType.BOW, new HashSet<>(Collections.singletonList(Material.BOW)));
		tools.put(ToolType.CHESTPLATE, new HashSet<>(Arrays.asList(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE,
				Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE)));
		tools.put(ToolType.CROSSBOW, new HashSet<>(Collections.singletonList(Material.CROSSBOW)));
		tools.put(ToolType.ELYTRA, new HashSet<>(Collections.singletonList(Material.ELYTRA)));
		tools.put(ToolType.HELMET, new HashSet<>(Arrays.asList(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET,
				Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.TURTLE_HELMET, Material.NETHERITE_HELMET)));
		tools.put(ToolType.HOE, new HashSet<>(Tag.ITEMS_HOES.getValues()));
		tools.put(ToolType.LEGGINGS, new HashSet<>(Arrays.asList(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS,
				Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS)));
		tools.put(ToolType.PICKAXE, new HashSet<>(Tag.ITEMS_PICKAXES.getValues()));
		tools.put(ToolType.FISHINGROD, new HashSet<>(Collections.singletonList(Material.FISHING_ROD)));
		tools.put(ToolType.SHIELD, new HashSet<>(Collections.singletonList(Material.SHIELD)));
		tools.put(ToolType.SHOVEL, new HashSet<>(Tag.ITEMS_SHOVELS.getValues()));
		tools.put(ToolType.SWORD, new HashSet<>(Tag.ITEMS_SWORDS.getValues()));
		tools.put(ToolType.TRIDENT, new HashSet<>(Collections.singletonList(Material.TRIDENT)));
		tools.put(ToolType.OTHER, new HashSet<>(Arrays.asList(Material.FLINT_AND_STEEL, Material.CARROT_ON_A_STICK)));
		tools.put(ToolType.SHEARS, new HashSet<>(Collections.singletonList(Material.SHEARS)));

		tools.put(ToolType.INVALID, new HashSet<>());

		final HashSet<Material> all = new HashSet<>();
		ToolType.tools.values().forEach(all::addAll);
		ToolType.tools.forEach((type, materials) -> materials.forEach(material -> ToolType.materialMap.put(material, type)));

		tools.put(ToolType.ARMOR, new HashSet<>(ToolType.HELMET.getToolMaterials())); //as other lists are unmodifiable
		tools.get(ToolType.ARMOR).addAll(ToolType.ELYTRA.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.LEGGINGS.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.BOOTS.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.SHIELD.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.CHESTPLATE.getToolMaterials());

		tools.put(ToolType.TOOLS, new HashSet<>(ToolType.SWORD.getToolMaterials()));
		tools.get(ToolType.TOOLS).addAll(ToolType.SHEARS.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.SHOVEL.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.HOE.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.TRIDENT.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.FISHINGROD.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.BOW.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.CROSSBOW.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.OTHER.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.SHIELD.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.AXE.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.PICKAXE.getToolMaterials());

		tools.put(ToolType.ALL, all);
	}

	private static @NotNull EnumMap<ToolType, HashSet<Material>> getTools() {
		return tools;
	}

	public static boolean isMaterialCompatible(final Material material) {
		return ToolType.ALL.contains(material);
	}

	/**
	 * get the tooltype from a string
	 *
	 * @param name the tooltype name
	 * @return tooltype instance, null if invalid name
	 */
	public static ToolType get(String name) {
		return valueOf(name);
	}

	/**
	 * get the tooltype for a given material
	 *
	 * @param material the material to check
	 * @return material's tooltype
	 */
	public static @NotNull ToolType get(@Nullable Material material) {
		if (material == null) return ToolType.INVALID;
		return materialMap.getOrDefault(material, INVALID);
	}

	public HashSet<Material> getToolMaterials() {
		return getTools().get(this);
	}

	public boolean contains(Material material) {
		return getToolMaterials().contains(material);
	}
}
