package com.imjake9.server.essentials.utils;

import com.imjake9.server.lib.MessageTemplate;
import com.imjake9.server.lib.Messaging;
import com.imjake9.server.lib.Messaging.MessageLevel;
import java.util.logging.Logger;

public class JSEMessaging {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    /**
     * Logs a message to the console with the INFO and JSE prefixes.
     * 
     * @param message 
     */
    public static void info(String message) {
        log.info("[JSEssentials] " + message);
    }
    
    /**
     * Logs a message to the console with the INFO and JSE prefixes.
     * 
     * @param message
     * @param args 
     */
    public static void info(MessageTemplate message, String... args) {
        info(Messaging.fillArgs(message, args));
    }
    
    /**
     * Logs a message to the console with the WARNING and JSE prefixes.
     * 
     * @param message 
     */
    public static void warning(String message) {
        log.warning("[JSEssentials] " + message);
    }
    
    /**
     * Logs a message to the console with the WARNING and JSE prefixes.
     * 
     * @param message
     * @param args 
     */
    public static void warning(MessageTemplate message, String... args) {
        warning(Messaging.fillArgs(message, args));
    }
    
    /**
     * Logs a message to the console with the SEVERE and JSE prefixes.
     * 
     * @param message 
     */
    public static void severe(String message) {
        log.severe("[JSEssentials] " + message);
    }
    
    /**
     * Logs a message to the console with the SEVERE and JSE prefixes.
     * 
     * @param message
     * @param args 
     */
    public static void severe(MessageTemplate message, String... args) {
        severe(Messaging.fillArgs(message, args));
    }
    
    public static enum JSEMessage implements MessageTemplate {
        
        ME_CHAT (MessageLevel.PLAIN, "* %1 %2"),
        CURRENT_TIME (MessageLevel.PLAIN, "Current time in <aqua>%1</aqua> is <aqua><b>%2</b></aqua> (%3)."),
        INVALID_TIME_STRING (MessageLevel.ERROR, "Time <i>%1</i> is invalid."),
        NO_REPLY_PLAYER (MessageLevel.ERROR, "No player to reply to."),
        PM_CHAT (MessageLevel.PRIVATE, "[PM] to <u>%1</u>: %2"),
        PM_RECIEVE (MessageLevel.PRIVATE, "[PM] from <u>%1</u>: %2"),
        SPAWN_SET (MessageLevel.SUCCESS, "Spawn has been set to your location."),
        TELEPORT_OTHER ("You have teleported <i>%1</i> to <i>%2</i>."),
        TELEPORT_SELF ("You have teleported to <i>%1</i>."),
        TELEPORTED_FROM ("You have been teleported to <i>%1</i>."),
        TELEPORTED_TO ("Player <i>%1</i> has teleported to you."),
        WEATHER_SET (MessageLevel.PLAIN, "Weather set to <aqua><b>%1</b></aqua> in world <aqua>%2</aqua>."),
        WORLD_STATUS (MessageLevel.PLAIN, "World <aqua>%1</aqua> is <aqua><b>%2</b></aqua>.");
        
        private MessageLevel level;
        private String format;
        
        JSEMessage(MessageLevel level, String format) {
            this.level = level;
            this.format = Messaging.parseStyling(level.getOpeningTag() + format + level.getClosingTag());
        }
        
        JSEMessage(String format) {
            this(MessageLevel.NORMAL, format);
        }
        
        /**
         * Gets the raw message as a String.
         * 
         * @return message
         */
        @Override
        public String getMessage() {
            return this.format;
        }
        
        /**
         * Gets the message's level.
         * 
         * @return level
         */
        @Override
        public MessageLevel getLevel() {
            return this.level;
        }
        
    }
    
}
