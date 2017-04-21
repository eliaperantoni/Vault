package com.extensys.vault;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by extensys on 21/04/2017.
 */
public class SettingsTest {
    @Test
    public void getInstance() throws Exception {
       assertNotNull(Settings.getInstance());
    }

    @Test
    public void settingsProvider() throws Exception {
        Settings s = Settings.getInstance();
        for(Settings.Fields x:Settings.Fields.values()){
            assertNotNull(s.settingsProvider(x));
        }
    }

}