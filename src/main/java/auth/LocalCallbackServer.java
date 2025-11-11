package auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Local HTTP server that handles OAuth callbacks from Steam.
 * This server runs temporarily on localhost to receive the authentication response.
 */
public class LocalCallbackServer {
    private HttpServer server;
    private CompletableFuture<Map<String, String>> callbackFuture;
    private final int port;

    /**
     * Creates a new LocalCallbackServer on the specified port.
     *
     * @param port The port to listen on (e.g., 8080)
     */
    public LocalCallbackServer(int port) {
        this.port = port;
    }

    /**
     * Starts the HTTP server and begins listening for callbacks.
     *
     * @return A CompletableFuture that will contain the callback parameters
     * @throws IOException if the server cannot be started
     */
    public CompletableFuture<Map<String, String>> start() throws IOException {
        this.callbackFuture = new CompletableFuture<>();

        // Create HTTP server on localhost
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());

        // Create context for the callback endpoint
        server.createContext("/callback", new CallbackHandler());

        // Start the server
        server.start();
        System.out.println("Callback server started on http://localhost:" + port + "/callback");

        return callbackFuture;
    }

    /**
     * Stops the HTTP server.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Callback server stopped");
        }
    }

    /**
     * Gets the callback URL that Steam should redirect to.
     *
     * @return The callback URL
     */
    public String getCallbackUrl() {
        return "http://localhost:" + port + "/callback";
    }

    /**
     * HTTP handler for the callback endpoint.
     */
    private class CallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Parse query parameters from the request
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQueryString(query);

                // Send success response to browser
                String response = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Steam Authentication</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                height: 100vh;
                                margin: 0;
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            }
                            .container {
                                text-align: center;
                                background: white;
                                padding: 40px;
                                border-radius: 10px;
                                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                            }
                            h1 {
                                color: #2c3e50;
                                margin-bottom: 20px;
                            }
                            p {
                                color: #7f8c8d;
                                margin-bottom: 20px;
                            }
                            .checkmark {
                                font-size: 48px;
                                color: #27ae60;
                                margin-bottom: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="checkmark">✓</div>
                            <h1>Authentication Successful!</h1>
                            <p>You have been successfully authenticated with Steam.</p>
                            <p>You can now close this window and return to the application.</p>
                        </div>
                    </body>
                    </html>
                    """;

                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

                // Complete the future with the received parameters
                callbackFuture.complete(params);

            } catch (Exception e) {
                // Send error response
                String errorResponse = "Authentication failed: " + e.getMessage();
                exchange.sendResponseHeaders(500, errorResponse.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorResponse.getBytes());
                }

                callbackFuture.completeExceptionally(e);
            }
        }

        /**
         * Parses a URL query string into a map of parameters.
         *
         * @param query The query string to parse
         * @return A map of parameter names to values
         */
        private Map<String, String> parseQueryString(String query) {
            Map<String, String> params = new HashMap<>();

            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        params.put(key, value);
                    }
                }
            }

            return params;
        }
    }
}