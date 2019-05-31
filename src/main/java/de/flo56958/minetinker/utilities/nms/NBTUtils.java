package de.flo56958.minetinker.utilities.nms;

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

        if (version.equals("v1_14_R0")) {
            handler = new NBTHandler_1_14_R0();
            oneThirteenCompatible = true;
            oneFourteenCompatible = true;
        }
    }

    public NBTHandler getHandler() {
        return handler;
    }

    public static boolean isOneThirteenCompatible() { return oneThirteenCompatible; }
    public static boolean isOneFourteenCompatible() { return oneFourteenCompatible; }
}
