package de.flo56958.MineTinker.Utilities.nms;

import org.bukkit.Bukkit;

public class NBTUtils {
    private NBTHandler handler;

    private String version;
    private String packageName;

    private static boolean oneThirteenCompatible = false;
    private static boolean oneFourteenCompatible = false;

    public NBTUtils() {
        try {
            packageName = NBTUtils.class.getPackage().getName();
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            if (version.equals("v1_14_R1")) {
                oneThirteenCompatible = true;
                oneFourteenCompatible = true;
            } else if (version.equals("v1_13_R1") || version.equals("v1_13_R2")) {
                oneThirteenCompatible = true;
                oneFourteenCompatible = false;
            } else {
                System.out.println("Unsupported version: " + version);
            }

            handler = (NBTHandler) Class.forName(packageName + ".NBTHandler_" + version).newInstance();
        } catch (ArrayIndexOutOfBoundsException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Invalid server version!");
            e.printStackTrace();
        }

    }

    public NBTHandler getHandler() {
        return handler;
    }

    public static boolean isOneThirteenCompatible() { return oneThirteenCompatible; }
    public static boolean isOneFourteenCompatible() { return oneFourteenCompatible; }
}
