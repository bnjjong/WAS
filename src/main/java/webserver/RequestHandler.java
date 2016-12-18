package webserver;

import controller.Controller;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private static final String DEFAULT_PATH = "http://localhost:8080/index.html";

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            Controller controller = RequestMapping.getController(httpRequest.getRequestPath());

            if (controller == null) {
                String path = getDefaultPath(httpRequest.getRequestPath());
                httpResponse.forward(path, httpRequest.getRequestHeader("Accept"));
            } else {
                controller.service(httpRequest, httpResponse);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
