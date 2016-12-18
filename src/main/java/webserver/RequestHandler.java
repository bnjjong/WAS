package webserver;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.BooleanSupplier;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

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

            String method = httpRequest.getRequestMethod();
            String path = httpRequest.getRequestPath();

            if ("GET".equalsIgnoreCase(method)) {
                if (path.equalsIgnoreCase("/index.html")) {
                    httpResponse.foward(path.trim(), httpRequest.getRequestHeader("Accept"));
                } else if ((path.equalsIgnoreCase("/user/list.html"))) {
                    Map<String, String> cookie = HttpRequestUtils.parseCookies(httpRequest.getRequestHeader("Cookie"));

                    boolean isLogined = Boolean.parseBoolean(cookie.get("logined"));

                    if (!isLogined) {
                        path = "/index.html";
                    }

                    httpResponse.foward(path.trim(), httpRequest.getRequestHeader("Accept"));

                } else {
                    httpResponse.foward(path.trim(), httpRequest.getRequestHeader("Accept"));
                }
            }

            if ("POST".equalsIgnoreCase(method)) {
                String location = "http://localhost:8080/index.html";

                if (path.equalsIgnoreCase("/user/create")) {
                    String userId = httpRequest.getRequestParam("userId");
                    String password = httpRequest.getRequestParam("password");
                    String name = httpRequest.getRequestParam("name");
                    String email = httpRequest.getRequestParam("email");

                    User newUser = new User(userId, password, name, email);
                    newUser.saveUser();

                    httpResponse.sendRedirect(location, false);

                }

                if (path.equalsIgnoreCase("/user/login")) {
                    boolean isLogined = false;
                    location = "http://localhost:8080/user/login_failed.html";
                    String userId = httpRequest.getRequestParam("userId");
                    String password = httpRequest.getRequestParam("password");

                    User loginUser = DataBase.findUserById(userId);

                    if (loginUser != null && loginUser.matchPassword(password)) {
                        isLogined = true;
                        location = "http://localhost:8080/index.html";

                    }

                    httpResponse.sendRedirect(location, isLogined);
                }

            }

            /*if ("POST".equals(method)) {
                int contentLength = Integer.parseInt(httpRequest.getRequestHeader("Content-Length"));

                String data = util.IOUtils.readData(br, contentLength);
                Map<String, String> map = HttpRequestUtils.parseQueryString(data);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));

                String location = "http://localhost:8080/index.html";
                if (url.contains("/user/create")) {
                    DataBase.addUser(user);

                    log.debug("CREATE - {}", DataBase.findAll());

                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, location, httpRequest.getRequestHeader("Accept"));
                }

                if (url.contains("/user/login")) {
                    User loginUser = DataBase.findUserById(user.getUserId());

                    if (!user.matchPassword(loginUser)) {
                        location = "http://localhost:8080/user/login_failed.html";

                        log.debug("LOGIN - {}", loginUser);

                        DataOutputStream dos = new DataOutputStream(out);
                        response302LoginSuccessHeader(dos, location, httpRequest.getRequestHeader("Accept"));
                    } else {
                        log.debug("LOGIN - {}", loginUser);

                        DataOutputStream dos = new DataOutputStream(out);
                        response302LoginSuccessHeader(dos, location, httpRequest.getRequestHeader("Accept"));
                    }
                }

            }*/

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
