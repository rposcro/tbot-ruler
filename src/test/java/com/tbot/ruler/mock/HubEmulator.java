package com.tbot.ruler.mock;

public class HubEmulator {
    public static void main(String[] args) throws Exception {

        long sleepTime = 10_000;

        if (args.length > 0) {
            sleepTime = Long.parseLong(args[0]);
        }

        while(true) {
            Thread.sleep(sleepTime);
            System.out.println("01 08 00 00 01 01 03 00 06 00 01 EB FF");
        }
    }
}
