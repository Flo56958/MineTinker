package de.flo56958.MineTinker.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

public enum ToolType {

	AXE,
	BOOTS,
	BOW,
	CHESTPLATE,
	ELYTRA,
	HELMET,
	HOE,
	LEGGINGS,
	PICKAXE,
	ROD,
	SHIELD,
	SHOVEL,
	SWORD,
	TRIDENT,
	OTHER;
	
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
		for(ToolType tt : values()) {
			if(tt.getMats().contains(mat)) return tt;
		}
		return null;
	}
	
	/**
	 * get all materials from a given tooltype
	 * 
	 * @return list of materials
	 */
	public List<Material> getMats(){
		List<Material> mats = new ArrayList<>();
		switch(this) {
		case AXE: 
			mats.addAll(Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE)); break;
		case BOOTS: 
			mats.addAll(Arrays.asList(Material.LEATHER_BOOTS,Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS)); break;
		case BOW: 
			mats.add(Material.BOW); break;
		case CHESTPLATE:
			mats.addAll(Arrays.asList(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE)); break;
		case ELYTRA:
			mats.add(Material.ELYTRA); break;
		case HELMET:
			mats.addAll(Arrays.asList(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET)); break;
		case HOE:
			mats.addAll(Arrays.asList(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE)); break;
		case LEGGINGS:
			mats.addAll(Arrays.asList(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS)); break;
		case PICKAXE:
			mats.addAll(Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE)); break;
		case ROD:
			mats.add(Material.FISHING_ROD); break;
		case SHIELD:
			mats.add(Material.SHIELD); break;
		case SHOVEL:
			mats.addAll(Arrays.asList(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL)); break;
		case SWORD:
			mats.addAll(Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD)); break;
		case TRIDENT:
			mats.add(Material.TRIDENT); break;
		case OTHER:
			mats.addAll(Arrays.asList(Material.FLINT_AND_STEEL, Material.CARROT_ON_A_STICK, Material.SHEARS)); break;
		default: break;
		}
		return mats;
	}
	
}
