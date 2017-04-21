package com.extensys.vault;

/**
 * Created by extensys on 21/04/2017.
 */
public class Settings {
    private static Settings ourInstance = new Settings();

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }
}
