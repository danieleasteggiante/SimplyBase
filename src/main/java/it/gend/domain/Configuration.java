package it.gend.domain;

/**
 * @author Daniele Asteggiante
 */
public class Configuration {
    String user;
    String password;
    int dbLocked;

    public Configuration(String user, String password, int dbLocked) {
        this.user = user;
        this.password = password;
        this.dbLocked = dbLocked;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getDbLocked() {
        return dbLocked;
    }
}
