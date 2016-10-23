package pl.grzegorz2047.bukkitpe.core.events;

import com.sun.istack.internal.NotNull;
import net.BukkitPE.event.Event;
import net.BukkitPE.event.HandlerList;
import pl.grzegorz2047.bukkitpe.core.auth.Auth;

/**
 * Created by grzeg on 22.10.2016.
 */
public class AuthSuccessEvent extends Event {

    private String username = "";
    private static final HandlerList handlers = new HandlerList();

    public AuthSuccessEvent(String username) {
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
