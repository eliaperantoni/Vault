package com.extensys.vault;

import com.extensys.vault.client.Functions;
import com.extensys.vault.crypto.CryptoUtils;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by extensys on 27/04/2017.
 */
public class ClientThreadTest {
    @Test
    public void connect() {
        Socket sock = Functions.connect();
        assertNotNull(sock);

    }


    @Test
    public void requestRandomKey() {
        Socket sock = Functions.connect();
        String key = Functions.requestKey(sock);
        Functions.close(sock);
        System.out.println(key);
        assert key.length() == 16;
    }

    @Test
    public void sendFile() {


    }
}