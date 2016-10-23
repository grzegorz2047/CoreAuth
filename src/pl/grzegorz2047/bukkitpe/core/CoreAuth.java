package pl.grzegorz2047.bukkitpe.core;

import net.BukkitPE.BukkitPE;
import net.BukkitPE.Player;
import net.BukkitPE.Server;
import net.BukkitPE.command.Command;
import net.BukkitPE.command.CommandSender;
import net.BukkitPE.event.Event;
import net.BukkitPE.event.EventHandler;
import net.BukkitPE.event.Listener;
import net.BukkitPE.event.block.BlockBreakEvent;
import net.BukkitPE.event.block.BlockPlaceEvent;
import net.BukkitPE.event.player.PlayerJoinEvent;
import net.BukkitPE.event.player.PlayerQuitEvent;
import net.BukkitPE.plugin.PluginBase;
import pl.grzegorz2047.bukkitpe.core.auth.listeners.AuthListener;
import pl.grzegorz2047.bukkitpe.core.auth.connection.Hikari;
import pl.grzegorz2047.bukkitpe.core.auth.Auth;
import pl.grzegorz2047.bukkitpe.core.auth.motd.Motd;
import pl.grzegorz2047.bukkitpe.core.colorchat.ChatListener;
import pl.grzegorz2047.bukkitpe.core.config.ConfigManager;
import pl.grzegorz2047.bukkitpe.core.events.AuthSuccessEvent;
import pl.grzegorz2047.bukkitpe.core.protection.AttackListeners;
import pl.grzegorz2047.databaseapi.DatabaseAPI;
import pl.grzegorz2047.databaseapi.SQLUser;

import java.util.HashMap;

/**
 * Created by grzeg on 17.08.2016.
 */
public class CoreAuth extends PluginBase implements Listener {

    private DatabaseAPI playerManager;
    private Hikari hikari;
    private Auth auth;
    private ConfigManager configManager;
    // private HashMap<String, SQLUser> sqlUsers = new HashMap<String, SQLUser>();
    //private HashMap<String, String> settings = new HashMap<String, String>();

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        hikari = new Hikari(this,
                configManager.getConfig().getString("auth.host"),
                configManager.getConfig().getInt("auth.port"),
                configManager.getConfig().getString("auth.db"),
                configManager.getConfig().getString("auth.user"),
                configManager.getConfig().getString("auth.password")
        );

        /*
        playerManager = new DatabaseAPI(
                configManager.getConfig().getString("players.host"),
                configManager.getConfig().getInt("players.port"),
                configManager.getConfig().getString("players.db"),
                configManager.getConfig().getString("players.user"),
                configManager.getConfig().getString("players.password"));
        this.settings = playerManager.getSettings();
        */
        auth = new Auth(this);
        Server.getInstance().getPluginManager().registerEvents(new AuthListener(auth, this), this);
        Server.getInstance().getPluginManager().registerEvents(new ChatListener(this), this);
        Server.getInstance().getPluginManager().registerEvents(new AttackListeners(), this);
        Server.getInstance().getPluginManager().registerEvents(new Motd(), this);
        System.out.println(this.getName() + " zostal wlaczony!");
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        //e.setJoinMessage("");
        //e.getPlayer().sendMessage("Witaj na serwerze testowym CraftGames!");
        //this.getPlayerManager().insertPlayer(p.getName(), p.getAddress());
        //SQLUser user = this.getPlayerManager().getPlayer(p.getName());
        //this.sqlUsers.put(p.getName(), user);
        //e.getPlayer().sendPopup("§6Witaj, " + user.getRank() + " o nicku " + p.getPlayer().getName(), "§cSerwer w fazie alpha!");
        //e.getPlayer().sendPopup("§6Witaj, " + user.getRank() + " o nicku " + p.getPlayer().getName(), "§cSerwer w fazie alpha!");
    }

    @EventHandler
    void onJoin(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        //this.sqlUsers.remove(p.getName());
    }

    @EventHandler
    void onPlace(BlockPlaceEvent e) {
        // if (!e.getPlayer().isOp()) {
        //     e.getPlayer().sendPopup("§cNie mozesz tego postawic!", "§6Jest to aktualnie zablokowanexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        //     e.setCancelled(true);
        //  }
    }

    @EventHandler
    void onBreak(BlockBreakEvent e) {
        // if (!e.getPlayer().isOp()) {
        //      e.getPlayer().sendPopup("Nie mozesz tego zniszczyc!", "Jest to aktualnie zablokowane");
        //     e.setCancelled(true);
        // }
    }


    public DatabaseAPI getPlayerManager() {
        return playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getLabel().equals("login")) {
            boolean success = hikari.executeLogin(sender, args);
            if (success) {
                AuthSuccessEvent successEvent = new AuthSuccessEvent(sender.getName());
                Server.getInstance().getPluginManager().callEvent(successEvent);
            }
        }
        if (command.getLabel().equals("register")) {
            hikari.executeRegister(sender, args);
        }
        if (command.getLabel().equals("changepassword")) {
            hikari.executeChangePassword(sender, args);
        }
        return true;
    }

    public Hikari getHikari() {
        return hikari;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Auth getAuth() {
        return auth;
    }
/*
    public HashMap<String, SQLUser> getSqlUsers() {
        return sqlUsers;
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }*/
}
