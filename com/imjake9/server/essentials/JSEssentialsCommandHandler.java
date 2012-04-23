package com.imjake9.server.essentials;

import com.imjake9.server.essentials.utils.JSEMessage;
import com.imjake9.server.lib.Messaging;
import com.imjake9.server.lib.Messaging.JSMessage;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class JSEssentialsCommandHandler implements CommandExecutor {
    
    private static Map<String, String> lastPM = new HashMap<String, String>();
    
    public JSEssentialsCommandHandler() {
        
        JSEssentials plugin = JSEssentials.getPlugin();
        
        for (JSEssentialsCommands command : JSEssentialsCommands.values()) {
            
            PluginCommand cmd = plugin.getCommand(command.name().toLowerCase());
            
            if (cmd == null)
                continue;
            
            cmd.setAliases(Arrays.asList(command.getAliases()));
            cmd.setPermission(command.getPermission());
            cmd.setPermissionMessage(command.getPermissionMessage());
            cmd.setExecutor(this);
            
        }
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String... args) {
        
        return JSEssentialsCommands.handleCommand(command, sender, args);
        
    }
    
    public static enum JSEssentialsCommands {
        
        /********************
         * General Commands *
         ********************/
        
        ITEM("give", "i") {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                return false;
            }
            
        },
        TIME() {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                
                // Set up parameters
                boolean explicitWorld = false;
                World world;
                String subcommand = null;
                
                // Non-players must specify a world
                if (!(sender instanceof Player)) {
                    if (args.length == 0) {
                        Messaging.send(JSMessage.MISSING_PARAMETER, sender, "world");
                        return false;
                    } else if (Bukkit.getWorld(args[0]) == null) {
                        Messaging.send(JSMessage.INVALID_WORLD, sender, args[0]);
                        return false;
                    }
                }
                // If specified, register a world
                if (args.length != 0 && Bukkit.getWorld(args[0]) != null) {
                    world = Bukkit.getWorld(args[0]);
                    explicitWorld = true;
                } else {
                    world = ((Player)sender).getWorld();
                }
                // Get the actual action
                if (explicitWorld && args.length >= 2) {
                    subcommand = args[1].toLowerCase();
                } else if (args.length >= 1) {
                    subcommand = args[0].toLowerCase();
                }
                // Only accept correct subcommands
                if (subcommand != null && !subcommand.equals("set") && !subcommand.equals("day") && !subcommand.equals("night")) {
                    Messaging.send(JSMessage.INVALID_PARAMTER, sender, "command", subcommand);
                    return false;
                }
                // If no command specified, display the current time
                if (subcommand == null) {
                    Messaging.send(JSEMessage.CURRENT_TIME, sender, world.getName(), getFormattedTime(world), String.valueOf(world.getTime()));
                    return true;
                }
                
                // Check set permission
                if (!hasSubPermission(sender, "set")) {
                    Messaging.send(JSMessage.LACKS_PERMISSION, sender, getSubPermission("set"));
                    return true;
                }
                
                // Set up parameters
                String timeString;
                
                // If using set, get the timestring
                if (subcommand.equals("set")) {
                    if (args.length < (explicitWorld ? 3 : 2)) {
                        Messaging.send(JSMessage.MISSING_PARAMETER, sender, "value");
                        return false;
                    }
                    timeString = args[explicitWorld ? 2 : 1];
                } else {
                    timeString = subcommand;
                }
                
                // Attempt to set the time
                if (!setTime(world, timeString)) {
                    Messaging.send(JSEMessage.INVALID_TIME_STRING, sender, timeString);
                    return false;
                }
                
                return true;
            }
            
            /**
             * Gets the time as a formatted, displayable string.
             */
            private String getFormattedTime(World world) {
                // Grab the raw time
                long time = world.getTime();
                // Isolate the hour as a single int
                int hour = (int)Math.floor(time/1000);
                // Isolate the minutes and convert from out of 100 to out of 60
                int minute = (int)Math.floor((time - (hour * 1000)) * (0.06));
                // Register whether it's AM or PM
                boolean pm = time >= 6000 && time < 18000;
                // Fix hours based on derpy Minecraft time
                hour += 6;
                // Convert to 12-hour time from 24-hour time
                hour %= 12;
                // Display hour 0 as hour 12
                if (hour == 0) hour = 12;
                // Build the formatted string
                return hour + ":" + chopPad(String.valueOf(minute), 2) + (pm ? "PM" : "AM");
            }
            
            /**
             * Sets the time using a formatted string.
             */
            private boolean setTime(World world, String time) {
                // Make sure the time is lower-cased for easy processing
                time = time.toLowerCase();
                // Handle preset time strings
                if (time.equals("day")) {
                    world.setTime(0);
                    return true;
                }
                if (time.equals("night")) {
                    world.setTime(13000);
                    return true;
                }
                // Try to directly process the time
                try {
                    int direct = Integer.parseInt(time);
                    world.setTime(direct);
                    return true;
                } catch (NumberFormatException ex) {}
                // Remove 'm' from the string, since it is worthless (eg 10h5m)
                time.replaceAll("m", "");
                // Try splitting based on colon
                String[] split = time.split(":");
                // Try splitting based on h
                if (split.length == 1)
                    split = time.split("h");
                // If string is made of 2 components, build time
                if (split.length == 2) {
                    try {
                        // Pull the hour
                        long hour = Integer.parseInt(split[0]);
                        // Pull the minute
                        long minute = Integer.parseInt(split[1]);
                        // Combine the two and convert to Minecraft's derpy time system
                        long fullTime = ((hour - 6) * 1000) + (long)(minute * (100.0/6.0));
                        // Set the time
                        world.setTime(fullTime);
                        return true;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                return false;
            }
            
            /**
             * Pads a number to be a certain number of places.
             */
            private String chopPad(String str, int digits) {
                // Copy string
                String ret = str.toString();
                // Only finish when the string is the right length
                while (ret.length() != digits) {
                    // Chop or add
                    if (ret.length() > digits)
                        ret = ret.substring(0, ret.length() - 1);
                    else
                        ret += "0";
                }
                return ret;
            }
            
        },
        WEATHER() {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                
                // Set up parameters
                World world;
                String set = null;
                
                // Non-players must specify a world
                if (!(sender instanceof Player)) {
                    if (args.length == 0) {
                        Messaging.send(JSMessage.MISSING_PARAMETER, sender, "world");
                        return false;
                    } else if (Bukkit.getWorld(args[0]) == null) {
                        Messaging.send(JSMessage.INVALID_WORLD, sender, args[0]);
                        return false;
                    }
                }
                // If specified, register a world
                if (args.length != 0 && Bukkit.getWorld(args[0]) != null) {
                    world = Bukkit.getWorld(args[0]);
                } else {
                    world = ((Player)sender).getWorld();
                }
                // Get the actual action
                if (args.length == 1 && Bukkit.getWorld(args[0]) == null) {
                    set = args[0].toLowerCase();
                } else if (args.length > 1) {
                    set = args[1].toLowerCase();
                }
                // Only accept actual commands
                if (set != null && !set.equals("sun") && !set.equals("storm")) {
                    Messaging.send(JSMessage.INVALID_PARAMTER, sender, "type", args[0]);
                    return false;
                }
                
                // If no command, display the weather
                if (set == null) {
                    Messaging.send(JSEMessage.WORLD_STATUS, sender, world.getName(), world.hasStorm() ? "stormy" : "sunny");
                    return true;
                }
                
                // Check set permission
                if (!hasSubPermission(sender, "set")) {
                    Messaging.send(JSMessage.LACKS_PERMISSION, sender, getSubPermission("set"));
                    return true;
                }
                
                // Update the weather
                world.setStorm(set.equals("storm"));
                Messaging.send(JSEMessage.WEATHER_SET, sender, set.equals("storm") ? "stormy" : "sunny", world.getName());
                
                return true;
            }
            
        },
        SPAWNMOB("smob") {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                return false;
            }
            
        },
        GAMEMODE("gm") {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                
                // Set up parameters
                String player;
                String mode;
                
                // Parse command
                if (!(sender instanceof Player)) {
                    if (args.length == 0) {
                        Messaging.send(JSMessage.MISSING_PARAMETER, sender, "player");
                        return false;
                    }
                    player = args[0];
                    if (args.length > 1)
                        mode = args[1];
                    else
                        mode = "toggle";
                } else {
                    if (args.length > 1) {
                        player = args[0];
                        mode = args[1];
                    } else if (args.length > 0) {
                        if (!args[0].equals("0") && !args[0].equals("1") && !args[0].equalsIgnoreCase("creative") && !args[0].equalsIgnoreCase("survival")) {
                            player = args[0];
                            mode = "toggle";
                        } else {
                            player = ((Player) sender).getName();
                            mode = args[0];
                        }
                    } else {
                        player = ((Player) sender).getName();
                        mode = "toggle";
                    }
                }
                
                Player target = Bukkit.getPlayer(player);
                
                // The player must exist
                if (target == null) {
                    Messaging.send(JSMessage.INVALID_PLAYER, sender, player);
                    return true;
                }
                
                // Define toggle command
                if (mode.equalsIgnoreCase("toggle")) {
                    if (target.getGameMode() == GameMode.SURVIVAL)
                        mode = "1";
                    else
                        mode = "0";
                }
                
                // Execute command
                if (mode.equals("0") || mode.equalsIgnoreCase("survival")) {
                    target.setGameMode(GameMode.SURVIVAL);
                    Messaging.send(JSEMessage.GAME_MODE_SET, sender, player, "survival");
                    return true;
                }
                if (mode.equals("1") || mode.equalsIgnoreCase("creative")) {
                    target.setGameMode(GameMode.CREATIVE);
                    Messaging.send(JSEMessage.GAME_MODE_SET, sender, player, "creative");
                    return true;
                }
                
                // Gamemode was wrong
                Messaging.send(JSEMessage.INVALID_GAME_MODE, sender, mode);
                return false;
            }
            
        },
        
        /*********************
         * Teleport Commands *
         *********************/
        
        SPAWN() {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                // Only players can use /spawn
                if (!(sender instanceof Player)) {
                    Messaging.send(JSMessage.ONLY_PLAYERS, sender);
                    return true;
                }
                // Teleport the player to the spawn location
                Player player = (Player)sender;
                Location location = player.getWorld().getSpawnLocation();
                // Center player on block
                location.setX(location.getX() + 0.5);
                location.setZ(location.getZ() + 0.5);
                // Keep player orientation
                location.setPitch(player.getLocation().getPitch());
                location.setYaw(player.getLocation().getYaw());
                player.teleport(location);
                return true;
            }
            
        },
        SETSPAWN() {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                // Only players can use /setspawn
                if (!(sender instanceof Player)) {
                    Messaging.send(JSMessage.ONLY_PLAYERS, sender);
                    return true;
                }
                // Set the spawn location to the player's current location
                Player player = (Player)sender;
                Location pLoc = player.getLocation();
                player.getWorld().setSpawnLocation(pLoc.getBlockX(), pLoc.getBlockY(), pLoc.getBlockZ());
                Messaging.send(JSEMessage.SPAWN_SET, sender);
                return true;
            }
            
        },
        TELEPORT("tp") {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                // Non-players must fill the target parameter
                if (!(sender instanceof Player) && args.length < 2) {
                    Messaging.send(JSMessage.MISSING_PARAMETER, sender, "target");
                    return false;
                }
                // The destination parameter must be filled
                if (args.length == 0) {
                    Messaging.send(JSMessage.MISSING_PARAMETER, sender, "destination");
                    return false;
                }
                // The destination player must be online
                if (Bukkit.getPlayer(args[0]) == null) {
                    Messaging.send(JSMessage.INVALID_PLAYER, sender, args[0]);
                    return false;
                }
                // If no target, teleport the user 
                if (args.length == 1) {
                    Player destination = Bukkit.getPlayer(args[0]);
                    ((Player)sender).teleport(destination);
                    Messaging.send(JSEMessage.TELEPORT_SELF, sender, destination.getName());
                    Messaging.send(JSEMessage.TELEPORTED_TO, destination, sender.getName());
                    return true;
                }
                // The target player must be online
                if (Bukkit.getPlayer(args[1]) == null) {
                    Messaging.send(JSMessage.INVALID_PLAYER, sender, args[1]);
                    return false;
                }
                // Teleport the target to the destination
                Player target = Bukkit.getPlayer(args[0]);
                Player destination = Bukkit.getPlayer(args[1]);
                target.teleport(destination);
                Messaging.send(JSEMessage.TELEPORT_OTHER, sender, target.getName(), destination.getName());
                Messaging.send(JSEMessage.TELEPORTED_FROM, target, destination.getName());
                Messaging.send(JSEMessage.TELEPORTED_TO, destination, target.getName());
                
                return true;
            }
            
        },
        TELEPORTHERE("tphere") {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                // Only players can use /tphere
                if (!(sender instanceof Player)) {
                    Messaging.send(JSMessage.ONLY_PLAYERS, sender);
                    return true;
                }
                // The target parameter must be filled
                if (args.length == 0) {
                    Messaging.send(JSMessage.MISSING_PARAMETER, sender, "target");
                    return false;
                }
                // The target player must be online
                if (Bukkit.getPlayer(args[0]) == null) {
                    Messaging.send(JSMessage.INVALID_PLAYER, sender, args[0]);
                    return false;
                }
                // Teleport the target to the user
                Player target = Bukkit.getPlayer(args[0]);
                target.teleport((Player)sender);
                Messaging.send(JSEMessage.TELEPORT_OTHER, sender, target.getName(), "yourself");
                Messaging.send(JSEMessage.TELEPORTED_FROM, target, sender.getName());
                
                return true;
            }
            
        },
        TELEPORTCOORDINATES("tppos", "tpcoord") {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                // Non-players must provide a target
                if (!(sender instanceof Player) && args.length < 5) {
                    Messaging.send(JSMessage.MISSING_PARAMETER, sender, "target");
                    return false;
                }
                // The parameters must be filled
                String parameter = null;
                switch (args.length) {
                    case 0:
                        parameter = "world";
                        break;
                    case 1:
                        parameter = "x";
                        break;
                    case 2:
                        parameter = "y";
                        break;
                    case 3:
                        parameter = "z";
                        break;
                }
                if (parameter != null) {
                    Messaging.send(JSMessage.MISSING_PARAMETER, sender, parameter);
                    return false;
                }
                // Get target player
                Player target;
                int explicitTarget = (args.length >= 5) ? 1 : 0;
                if (explicitTarget == 1) {
                    target = Bukkit.getPlayer(args[0]);
                } else {
                    target = (Player)sender;
                }
                // The target player must be online
                if (target == null) {
                    Messaging.send(JSMessage.INVALID_PLAYER, sender, args[0]);
                    return false;
                }
                // The world must exist
                if (Bukkit.getWorld(args[0 + explicitTarget]) == null) {
                    Messaging.send(JSMessage.INVALID_WORLD, sender, args[0 + explicitTarget]);
                    return false;
                }
                // Validate inputs
                Location destination;
                try {
                    destination = new Location(
                            Bukkit.getWorld(args[0 + explicitTarget]),
                            Integer.parseInt(args[1 + explicitTarget]),
                            Integer.parseInt(args[2 + explicitTarget]),
                            Integer.parseInt(args[3 + explicitTarget]));
                } catch (NumberFormatException ex) {
                    Messaging.send(JSMessage.INVALID_LOCATION, sender);
                    return false;
                }
                // Teleport player
                ((Player)sender).teleport(destination);
                return true;
            }
            
        },
        
        /*****************
         * Chat Commands *
         *****************/
        
        ME() {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                String message = "";
                for (String piece : args) {
                    message += piece + " ";
                }
                Messaging.broadcast(JSEMessage.ME_CHAT, (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName(), message);
                return true;
            }
            
        },
        TELL("msg", "pm") {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                // Must put a player and a message
                if (args.length < 2) {
                    Messaging.send(JSMessage.MISSING_PARAMETER, sender, "message");
                    return false;
                }
                Player recipient = Bukkit.getPlayer(args[0]);
                // Recipient must be online
                if (recipient == null) {
                    Messaging.send(JSMessage.INVALID_PLAYER, sender, args[0]);
                    return false;
                }
                // Generate message
                String message = "";
                for (String piece : Arrays.copyOfRange(args, 1, args.length)) {
                    message += piece + " ";
                }
                // Send message
                Messaging.send(JSEMessage.PM_CHAT, sender, recipient.getName(), message);
                Messaging.send(JSEMessage.PM_RECIEVE, recipient, sender.getName(), message);
                // Save last PM for /reply
                if (sender instanceof Player)
                    lastPM.put(recipient.getName(), sender.getName());
                return true;
            }
            
        },
        REPLY() {
            
            @Override
            public boolean handle(CommandSender sender, String... args) {
                // Must be a message
                if (args.length == 0) {
                    Messaging.send(JSMessage.MISSING_PARAMETER, sender, "message");
                    return false;
                }
                Player recipient;
                // Register player only if one is available
                if (lastPM.get(sender.getName()) == null || (recipient = Bukkit.getPlayer(lastPM.get(sender.getName()))) == null) {
                    Messaging.send(JSEMessage.NO_REPLY_PLAYER, sender);
                    return true;
                }
                // Add player name to args and use /tell
                List<String> argsList = new ArrayList<String>();
                argsList.addAll(Arrays.asList(args));
                argsList.add(0, recipient.getName());
                TELL.handle(sender, argsList.toArray(new String[0]));
                return true;
            }
            
        };
        
        private String[] aliases;
        
        public static boolean handleCommand(Command command, CommandSender sender, String... args) {
            JSEssentialsCommands handler = valueOf(command.getName().toUpperCase());
            if (!handler.hasPermission(sender)) {
                Messaging.send(JSMessage.LACKS_PERMISSION, sender, handler.getPermission());
                return true;
            }
            return valueOf(command.getName().toUpperCase()).handle(sender, args);
        }
        
        JSEssentialsCommands(String... aliases) {
            this.aliases = aliases;
        }
        
        public String[] getAliases() {
            return this.aliases;
        }
        
        public String getPermission() {
            return JSEssentials.getPlugin().getPermissionsManager().getPermission(name().toLowerCase());
        }
        
        public boolean hasPermission(CommandSender sender) {
            return JSEssentials.getPlugin().getPermissionsManager().hasPermission(sender, name().toLowerCase());
        }
        
        public String getPermissionMessage() {
            return Messaging.fillArgs(JSMessage.LACKS_PERMISSION, getPermission());
        }
        
        public String getSubPermission(String node) {
            return JSEssentials.getPlugin().getPermissionsManager().getPermission(name().toLowerCase() + "." + node);
        }
        
        public boolean hasSubPermission(CommandSender sender, String node) {
            return JSEssentials.getPlugin().getPermissionsManager().hasPermission(sender, name().toLowerCase() + "." + node);
        }
        
        public abstract boolean handle(CommandSender sender, String... args);
        
    }
    
}
