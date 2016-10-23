package pl.grzegorz2047.bukkitpe.core.protection;

import net.BukkitPE.Player;
import net.BukkitPE.Server;
import net.BukkitPE.event.EventHandler;
import net.BukkitPE.event.Listener;
import net.BukkitPE.event.entity.EntityDamageByEntityEvent;
import net.BukkitPE.event.entity.EntityDamageEvent;
import net.BukkitPE.level.Location;
import net.BukkitPE.level.Position;

/**
 * Created by grzeg on 17.08.2016.
 */
public class AttackListeners implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {/*
       Position loc = e.getEntity().getLevel().getSpawnLocation();
        Position pLoc = e.getEntity().getPosition();
        if (loc.distance(pLoc) < 10) {
            e.setCancelled(true);
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                p.sendPopup("Nie mozesz bic sie blisko spawnu!");
            }
        }
        e.setCancelled(true);
       */
        Server.getInstance().getLogger().info("Wykonalem event!");
    }

    @EventHandler
    public void onDamageSomething(EntityDamageByEntityEvent e) {
       // e.setCancelled(true);
    }

}
