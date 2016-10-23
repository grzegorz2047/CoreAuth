package pl.grzegorz2047.bukkitpe.core.config;

import net.BukkitPE.utils.Config;
import pl.grzegorz2047.bukkitpe.core.CoreAuth;

import java.io.File;

/**
 * Created by grzeg on 17.08.2016.
 */
public class ConfigManager {

    Config conf;

    public ConfigManager(CoreAuth coreAuth) {
        if(!new File(coreAuth.getDataFolder() + "").exists()){
            new File(coreAuth.getDataFolder() + "").mkdir();
        }
        String path = coreAuth.getDataFolder() + File.separator + "config.yml";
        if (!new File(path).exists()) {
            conf = new Config(path, Config.YAML);
            conf.save();
            conf.set("auth.host", "");
            conf.set("auth.port", "3306");
            conf.set("auth.db", "");
            conf.set("auth.table", "");
            conf.set("auth.user", "");
            conf.set("auth.password", "");
            conf.set("players.host", "");
            conf.set("players.port", "3306");
            conf.set("players.db", "");
            conf.set("players.table", "");
            conf.set("players.user", "");
            conf.set("chat.format", "&7[&e%rank%] %nick%: %msg%");
            conf.save();
        } else {
            conf = new Config(path, Config.YAML);
        }
    }

    public Config getConfig() {
        return conf;
    }

}
