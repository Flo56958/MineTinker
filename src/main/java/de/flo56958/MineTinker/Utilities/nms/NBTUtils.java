package de.flo56958.MineTinker.Utilities.nms;

import org.bukkit.Bukkit;

public class NBTUtils {
    private NBTHandler handler;
    private String version;

    private static boolean oneThirteenCompatible = false;
    private static boolean oneFourteenCompatible = false;

    public NBTUtils() {
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid server version!");
            return;
        }

        if (version.equals("v1_14_R1")) {
            handler = new NBTHandler_1_14_R1();
            oneThirteenCompatible = true;
            oneFourteenCompatible = true;
        } else {
            System.out.println("Unsupported version: " + version);
        }
    }

    public NBTHandler getHandler() {
        return handler;
    }

    public static boolean isOneThirteenCompatible() { return oneThirteenCompatible; }
    public static boolean isOneFourteenCompatible() { return oneFourteenCompatible; }
}
