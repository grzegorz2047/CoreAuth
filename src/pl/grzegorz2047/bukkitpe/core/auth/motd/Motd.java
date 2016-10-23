package pl.grzegorz2047.bukkitpe.core.auth.motd;

import net.BukkitPE.Server;
import net.BukkitPE.event.EventHandler;
import net.BukkitPE.event.Listener;
import net.BukkitPE.event.server.QueryRegenerateEvent;

/**
 * Created by grzeg on 18.08.2016.
 */
public class Motd implements Listener {

    @EventHandler
    void onMotdPing(QueryRegenerateEvent event) {
        //event.setServerName(event.getServerName().replaceAll("&","§"));
        //event.setPlayerCount(10);
        //event.setMaxPlayerCount(20);
        Server.getInstance().getNetwork().setName("test");
    }

}
