package de.flo56958.minetinker.data;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

	private static final EnumMap<ToolType, List<Material>> tools = new EnumMap<>(ToolType.class);
	private static final HashSet<Material> toolMaterials = new HashSet<>();

	static {
		tools.put(ToolType.AXE, new ArrayList<>(Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
				Material.GOLDEN_AXE, Material.DIAMOND_AXE)));
		tools.put(ToolType.BOOTS, new ArrayList<>(Arrays.asList(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS,
				Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS)));
		tools.put(ToolType.BOW, new ArrayList<>(Collections.singletonList(Material.BOW)));
		tools.put(ToolType.CHESTPLATE, new ArrayList<>(Arrays.asList(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE,
				Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE)));
		tools.put(ToolType.CROSSBOW, new ArrayList<>(Collections.singletonList(Material.CROSSBOW)));
		tools.put(ToolType.ELYTRA, new ArrayList<>(Collections.singletonList(Material.ELYTRA)));
		tools.put(ToolType.HELMET, new ArrayList<>(Arrays.asList(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET,
				Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.TURTLE_HELMET)));
		tools.put(ToolType.HOE, new ArrayList<>(Arrays.asList(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
				Material.GOLDEN_HOE, Material.DIAMOND_HOE)));
		tools.put(ToolType.LEGGINGS, new ArrayList<>(Arrays.asList(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS,
				Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS)));
		tools.put(ToolType.PICKAXE, new ArrayList<>(Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE,
				Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE)));
		tools.put(ToolType.FISHINGROD, new ArrayList<>(Collections.singletonList(Material.FISHING_ROD)));
		tools.put(ToolType.SHIELD, new ArrayList<>(Collections.singletonList(Material.SHIELD)));
		tools.put(ToolType.SHOVEL, new ArrayList<>(Arrays.asList(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL,
				Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL)));
		tools.put(ToolType.SWORD, new ArrayList<>(Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
				Material.GOLDEN_SWORD, Material.DIAMOND_SWORD)));
		tools.put(ToolType.TRIDENT, new ArrayList<>(Collections.singletonList(Material.TRIDENT)));
		tools.put(ToolType.OTHER, new ArrayList<>(Arrays.asList(Material.FLINT_AND_STEEL, Material.CARROT_ON_A_STICK)));
		tools.put(ToolType.SHEARS, new ArrayList<>(Collections.singletonList(Material.SHEARS)));

		if (MineTinker.is16compatible) {
			tools.get(ToolType.AXE).add(Material.NETHERITE_AXE);
			tools.get(ToolType.BOOTS).add(Material.NETHERITE_BOOTS);
			tools.get(ToolType.CHESTPLATE).add(Material.NETHERITE_CHESTPLATE);
			tools.get(ToolType.HELMET).add(Material.NETHERITE_HELMET);
			tools.get(ToolType.HOE).add(Material.NETHERITE_HOE);
			tools.get(ToolType.LEGGINGS).add(Material.NETHERITE_LEGGINGS);
			tools.get(ToolType.PICKAXE).add(Material.NETHERITE_PICKAXE);
			tools.get(ToolType.SHOVEL).add(Material.NETHERITE_SHOVEL);
			tools.get(ToolType.SWORD).add(Material.NETHERITE_SWORD);
		}
		
		tools.put(ToolType.INVALID, Collections.emptyList());

		ToolType.getTools().values().forEach(toolMaterials::addAll);

		tools.put(ToolType.ARMOR, new ArrayList<>(ToolType.HELMET.getToolMaterials())); //as other lists are unmodifiable
		tools.get(ToolType.ARMOR).addAll(ToolType.ELYTRA.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.CHESTPLATE.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.LEGGINGS.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.BOOTS.getToolMaterials());
		tools.get(ToolType.ARMOR).addAll(ToolType.SHIELD.getToolMaterials());

		tools.put(ToolType.TOOLS, new ArrayList<>(ToolType.SWORD.getToolMaterials()));
		tools.get(ToolType.TOOLS).addAll(ToolType.SHEARS.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.PICKAXE.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.SHOVEL.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.HOE.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.TRIDENT.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.FISHINGROD.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.BOW.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.CROSSBOW.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.OTHER.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.SHIELD.getToolMaterials());
		tools.get(ToolType.TOOLS).addAll(ToolType.AXE.getToolMaterials());

		tools.put(ToolType.ALL, new ArrayList<>(getAllToolMaterials()));
	}

	public static @NotNull EnumMap<ToolType, List<Material>> getTools() {
		return tools;
	}

	public static @NotNull HashSet<Material> getAllToolMaterials() {
		return toolMaterials;
	}

	public static boolean isMaterialCompatible(Material material) {
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
	 * @return material's tooltype, null if invalid
	 */
	public static @NotNull ToolType get(@Nullable Material material) {
		if (material == null) return ToolType.INVALID;
		for (ToolType type : values()) {
			if (type == ToolType.ALL || type == ToolType.TOOLS || type == ToolType.ARMOR || type == ToolType.INVALID) {
				continue;
			}

			if (getTools().get(type).contains(material)) {
				return type;
			}
		}

		return ToolType.INVALID;
	}

	public List<Material> getToolMaterials() {
		return getTools().get(this);
	}

	public boolean contains(Material material) {
		return getToolMaterials().contains(material);
	}
}
