package pl.grzegorz2047.bukkitpe.core.colorchat;

import net.BukkitPE.Player;
import net.BukkitPE.event.EventHandler;
import net.BukkitPE.event.Listener;
import net.BukkitPE.event.player.PlayerChatEvent;
import net.BukkitPE.event.player.PlayerCommandPreprocessEvent;
import pl.grzegorz2047.bukkitpe.core.CoreAuth;
import pl.grzegorz2047.databaseapi.SQLUser;

/**
 * Created by grzeg on 17.08.2016.
 */
public class ChatListener implements Listener {

    private final CoreAuth plugin;

    public ChatListener(CoreAuth plugin) {
        this.plugin = plugin;
    }
/*
    @EventHandler
    void onPreCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/version")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onChat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        SQLUser user = plugin.getSqlUsers().get(p.getName());
        String format = plugin.getSettings().get("chat." + user.getRank().toLowerCase());
        String message = e.getMessage().replace('%', ' ');
        message = e.getMessage().replaceAll("&", "§");
        e.setMessage(message);
        e.setFormat(format.replace("{DISPLAYNAME}", p.getDisplayName()).replace("{MESSAGE}", message).replace("{LANG}", user.getLanguage()));
    }
*/
}
