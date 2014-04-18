package me.RyanWild.CJFreedomMod.Commands;

import me.RyanWild.CJFreedomMod.CJFM_Util;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Ban;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_TwitterHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "System Administration Management", usage = "/<command> <Teston | Testoff <saadd| sadelete| superdoom| adminworld <on | off> <username>>")
public class Command_sys extends CJFM_Command
{

    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (CJFM_Util.SYSADMINS.contains(sender.getName()) || sender.getName().equals("Camzie99"))
            {
                if (args[0].equalsIgnoreCase("teston"))
                {
                    sender.sendMessage("Please use the /dev command from now on to get access to this command.");
                }

                if (args[0].equalsIgnoreCase("testoff"))
                {
                    sender.sendMessage("Please use the /dev command from now on to get access to this command.");

                }
            }
        }

        if (args.length == 2)
        {
            /*if (args[0].equalsIgnoreCase("adminworld"))
             {
             if (CJFM_Util.SYSADMINS.contains(sender.getName().toLowerCase()) || CJFM_Util.EXECUTIVES.contains(sender.getName().toLowerCase()))
             {
             if (args[1].equalsIgnoreCase("on"))
             {
             TFM_ConfigEntry.ENABLE_ADMINWORLD.setBoolean(true);
             TFM_Util.adminAction(sender.getName(), "Enabling AdminWorld", false);
             return true;
             }

             if (args[1].equalsIgnoreCase("off"))
             {
             TFM_ConfigEntry.ENABLE_ADMINWORLD.setBoolean(false);
             TFM_Util.adminAction(sender.getName(), "Disabling AdminWorld", true);
             return true;
             }

             else
             {
             playerMsg(sender, "Invalid sub-command, possible values are: on, off");
             return false;
             }
             }
             else
             {
             sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
             TFM_Util.adminAction("WARNING: " + sender.getName(), "Has attempted to use a High Level Administration only command! The High Level Administration Team has been alerted!", true);
             sender.setOp(false);

             return true;
             }
             } */
        }

        else if (!CJFM_Util.SYSADMINS.contains(sender.getName()))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            TFM_Util.adminAction("WARNING: " + sender.getName(), "Has attempted to use a system admin only command. System administration team has been alerted.", true);

            if (!senderIsConsole)
            {
                sender.setOp(false);
            }
            else
            {
                sender.sendMessage("You are not a System Admin and may NOT use this command. If you feel this in error please contact a Developer.");
            }

            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("saadd"))
            {
                Player player = null;
                String playername = null;

                try
                {
                    player = getPlayer(args[1]);
                }
                catch (PlayerNotFoundException ex)
                {
                    final TFM_Admin superadmin = TFM_AdminList.getEntry(args[1]);
                    if (superadmin != null)
                    {
                        playername = superadmin.getLastLoginName();
                    }
                    else
                    {
                        playerMsg(ex.getMessage(), ChatColor.RED);
                        return true;
                    }
                }

                if (player != null)
                {
                    TFM_Util.adminAction(sender.getName(), "Adding " + player.getName() + " to the superadmin list.", true);
                    TFM_AdminList.addSuperadmin(player);
                }
                else if (playername != null)
                {
                    TFM_Util.adminAction(sender.getName(), "Adding " + playername + " to the superadmin list.", true);
                    TFM_AdminList.addSuperadmin(player);
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("sadelete") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove"))
            {
                if (!TFM_AdminList.isSeniorAdmin(sender))
                {
                    playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                String targetName = args[1];

                try
                {
                    targetName = getPlayer(targetName).getName();
                }
                catch (PlayerNotFoundException ex)
                {
                }

                if (!TFM_AdminList.getLowerSuperadminNames().contains(targetName.toLowerCase()))
                {
                    playerMsg("Superadmin not found: " + targetName);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Removing " + targetName + " from the superadmin list", true);
                TFM_AdminList.removeSuperadmin(Bukkit.getOfflinePlayer(targetName));

                // Twitterbot
                if (TFM_ConfigEntry.TWITTERBOT_ENABLED.getBoolean())
                {
                    TFM_TwitterHandler.getInstance().delTwitterVerbose(targetName, sender);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("superdoom"))
            {

                String reason = null;

                final Player player;
                try
                {
                    player = getPlayer(args[1]);
                }
                catch (PlayerNotFoundException ex)
                {
                    sender.sendMessage(ex.getMessage());
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Casting a dark shadow of oblivion over " + player.getName(), true);
                TFM_Util.bcastMsg(player.getName() + " will be completely obliviated!", ChatColor.RED);

                final String IP = player.getAddress().getAddress().getHostAddress().trim();

                // remove from whitelist
                player.setWhitelisted(false);

                // deop
                player.setOp(false);

                // set gamemode to survival
                player.setGameMode(GameMode.SURVIVAL);

                // clear inventory
                player.closeInventory();
                player.getInventory().clear();

                // ignite player
                player.setFireTicks(10000);

                // generate explosion
                player.getWorld().createExplosion(player.getLocation(), 4F);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // strike lightning
                        player.getWorld().strikeLightning(player.getLocation());
                    }
                }.runTaskLater(plugin, 20L * 2L);

                // generate explosion
                player.getWorld().createExplosion(player.getLocation(), 4F);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // strike lightning
                        player.getWorld().strikeLightning(player.getLocation());
                    }
                }.runTaskLater(plugin, 20L * 2L);

                // message
                TFM_Util.adminAction(player.getName(), "Has been Superdoomed, may the hell continue ", true);

                // ignite player
                player.setFireTicks(10000);

                // ban IP address:
                String ip = player.getAddress().getAddress().getHostAddress();
                String[] ipParts = ip.split("\\.");
                if (ipParts.length == 4)
                {
                    ip = String.format("%s.%s.*.*", ipParts[0], ipParts[1]);
                }
                TFM_Util.bcastMsg(String.format("Banning: %s, IP: %s.", player.getName(), ip), ChatColor.RED);

                TFM_BanManager.getInstance().addIpBan(new TFM_Ban(ip, player.getName(), sender.getName(), null, reason));

                // ban username:
                TFM_BanManager.getInstance().addUuidBan(new TFM_Ban(player.getUniqueId(), player.getName(), sender.getName(), null, reason));

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        // message
                        TFM_Util.adminAction(sender.getName(), "Has Superdoomed: " + player.getName() + ", IP: " + IP, true);

                        // generate explosion
                        player.getWorld().createExplosion(player.getLocation(), 4F);

                        // kick player
                        player.kickPlayer(ChatColor.RED + "FUCKOFF, and get your shit together you super doomed cunt!");
                    }
                }.runTaskLater(plugin, 20L * 3L);

            }
            else
            {

                return false;
            }

            return true;
        }
        return true;
    }
}
