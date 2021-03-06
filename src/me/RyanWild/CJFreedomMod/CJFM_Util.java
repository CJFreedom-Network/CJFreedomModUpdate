package me.RyanWild.CJFreedomMod;

import java.util.Arrays;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerRank;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CJFM_Util
{

    public static TotalFreedomMod plugin;

    public static final List<String> EXECUTIVES = Arrays.asList("");
    public static final List<String> MANAGERS = Arrays.asList("Wild1145", "Varuct", "thecjgcjg", "Ninjablue1", "Hockeyfan360");
    public static final List<String> DEVELOPERS = Arrays.asList("Madgeek1450", "DarthSalamon", "wild1145", "Paldiu", "Wahoozel", "Camzie99", "hawkeyeshi", "Paul19988");
    public static final List<String> FAMOUS = Arrays.asList(
            "skythekidrs", "antvenom", "deadlox", "stampylongnose", "sethbling", "asfjerome", "dantdm", "pokemondanlv45", "zexyzek", "ssundee",
            "explodingtnt", "kurtjmac", "xephos", "honeydew", "captainsparklez", "truemu", "jeb_", "grumm", "notch", "chimneyswift", "vechs",
            "cavemanfilms", "tobyturner", "inthelittlewood", "sips_", "sjin", "lividcofee", "etho");
    /* 

     public static void updateDatabase(String SQLquery) throws SQLException
     {
     Connection c = mySQL.openConnection();
     Statement statement = c.createStatement();
     statement.executeUpdate(SQLquery);
     }

     public void getValueFromDB(String SQLquery) throws SQLException
     {
     Connection c = mySQL.openConnection();
     Statement statement = c.createStatement();
     ResultSet res = statement.executeQuery(SQLquery);
     res.next();
     }
     */
    private final Server server;

    public CJFM_Util(TotalFreedomMod plugin)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    public void sendSyncMessage(final CommandSender sendTo, final String message)
    {
        Bukkit.getScheduler().runTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                sendTo.sendMessage(message);
            }
        });
    }

    public static void SeniorAdminChatMessage(CommandSender sender, String message, boolean senderIsConsole)
    {
        String name = sender.getName() + " " + TFM_PlayerRank.fromSender(sender).getPrefix() + ChatColor.WHITE;
        TFM_Log.info("[SENIOR-ADMIN] " + name + ": " + message);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (TFM_AdminList.isSeniorAdmin(player))
            {
                player.sendMessage("[" + ChatColor.YELLOW + "SENIOR-ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + ChatColor.YELLOW + message);
            }
        }
    }

    public static boolean inGod(Player player)
    {
        return TFM_PlayerData.getPlayerData(player).inGod();
    }

    public static void setGod(Player player, boolean enabled)
    {
        TFM_PlayerData.getPlayerData(player).setGod(enabled);
    }

    public void msg(CommandSender sender, String message)
    {
        sender.sendMessage(ChatColor.YELLOW + message);

    }

    public void adminAction(String admin, String action, ChatColor color)
    {
        server.broadcastMessage(color + admin + " - " + action);
    }

    public void adminAction(CommandSender sender, String action)
    {
        adminAction(sender.getName(), action, ChatColor.RED);
    }

}
