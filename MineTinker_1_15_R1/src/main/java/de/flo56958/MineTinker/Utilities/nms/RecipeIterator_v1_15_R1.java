package de.flo56958.MineTinker.Utilities.nms;

import net.minecraft.server.v1_15_R1.IRecipe;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.Map;

public class RecipeIterator_v1_15_R1 implements Iterator<Recipe> {

	private Iterator<? extends Map<MinecraftKey, IRecipe<?>>> iterator;
	private Iterator<IRecipe<?>> currentIterator;

	RecipeIterator_v1_15_R1() {
		iterator = MinecraftServer.getServer().getCraftingManager().recipes.values().iterator();
	}

	@Override
	public boolean hasNext() {
		if (currentIterator == null || !currentIterator.hasNext()) {
			while (iterator.hasNext()) {
				currentIterator = iterator.next().values().iterator();
				if (currentIterator.hasNext())
					return true;
			}
		}

		return currentIterator != null && currentIterator.hasNext();
	}

	@Override
	public Recipe next() {
		if (currentIterator == null) return null;
		return currentIterator.next().toBukkitRecipe();
	}

	@Override
	public void remove() {
		if (currentIterator == null) return;
		currentIterator.remove();
	}
}
