package com.imjake9.server.essentials;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JSEssentialsPermissionsHandler {
    
    /**
     * Gets a node prefixed with "jsessentials".
     * 
     * @param node node suffix
     * @return formatted permission node
     */
    public static String getPermission(String node) {
        return "jsessentials." + node;
    }
    
    /**
     * Gets whether a player has permission using the prefix "jsessentials".
     * 
     * @param player player to check
     * @param node node suffix to check
     * @return whether or not the player has permission
     */
    public static boolean hasPermission(CommandSender player, String node) {
        if (!(player instanceof Player))
            return true;
        return player.hasPermission(getPermission(node));
    }
    
}
