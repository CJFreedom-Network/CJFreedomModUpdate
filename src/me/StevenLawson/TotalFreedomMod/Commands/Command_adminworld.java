package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminWorld;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Go to the AdminWorld.", usage = "/<command> [guest < list | purge | add <player> | remove <player> > | time <morning | noon | evening | night> | weather <off | on | storm>]")
public class Command_adminworld extends TFM_Command
{
    private enum CommandMode
    {
        TELEPORT, GUEST, TIME, WEATHER
    }

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        CommandMode commandMode = null;

        if (args.length == 0)
        {
            commandMode = CommandMode.TELEPORT;
        }
        else if (args.length >= 2)
        {
            if ("guest".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.GUEST;
            }
            else if ("time".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.TIME;
            }
            else if ("weather".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.WEATHER;
            }
        }

        if (commandMode == null)
        {
            return false;
        }

        try
        {
            switch (commandMode)
            {
                case TELEPORT:
                {
                    World adminWorld = null;
                    try
                    {
                        adminWorld = TFM_AdminWorld.getInstance().getWorld();
                    }
                    catch (Exception ex)
                    {
                    }

                    if (adminWorld == null || sender_p.getWorld() == adminWorld)
                    {
                        playerMsg("Going to the main world.");
                        sender_p.teleport(server.getWorlds().get(0).getSpawnLocation());
                    }
                    else
                    {
                        playerMsg("Going to the AdminWorld.");
                        TFM_AdminWorld.getInstance().sendToWorld(sender_p);
                    }

                    break;
                }
                case GUEST:
                {
                    if (args.length == 2)
                    {
                        if ("list".equalsIgnoreCase(args[1]))
                        {
                            playerMsg("AdminWorld Guest List: " + TFM_AdminWorld.getInstance().guestListToString());
                        }
                        else if ("purge".equalsIgnoreCase(args[1]))
                        {
                            assertCommandPerms(sender, sender_p);
                            TFM_AdminWorld.getInstance().purgeGuestList();
                            playerMsg("AdminWorld guest list purged.");
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else if (args.length == 3)
                    {
                        assertCommandPerms(sender, sender_p);

                        if ("add".equalsIgnoreCase(args[1]))
                        {
                            try
                            {
                                TFM_AdminWorld.getInstance().addGuest(getPlayer(args[2]), sender_p);
                            }
                            catch (PlayerNotFoundException ex)
                            {
                                sender.sendMessage(ex.getMessage());
                                return true;
                            }
                        }
                        else if (TFM_Util.isRemoveCommand(args[1]))
                        {
                            Player player = TFM_AdminWorld.getInstance().removeGuest(args[2]);
                            if (player != null)
                            {
                                playerMsg("Guest removed: " + player.getName());
                            }
                            else
                            {
                                playerMsg("Can't find guest entry for: " + args[2]);
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }

                    break;
                }
                case TIME:
                {
                    assertCommandPerms(sender, sender_p);

                    if (args.length == 2)
                    {
                        //set time = args[1] : <morning|noon|evening|night>

                        playerMsg("Feature not implemented.");
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                case WEATHER:
                {
                    assertCommandPerms(sender, sender_p);

                    if (args.length == 2)
                    {
                        //set weather = args[1] : <off|on|storm>

                        playerMsg("Feature not implemented.");
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                default:
                {
                    return false;
                }
            }
        }
        catch (PermissionDeniedException ex)
        {
            sender.sendMessage(ex.getMessage());
        }

        return true;
    }

    private void assertCommandPerms(CommandSender sender, Player sender_p) throws PermissionDeniedException
    {
        if (!(sender instanceof Player) || sender_p == null || !TFM_SuperadminList.isUserSuperadmin(sender))
        {
            throw new PermissionDeniedException(TotalFreedomMod.MSG_NO_PERMS);
        }
    }

    private class PermissionDeniedException extends Exception
    {
        public PermissionDeniedException(String string)
        {
            super(string);
        }
    }
}