package com.server.comms;

import com.server.comms.packets.HelloPacket;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

public class WebsocketContainer {
    protected static ObjectMapper json;
    private final Server server;

    public WebsocketContainer(String hostname, int port) {
        server = new Server(hostname, port, "/", null, WebsocketContainer.Endpoint.class);
        json = new ObjectMapper();
    }

    public void start() {
        try {
            server.start();
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        server.stop();
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
