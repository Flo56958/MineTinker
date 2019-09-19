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

    // Calculate amount of EXP needed to level up
    private static int getExpToLevelUp(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    // Calculate total experience up to a level
    private static int getExpAtLevel(int level){
        if(level <= 16){
            return (int) (Math.pow(level,2) + 6*level);
        } else if(level <= 31){
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }

    // Calculate player's current EXP amount
    public static int getPlayerExp(Player player){
        int exp = 0;
        int level = player.getLevel();

        // Get the amount of XP in past levels
        exp += getExpAtLevel(level);

        // Get amount of XP towards next level
        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }
}
