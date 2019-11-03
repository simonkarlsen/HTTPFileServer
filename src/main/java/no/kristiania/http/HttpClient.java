package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private String host;
    private int port;
    private String requestTarget;

    public HttpClient(String host, int port, String requestTarget) {
        this.host = host;
        this.port = port;
        this.requestTarget = requestTarget;
    }

    public static void main(String[] args) throws IOException {
        new HttpClient("urlecho.appspot.com", 80, "/echo?=body=Hello%20world!").executeRequest();
    }

    public HttpClientResponse executeRequest() throws IOException {
        Socket socket = new Socket(host, port);

        socket.getOutputStream().write(("GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: urlecho.appspot.com\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes());

        socket.getOutputStream().flush();

        return new HttpClientResponse(socket.getInputStream());
    }

}
