package de.flo56958.MineTinker.Data;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerData {

    public static HashMap<Player, BlockFace> BlockFace = new HashMap<>();
    public static HashMap<Player, Boolean> hasPower = new HashMap<>();
    public static HashMap<Player, Boolean> canBreakBlocks = new HashMap<>();
}
