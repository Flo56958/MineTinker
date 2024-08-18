package de.flo56958.minetinker.commands;

public enum ArgumentType {
	COLORED_TEXT, //Text with ChatColors, e.g. &a
	PLAYER,       //A Player, can also be @r, @p, and @a
	RANDOM_NUMBER, //A random number from the definitions in this index
	BOOLEAN,       //A boolean value, either true or false
}
