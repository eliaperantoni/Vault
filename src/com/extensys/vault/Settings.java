package com.extensys.vault;

import java.util.ArrayList;
import java.util.List;

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


    public enum Fields {
        AUTOSAVE, DEBUG;
    }

    String settingsProvider(Fields settingToProvide) {
        switch (settingToProvide) {
            case AUTOSAVE:
                return "false";
            case DEBUG:
                return "true";
        }
        return "NULL";
    }
}
