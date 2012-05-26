package com.imjake9.server.essentials.utils;

import com.imjake9.server.lib.MessageTemplate;
import com.imjake9.server.lib.Messaging;
import com.imjake9.server.lib.Messaging.MessageLevel;

public enum JSEMessage implements MessageTemplate {
    
    ME_CHAT(MessageLevel.PLAIN, "* %1 %2"),
    CURRENT_TIME(MessageLevel.PLAIN, "Current time in <aqua>%1</aqua> is <aqua><b>%2</b></aqua> (%3)."),
    GAME_MODE_SET(MessageLevel.PLAIN, "Player <aqua>%1</aqua> set to game mode <aqua>%2</aqua>."),
    INVALID_GAME_MODE(MessageLevel.ERROR, "Gamemode <i>%1</i> does not exist."),
    INVALID_ITEM_NAME(MessageLevel.ERROR, "There is no item named <i>%1</i>."),
    INVALID_TIME_STRING(MessageLevel.ERROR, "Time <i>%1</i> is invalid."),
    ITEM_GIVEN(MessageLevel.PLAIN, "You gave <aqua>%1</aqua> some <aqua>%2</aqua>."),
    ITEM_RECIEVED(MessageLevel.PLAIN, "You have recieved some <aqua>%1</aqua> from <aqua>%2</aqua>."),
    ITEM_FULFILLED(MessageLevel.PLAIN, "You have recieved some <aqua>%1</aqua>."),
    NO_REPLY_PLAYER(MessageLevel.ERROR, "No player to reply to."),
    PM_CHAT(MessageLevel.PRIVATE, "[PM] to <u>%1</u>: %2"),
    PM_RECIEVE(MessageLevel.PRIVATE, "[PM] from <u>%1</u>: %2"),
    SPAWN_SET(MessageLevel.SUCCESS, "Spawn has been set to your location."),
    TELEPORT_OTHER("You have teleported <i>%1</i> to <i>%2</i>."),
    TELEPORT_SELF("You have teleported to <i>%1</i>."),
    TELEPORTED_FROM("You have been teleported to <i>%1</i>."),
    TELEPORTED_TO("Player <i>%1</i> has teleported to you."),
    WEATHER_SET(MessageLevel.PLAIN, "Weather set to <aqua><b>%1</b></aqua> in world <aqua>%2</aqua>."),
    WORLD_STATUS(MessageLevel.PLAIN, "World <aqua>%1</aqua> is <aqua><b>%2</b></aqua>.");
    
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
