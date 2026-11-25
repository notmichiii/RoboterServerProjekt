package com.server;

import com.server.packets.HelloPacket;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

public class Main implements Runnable {
    protected static ObjectMapper json;

    static void main() {
        new Main().run();
    }

    @Override
    public void run() {
        Server server = new Server("localhost", 8888, "/", null, Main.Endpoint.class);
        json = new ObjectMapper();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown.");
            server.stop();
        }));

        try {
            server.start();

            while (true) {
                Thread.onSpinWait();
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (DeploymentException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @ServerEndpoint(value = "/api")
    public static class Endpoint {
        @OnMessage
        public void onMessage(@NotNull Session session, @NotNull String message) throws Exception {
            System.out.println(message);
            session.getAsyncRemote().sendText(json.writeValueAsString(new HelloPacket("Hello World!")));
        }
    }
}