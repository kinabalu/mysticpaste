package com.mysticcoders;

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

/**
 * Runs MysticPaste in an embedded Jetty instance for local development.
 */
public class Start {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("jetty.port", DEFAULT_PORT);

        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setIdleTimeout(1000L * 60 * 60);
        connector.setPort(port);
        server.setConnectors(new ServerConnector[]{connector});

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar("web/src/main/webapp");
        webapp.setParentLoaderPriority(true);

        server.setHandler(webapp);

        System.out.println(">>> STARTING EMBEDDED JETTY SERVER on port " + port + ", PRESS CTRL-C TO STOP");
        server.start();
        server.join();
    }
}
