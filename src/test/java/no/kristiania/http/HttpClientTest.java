package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    @Test
    void shouldReturnResponseCode() throws IOException {
        HttpClient httpClient = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Hello%20world!");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void shouldReturnFailureCode() throws IOException{
        HttpClient httpClient = new HttpClient("urlecho.appspot.com", 80, "/echo?status=401");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals(401, response.getStatusCode());
    }

    @Test
    void shouldReturnResponseHeader() throws IOException {
        HttpClient httpClient = new HttpClient("urlecho.appspot.com", 80,
                "/echo?status=302&Location=http://www.example.com");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals("http://www.example.com", response.getHeader("Location"));
    }

    @Test
    void shouldReturnContentLength() throws IOException {
        HttpClient httpClient = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Hello");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals("5", response.getHeader("Content-Length"));
    }

    @Test
    void shouldReturnContentBody() throws IOException{
        HttpClient httpClient = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Hello");
        HttpClientResponse response = httpClient.executeRequest();
        assertEquals("Hello", response.getBody());
    }
}