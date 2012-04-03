package com.imjake9.server.essentials;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class JSEssentials extends JavaPlugin {
    
    private static JSEssentials plugin;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private JSEssentialsCommandHandler commandHandler;
    private JSEssentialsPermissionsHandler permissionsHandler;
    
    @Override
    public void onEnable() {
        plugin = this;
        commandHandler = new JSEssentialsCommandHandler();
        permissionsHandler = new JSEssentialsPermissionsHandler();
    }
    
    public static JSEssentials getPlugin() {
        return plugin;
    }
    
    public static JSEssentialsCommandHandler getCommandHandler() {
        return plugin.commandHandler;
    }
    
    public static JSEssentialsPermissionsHandler getPermissionsHandler() {
        return plugin.permissionsHandler;
    }
    
}
