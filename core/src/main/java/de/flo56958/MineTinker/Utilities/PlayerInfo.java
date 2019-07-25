package de.flo56958.MineTinker.Utilities;

import org.bukkit.entity.Player;

public class PlayerInfo {

    /**
     * @param player The player to get the facing direction of as a single character
     * @return The facing direction of the player in Degrees
     */
    public static String getFacingDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90) % 360;

        if (rot < 0) {
            rot += 360.0;
        }

        return getDirection(rot);
    }

    /**
     * @param rot The rotation in degrees
     * @return The compass facing direction
     */
    private static String getDirection(double rot) {
        if (0 <= rot && rot < 45) {
            return "W";
        } else if (45 <= rot && rot < 135) {
            return "N";
        } else if (135 <= rot && rot < 225) {
            return "E";
        } else if (225 <= rot && rot < 315) {
            return "S";
        } else if (315 <= rot && rot < 360) {
            return "W";
        } else {
            return null;
        }
    }

}
