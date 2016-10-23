package pl.grzegorz2047.bukkitpe.core.auth;


import pl.grzegorz2047.bukkitpe.core.CoreAuth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author neksi
 */
public class Auth {

    public CoreAuth plugin;
    private final Random random = new Random();
    private ArrayList<String> authenticated = new ArrayList<String>();

    public Auth(CoreAuth plugin) {
        this.plugin = plugin;
    }

    //============Szyfrowania z AuthMe
    private String getSaltedHash(String message, String salt) throws NoSuchAlgorithmException {
        return "$SHA$" + salt + "$" + getSHA256(getSHA256(message) + salt);
    }

    private String createSalt(int length) throws NoSuchAlgorithmException {
        byte[] msg = new byte[40];
        random.nextBytes(msg);

        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        sha1.reset();
        byte[] digest = sha1.digest(msg);
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest)).substring(0, length);
    }

    public String getHash(String password) throws NoSuchAlgorithmException {
        String salt = createSalt(16);
        return getSaltedHash(password, salt);
    }

    private String getSHA256(String message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

        sha256.reset();
        sha256.update(message.getBytes());
        byte[] digest = sha256.digest();

        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1,
                digest));
    }

    public boolean comparePasswordWithHash(String password, String hash) throws NoSuchAlgorithmException {
        if (hash.contains("$")) {
            String[] line = hash.split("\\$");
            if (line.length > 3 && line[1].equals("SHA")) {
                return hash.equals(getSaltedHash(password, line[2]));
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public ArrayList<String> getAuthenticated() {
        return authenticated;
    }
}