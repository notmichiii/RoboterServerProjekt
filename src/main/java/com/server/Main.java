package com.server;

import com.server.comms.WebsocketContainer;
import com.server.data.DatabaseManager;

public class Main {

    static void main() {
        new Main().init();
    }

    public void init() {
        DatabaseManager dm = new DatabaseManager(System.getenv("JOOQ_DB_URL"), System.getenv("JOOQ_DB_USER"), System.getenv("JOOQ_DB_PASSWORD"));
        WebsocketContainer ws = new WebsocketContainer("localhost", 8888);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown.");
            ws.stop();
        }));

        ws.start();

        System.out.println(dm.getBase());
    }
}