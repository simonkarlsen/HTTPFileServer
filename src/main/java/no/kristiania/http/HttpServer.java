package no.kristiania.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private int port;
    private ServerSocket serverSocket;
    private String fileLocation;

    public HttpServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);

    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(8080);
        httpServer.setFileLocation("src/main/resources");
        httpServer.startServer();

    }

    public void startServer() throws IOException {
        new Thread(() -> run()).start();

    }

    private void run() {
        try (Socket socket = serverSocket.accept()) {

            StringBuilder line = new StringBuilder();
            String requestLine = null;
            int c;
            while ((c = socket.getInputStream().read()) != -1) {
                if (c == '\r') {
                    c = socket.getInputStream().read();
                    if (requestLine == null) {
                        requestLine = line.toString();
                    }
                    System.out.print(line);
                    if (line.toString().isBlank()) {
                        break;
                    }
                    line = new StringBuilder();
                }
                line.append((char)c);
            }

            String requestTarget = requestLine.split(" ")[1];

            int questionPos = requestTarget.indexOf('?');
            String requestPath = questionPos == -1 ? requestTarget : requestTarget.substring(0, questionPos);
            if (!requestPath.equals("/echo")) {
                File file = new File(fileLocation + requestPath);
                socket.getOutputStream().write(("HTTP/1.1 200 OK\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());

                new FileInputStream(file).transferTo(socket.getOutputStream());

                return;
            }

            Map<String, String> queryParameters = parseQueryParameters(requestTarget);

            String status = queryParameters.getOrDefault("status", "200");
            String location = queryParameters.getOrDefault("Location", null);
            String body = queryParameters.getOrDefault("body", "Hello world!");


            socket.getOutputStream().write(("HTTP/1.1 " + status + " OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Connection: close\r\n" +
                    (location != null ? "Location: " + location + "\r\n" : "") +
                    "\r\n" +
                    body).getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> parseQueryParameters(String requestTarget) {
        Map<String, String> queryParameters = new HashMap<>();
        int questionPos = requestTarget.indexOf('?');
        if (questionPos > 0) {
            String query = requestTarget.substring(questionPos + 1);
            for (String parameter : query.split("&")) {
                int equalsPos = parameter.indexOf('=');
                String parameterName = parameter.substring(0, equalsPos);
                String parameterValue = parameter.substring(equalsPos + 1);
                queryParameters.put(parameterName, parameterValue);
            }
        }
        return queryParameters;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
