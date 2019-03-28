package de.flo56958.MineTinker.Data;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ToolType {

	AXE,
	BOOTS,
	BOW,
	CHESTPLATE,
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
	 * @param mat the material to check
	 * @return material's tooltype, null if invalid
	 */
	public static ToolType get(Material mat) {
		for (ToolType tt : values()) {
			if (tt.getMaterials().contains(mat)) return tt;
		}
		return null;
	}

	/**
	 * get all materials from a given tooltype
	 *
	 * @return list of materials
	 */
	public List<Material> getMaterials() {
		List<Material> materials = new ArrayList<>();
		switch (this) {
			case AXE:
				materials.addAll(Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE));
				break;
			case BOOTS:
				materials.addAll(Arrays.asList(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS));
				break;
			case BOW:
				materials.add(Material.BOW);
				break;
			case CHESTPLATE:
				materials.addAll(Arrays.asList(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE));
				break;
			case ELYTRA:
				materials.add(Material.ELYTRA);
				break;
			case HELMET:
				materials.addAll(Arrays.asList(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.TURTLE_HELMET));
				break;
			case HOE:
				materials.addAll(Arrays.asList(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE));
				break;
			case LEGGINGS:
				materials.addAll(Arrays.asList(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS));
				break;
			case PICKAXE:
				materials.addAll(Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE));
				break;
			case FISHINGROD:
				materials.add(Material.FISHING_ROD);
				break;
			case SHIELD:
				materials.add(Material.SHIELD);
				break;
			case SHOVEL:
				materials.addAll(Arrays.asList(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL));
				break;
			case SWORD:
				materials.addAll(Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD));
				break;
			case TRIDENT:
				materials.add(Material.TRIDENT);
				break;
			case OTHER: //TODO: REQUEST: ADD SHEARS
				materials.addAll(Arrays.asList(Material.FLINT_AND_STEEL, Material.CARROT_ON_A_STICK));
				break;
			case SHEARS:
				materials.add(Material.SHEARS);
			default:
				break;
		}
		return materials;
	}
}
