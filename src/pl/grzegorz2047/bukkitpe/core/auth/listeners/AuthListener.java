package pl.grzegorz2047.bukkitpe.core.auth.listeners;


import net.BukkitPE.Player;
import net.BukkitPE.Server;
import net.BukkitPE.event.EventHandler;
import net.BukkitPE.event.EventPriority;
import net.BukkitPE.event.Listener;
import net.BukkitPE.event.block.BlockBreakEvent;
import net.BukkitPE.event.block.BlockPlaceEvent;
import net.BukkitPE.event.entity.EntityDamageByEntityEvent;
import net.BukkitPE.event.player.*;
import net.BukkitPE.scheduler.AsyncTask;
import pl.grzegorz2047.bukkitpe.core.CoreAuth;
import pl.grzegorz2047.bukkitpe.core.auth.Auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author neksi
 */
public class AuthListener implements Listener {

    private final Auth auth;

    public CoreAuth plugin;

    public AuthListener(Auth auth, CoreAuth plugin) {
        this.auth = auth;
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerConnect(PlayerLoginEvent ev) {
        ev.getPlayer().sendMessage("§bNie jestes zalogowany! Zaloguj sie uzywajac: " + "§c/login haslo");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void Command(PlayerChatEvent e) {
        String regex = "[a-z0-9]+[\\.]{1}[a-z0-9]+[\\.]{1}[a-z0-9]+[\\.]{1}[a-z0-9]+";
        String regex2 = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
        Player p = e.getPlayer();
        if (!auth.getAuthenticated().contains(p.getName()) && !e.getMessage().startsWith("/login") && !e.getMessage().startsWith("/register")) {
            p.sendMessage("§cZaloguj sie aby moc rozmawiac!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerConnected(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();

        Server.getInstance().getScheduler().scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {

                Connection connection = null;
                PreparedStatement statement = null;
                try {
                    connection = plugin.getHikari().getHikari().getConnection();
                    String table = plugin.getConfigManager().getConfig().getString("auth.table");
                    statement = connection.prepareStatement("SELECT username FROM " + table + " WHERE username = ?");
                    statement.setString(1, player.getName());
                    ResultSet set = statement.executeQuery();
                    if (set.next()) {
                        player.sendMessage("§2Zaloguj sie przy uzyciu komendy " + "§6/login haslo");
                    } else {
                        player.sendMessage("§aNie posiadasz konta, zarejestruj sie! " + "/register haslo email");
                    }
                } catch (SQLException ex) {
                    System.out.print(ex.getCause() + " " + ex.getMessage());

                    player.kick("§cProblem z baza danych, zglos administracji!");
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!auth.getAuthenticated().contains(e.getPlayer().getName())) {
            e.setTo(e.getFrom());
            warnPlayer(e.getPlayer());
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onPlace(BlockPlaceEvent e) {
        if (!auth.getAuthenticated().contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            warnPlayer(e.getPlayer());
        }
    }

    @EventHandler
    void onBreak(BlockBreakEvent e) {
        if (!auth.getAuthenticated().contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            warnPlayer(e.getPlayer());
        }
    }
    @EventHandler
    public void onDamageSomething(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player){
            Player p = (Player) e.getDamager();
            if (!auth.getAuthenticated().contains(p.getName())) {
                e.setCancelled(true);
                warnPlayer(p);
            }
        }
    }

    private void warnPlayer(Player p) {
        p.sendPopup("§cZaloguj sie lub zarejestruj aby moc grac!", "§2Wpisz §6/register §2albo §6/login");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        auth.getAuthenticated().remove(e.getPlayer().getName());
    }
}