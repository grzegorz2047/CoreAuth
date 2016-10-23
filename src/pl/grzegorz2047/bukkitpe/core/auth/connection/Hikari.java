package pl.grzegorz2047.bukkitpe.core.auth.connection;

import com.zaxxer.hikari.HikariDataSource;
import net.BukkitPE.Player;
import net.BukkitPE.command.CommandSender;
import pl.grzegorz2047.bukkitpe.core.CoreAuth;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grzeg on 17.08.2016.
 */
public class Hikari {

    private CoreAuth plugin;

    private HikariDataSource hikari;

    public Hikari(CoreAuth plugin, String host, int port, String db, String user, String password) {
        hikari = new HikariDataSource();
        //hikari.setMaximumPoolSize(20);
        this.plugin = plugin;
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", db);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", password);
        hikari.addDataSourceProperty("cachePrepStmts", true);
        hikari.addDataSourceProperty("prepStmtCacheSize", 250);
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        executeCreateTable();
    }

    public HikariDataSource getHikari() {
        return hikari;
    }

    private boolean executeCreateTable() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = hikari.getConnection();
            String table = plugin.
                    getConfigManager().
                    getConfig()
                    .getString("auth.table");

            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS `AuthMe` (" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT," +
                    "  `username` varchar(255) NOT NULL," +
                    "  `password` varchar(255) NOT NULL," +
                    "  `ip` varchar(40) NOT NULL," +
                    "  `lastlogin` bigint(32) DEFAULT NULL," +
                    "  `x` double NOT NULL DEFAULT '0'," +
                    "  `y` double NOT NULL DEFAULT '0'," +
                    "  `z` double NOT NULL DEFAULT '0'," +
                    "  `world` varchar(255) NOT NULL DEFAULT 'world'," +
                    "  `email` varchar(255) DEFAULT 'your@email.com'," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE KEY `username` (`username`)," +
                    "  KEY `username_2` (`username`)" +
                    ") ENGINE=InnoDB  DEFAULT CHARSET=latin2");

        } catch (SQLException ex) {
            ex.getStackTrace();
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
        return true;
    }

    public boolean executeLogin(CommandSender sender, String[] strings) {
        Player player = (Player) sender;
        if (strings.length != 1) {
            player.sendMessage("§cUSage: /login password");
            return false;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        String passwordFromDB = "";
        String password = strings[0];
        try {
            connection = hikari.getConnection();
            String table = plugin.
                    getConfigManager().
                    getConfig()
                    .getString("auth.table");

            statement = connection.prepareStatement("SELECT password FROM " + table + " WHERE username = ?");
            statement.setString(1, player.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                passwordFromDB = set.getString("password");
            } else {
                player.sendMessage("§2You don't have account! Register using " + "/register password email");
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
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


        boolean isGood = false;
        try {
            isGood = plugin.getAuth().comparePasswordWithHash(password, passwordFromDB);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        if (isGood) {
            try {
                connection = hikari.getConnection();
                String table = plugin.getConfigManager().getConfig().getString("auth.table");

                statement = connection.prepareStatement("UPDATE " + table + " SET ip=?,lastlogin=? WHERE username = ?");

                statement.setString(1, player.getAddress());
                statement.setLong(2, (System.currentTimeMillis() / 1000L));
                statement.setString(3, player.getName());

                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
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


            plugin.getAuth().getAuthenticated().add(player.getName());
            player.sendMessage("§2Logged in!");
            return true;
        } else {
            player.sendMessage("§cYou typed wrong password!");
            return false;
        }
    }

    public void executeRegister(CommandSender sender, String[] strings) {
        Player player = (Player) sender;
        if (strings.length != 2) {
            player.sendMessage("Usage: /register yourpassword twojemail");
            player.sendMessage("Example usage: /register mypassword mymail@gmail.com");
            return;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = hikari.getConnection();
            String table = plugin.getConfigManager().getConfig().getString("auth.table");

            statement = connection.prepareStatement("SELECT id FROM " + table + " WHERE username = ?");
            statement.setString(1, player.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                player.sendMessage("You already have an account! Log in using " + "/login password");
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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

        String password = strings[0];
        if (player.getName().length() < 3) {
            player.sendMessage("Too short username! You need at least 3 letters username");
            return;
        }
        Pattern pattern;
        Matcher matcher;
        String USERNAME_PATTERN = "[a-zA-Z0-9_]*";
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(player.getName());
        if (!matcher.matches()) {
            player.sendMessage("Illegal characters! Fix your username!");
            return;
        }
        if (player.getName().length() < 3) {
            player.sendMessage("Too short username! You need at least 3 letters username");
            return;
        }
        if (password.length() < 6) {
            player.sendMessage("Your password needs to be at least 6 letters long");
            return;
        }

        String email = strings[1];
        if (email.length() < 6 || !email.contains("@") || !email.contains(".")) {
            player.sendMessage("You typed incorrect email! You'll need it to recover your account!");
            return;
        }
        String hash = "";
        try {
            hash = plugin.getAuth().getHash(password);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        try {
            connection = hikari.getConnection();
            String table = plugin.getConfigManager().getConfig().getString("auth.table");
            statement = connection.prepareStatement("INSERT INTO " + table + "("
                    + "id, username, password, ip, lastlogin, x, y, z, world, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ResultSet set = statement.getGeneratedKeys();
            int i = 0;
            if (set.next()) {
                i = set.getInt(1);
            }
            statement.setInt(1, i);
            statement.setString(2, player.getName());
            statement.setString(3, hash);
            statement.setString(4, player.getAddress());
            statement.setLong(5, (System.currentTimeMillis() / 1000L));
            statement.setDouble(6, 0);
            statement.setDouble(7, 0);
            statement.setDouble(8, 0);
            statement.setString(9, "world");
            statement.setString(10, email);

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
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

            player.sendMessage("You're now registered! Log in to play!");
        }

    }

    public void executeChangePassword(CommandSender sender, String[] strings) {
        Player player = (Player) sender;


        if (strings.length != 2) {
            player.sendMessage("§cUsage: /changepassword oldpass newpass");
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        String passwordFromDB = "";
        String oldPassword = strings[0];
        String newPassword = strings[1];
        try {
            connection = hikari.getConnection();
            String table = plugin.getConfigManager().getConfig().getString("auth.table");
            statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE username = ?");
            statement.setString(1, player.getName());
            ResultSet set = statement.executeQuery();
            if (!set.next()) {
                player.sendMessage("§2You don't have account! Register using " + "/register password email");
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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

        try {
            connection = hikari.getConnection();
            String table = plugin.getConfigManager().getConfig().getString("auth.table");
            statement = connection.prepareStatement("SELECT password FROM " + table + " WHERE username = ?");
            statement.setString(1, player.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                passwordFromDB = set.getString("password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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

        boolean isGood = false;
        try {
            isGood = plugin.getAuth().comparePasswordWithHash(oldPassword, passwordFromDB);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        if (isGood) {
            if (newPassword.length() < 6) {
                player.sendMessage("Your password needs to be at least 6 letters long");
                return;
            } else {
                String newHash = "";
                try {
                    newHash = plugin.getAuth().getHash(newPassword);
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }
                try {
                    connection = hikari.getConnection();
                    String table = plugin.getConfigManager().getConfig().getString("auth.table");
                    statement = connection.prepareStatement("UPDATE " + table + " SET password = ? WHERE username = ?");
                    statement.setString(1, newHash);
                    statement.setString(2, player.getName());
                    statement.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
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
            player.sendMessage("§2Password changed! You can now log in!");
        } else {
            player.sendMessage("§cOld password incorrect!");
        }
    }


}
