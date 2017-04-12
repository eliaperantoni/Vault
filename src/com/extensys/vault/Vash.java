package com.extensys.vault;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by extensys on 07/04/2017.
 */
public class Vash extends Thread {
    static String getVersion() {
        return String.format("VASH v%d.%d.%d", version[0], version[1], version[2]);
    }
    List<String> cmds = Arrays.asList(new String[]{
            "info: System and VASH info breakdown",
            "cmds: List of alla commands"});
    static int[] version = {0, 0, 1};

    @Override
    public void run() {
        super.run();
        System.out.println(String.format("VASH v%d.%d.%d", version[0], version[1], version[2]));
        String inp = "null";
        Scanner scan = new Scanner(System.in);
        while (!inp.equals("exit")) {
            System.out.print("~ ");
            inp = scan.nextLine();
            List<String> inpList = Arrays.asList(inp.split(" "));
            switch (inpList.get(0)) {
                case "info":
                    System.out.println("\n ___      ___  ________   ________   ___  ___     \n" +
                            "|\\  \\    /  /||\\   __  \\ |\\   ____\\ |\\  \\|\\  \\    \n" +
                            "\\ \\  \\  /  / /\\ \\  \\|\\  \\\\ \\  \\___|_\\ \\  \\\\\\  \\   \n" +
                            " \\ \\  \\/  / /  \\ \\   __  \\\\ \\_____  \\\\ \\   __  \\  \n" +
                            "  \\ \\    / /    \\ \\  \\ \\  \\\\|____|\\  \\\\ \\  \\ \\  \\ \n" +
                            "   \\ \\__/ /      \\ \\__\\ \\__\\ ____\\_\\  \\\\ \\__\\ \\__\\\n" +
                            "    \\|__|/        \\|__|\\|__||\\_________\\\\|__|\\|__|\n" +
                            "                            \\|_________|          \n" +
                            "                                                  \n" +
                            "                                                  ");
                    System.out.println("" +
                            "VaultAgainSHell is used by sysadmins to\n" +
                            "interact with the server\n");
                    System.out.println("You are using version " + getVersion());

                    System.out.println("\nSystem info:\n");
                    /* Total number of processors or cores available to the JVM */
                    System.out.println("Available processors (cores): " +
                            Runtime.getRuntime().availableProcessors());

                    /* Total amount of free memory available to the JVM */
                    System.out.println("Free memory (bytes): " +
                            Runtime.getRuntime().freeMemory());

                    /* This will return Long.MAX_VALUE if there is no preset limit */
                    long maxMemory = Runtime.getRuntime().maxMemory();
                    /* Maximum amount of memory the JVM will attempt to use */
                    System.out.println("Maximum memory (bytes): " +
                            (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

                    /* Total memory currently available to the JVM */
                    System.out.println("Total memory available to JVM (bytes): " +
                            Runtime.getRuntime().totalMemory());

                    /* Get a list of all filesystem roots on this system */
                    File[] roots = File.listRoots();
                    System.out.println();
                    /* For each filesystem root, print some info */
                    for (File root : roots) {
                        System.out.println("File system root: " + root.getAbsolutePath());
                        System.out.println("Total space (bytes): " + root.getTotalSpace());
                        System.out.println("Free space (bytes): " + root.getFreeSpace());
                        System.out.println("Usable space (bytes): " + root.getUsableSpace());
                        System.out.println();
                    }
                    break;
                case "cmds":
                    for(String x:cmds){
                        System.out.println(x);
                    }
                    break;
                default:
                    System.out.println("Command not found, use \'cmds\' for a list of available commands");
                    break;
            }
        }
        System.out.println("Closing VASH...");
        this.interrupt();
    }
}
