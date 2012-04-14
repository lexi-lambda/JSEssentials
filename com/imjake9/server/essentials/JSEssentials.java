package com.imjake9.server.essentials;

import com.imjake9.server.lib.plugin.JSPlugin;
import java.util.logging.Logger;

public class JSEssentials extends JSPlugin {
    
    private static JSEssentials plugin;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private JSEssentialsCommandHandler commandHandler;
    
    @Override
    public void onJSEnable() {
        plugin = this;
        commandHandler = new JSEssentialsCommandHandler();
    }
    
    @Override
    public void onJSDisable() {
        
    }
    
    public static JSEssentials getPlugin() {
        return plugin;
    }
    
    public static JSEssentialsCommandHandler getCommandHandler() {
        return plugin.commandHandler;
    }
    
}
