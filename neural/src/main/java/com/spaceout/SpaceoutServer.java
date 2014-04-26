package com.spaceout;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import java.util.concurrent.Executors;

public class SpaceoutServer implements HttpHandler {
    private HttpServer server;
    private HttpContext context;

    private SpaceoutServer() throws IOException {
        InetSocketAddress address = new InetSocketAddress("0.0.0.0", 8080);
        this.server = HttpServer.create(address, 0);
        this.server.setExecutor(Executors.newFixedThreadPool(8));
        this.context = this.server.createContext("/neural", this);
    }

    public final void handle(HttpExchange exchange) throws IOException {
        // Request method
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("get")) {
            System.out.println("GET requested");
        } else if (requestMethod.equalsIgnoreCase("post")) {
            System.out.println("POST requested");
        }

        // Set the response headers
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "application/json");

        // Response is OK (HTTP 200)
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

        // Set up the input and output streams
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody()));

        // Read the input, and have the implementing child handle the output
        String messageString = reader.readLine();

        String remoteHostAddress = exchange.getRemoteAddress().getAddress().getHostAddress();
        this.handleExchange(remoteHostAddress, messageString, writer);
        writer.flush();
        exchange.close();
    }

    private final void handleExchange(String remoteHostAddress, String messageString, BufferedWriter responseWriter) {
        // TODO -- implement translating message string into a request for last
        // mental state
    }

    public static void main(String[] args) {
        try {
            new SpaceoutServer();
        } catch (IOException ex) {
            System.err.println("Failed to startup HTTP server: exiting!");
            System.exit(1);
        }

    }
}
