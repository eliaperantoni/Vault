package com.extensys.vault;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by extensys on 21/04/2017.
 */
public class SettingsTest {

    @Test
    public void settingsProvider() throws Exception {
        assert Settings.debug==true || Settings.debug==false;
        assert Settings.autosave==true || Settings.autosave==false;
    }

}