package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 강홍구 on 2016-12-18.
 */
public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private DataOutputStream dataOutputStream ;
    private Map<String, String> responseHeader = new HashMap<>();;

    public HttpResponse (OutputStream outputStream) {
        dataOutputStream = new DataOutputStream(outputStream);
    }

    public void forward (String url, String contentType) throws IOException {
        byte[] body =  Files.readAllBytes(new File("./webapp" + url).toPath());

        this.addHeader("Content-Length", body.length + "");
        this.addHeader("Content-Type", contentType);
        this.response200Header();
        this.responseBody(body);
    }

    public void sendRedirect (String url, boolean isLogined) {
        this.addHeader("Location", url);
        if (isLogined) {
            this.addHeader("Set-Cookie", "true");
        }
        this.response302Header();
    }

    private void addHeader (String key, String value) {
        responseHeader.put(key.trim(), value.trim());
    }

    private void processHeader () throws IOException {
        for (String key : responseHeader.keySet()) {
            dataOutputStream.writeBytes(key + ": " + responseHeader.get(key) + "\r\n");
            log.debug("Process ResponseHeader - {} : {}", key + " ", responseHeader.get(key));
        }
    }

    private void response200Header() {
        try {
            dataOutputStream.writeBytes("HTTP/1.1 200 OK \r\n");
            this.processHeader();
            dataOutputStream.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header() {
        try {
            dataOutputStream.writeBytes("HTTP/1.1 302 Found \r\n");
            this.processHeader();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dataOutputStream.write(body, 0, body.length);
            dataOutputStream.writeBytes("\r\n");
            dataOutputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
