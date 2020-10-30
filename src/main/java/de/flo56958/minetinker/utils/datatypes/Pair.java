package de.flo56958.minetinker.utils.datatypes;

import org.jetbrains.annotations.Nullable;

public class Pair <X, Y> {

	public final X x;
	public final Y y;

	public Pair (@Nullable final X x, @Nullable final Y y) {
		this.x = x;
		this.y = y;
	}
}
