package no.kristiania.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {

    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpServer(0);
        server.startServer();

    }

    @Test
    void shouldGet200StatusCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo");
        HttpClientResponse response = client.executeRequest();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void shouldRequestStatusCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=401");
        HttpClientResponse response = client.executeRequest();
        assertEquals(401, response.getStatusCode());
    }

    @Test
    void shouldReturnResponseHeader() throws IOException {
        HttpClient httpClient = new HttpClient("localhost", server.getPort(), "/echo?status=302&Location=http://www.example.com");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals(302, response.getStatusCode());
        assertEquals("http://www.example.com", response.getHeader("Location"));
    }

    @Test
    void shouldReturnContent() throws IOException {
        HttpClient httpClient = new HttpClient("localhost", server.getPort(), "/echo?body=Hello");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals("5", response.getHeader("Content-Length"));
        assertEquals("Hello", response.getBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        Files.writeString(Paths.get("target/mytestfile.txt"), "Hello Kristiania");
        server.setFileLocation("target");
        HttpClient httpClient = new HttpClient("localhost", server.getPort(), "/mytestfile.txt");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals("Hello Kristiania", response.getBody());
    }
}