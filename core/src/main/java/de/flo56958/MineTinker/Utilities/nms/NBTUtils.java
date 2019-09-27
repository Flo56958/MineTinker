package de.flo56958.MineTinker.Utilities.nms;

import de.flo56958.MineTinker.Utilities.ChatWriter;
import org.bukkit.Bukkit;

public class NBTUtils {
	private static NBTHandler handler;

	private static boolean oneThirteenCompatible = false;
	private static boolean oneFourteenCompatible = false;

	private NBTUtils() {
	}

	/**
	 * Initializes the NBTHandler for the server version.
	 *
	 * @return true, if MineTinker is compatible with the server version.
	 * false, if MineTinker does not support the version
	 */
	public static boolean init() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			ChatWriter.logError("Invalid server version!");
			return false;
		}

		switch (version) {
			case "v1_14_R1":
				handler = new NBTHandler_v1_14_R1();
				oneThirteenCompatible = true;
				oneFourteenCompatible = true;
				break;
			case "v1_13_R2":
				handler = new NBTHandler_v1_13_R2();
				oneThirteenCompatible = true;
				break;
			case "v1_13_R1":
				handler = new NBTHandler_v1_13_R1();
				oneThirteenCompatible = true;
				break;
			default:
				ChatWriter.logError("Unsupported version: " + version);
				return false;
		}

		return true;
	}

	public static NBTHandler getHandler() {
		return handler;
	}

	public static boolean isOneThirteenCompatible() {
		return oneThirteenCompatible;
	}

	public static boolean isOneFourteenCompatible() {
		return oneFourteenCompatible;
	}
}
