package de.flo56958.minetinker.utils.datatypes;

import org.jetbrains.annotations.Nullable;

public record Pair<X, Y>(X x, Y y) {

	public Pair(@Nullable final X x, @Nullable final Y y) {
		this.x = x;
		this.y = y;
	}
}
