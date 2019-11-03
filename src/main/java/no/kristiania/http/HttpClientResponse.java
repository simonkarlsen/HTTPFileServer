package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;

public class HttpClientResponse {
    private String result;

    public HttpClientResponse(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            result.append((char)c);
        }
        System.out.println(result);
        this.result = result.toString();
    }

    public int getStatusCode() {
        String statusLine = result.split("\r?\n")[0];
        return Integer.parseInt(statusLine.split(" ")[1]);
    }

    public String getHeader(String name) {
        for (String headerField : result.split("\r?\n")) {
            int colonPos = headerField.indexOf(':');
            if (colonPos >= 0) {
                String fieldName = headerField.substring(0, colonPos);
                String fieldValue = headerField.substring(colonPos + 1).trim();
                if (fieldName.equals(name)) {
                    return fieldValue;
                }
            }
        }
        return null;
    }

    public String getBody() {
        String[] lines = result.split("\r?\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.isBlank()) {
                return lines[i+1];
            }
        }
        return null;
    }

}
