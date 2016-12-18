package webserver;

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

import db.DataBase;
import model.User;
import util.HttpHeaderUtil;
import util.HttpRequestUtils;

import javax.xml.crypto.Data;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            byte[] body;

            // getUrl
            String line = br.readLine();
            log.debug("request line : {}", line);
            if(line == null) {
                return;
            }

            String url = HttpRequestUtils.getUrl(line, " ");
            String method = HttpRequestUtils.getMethod(line, " ");

            String requestPath = url;

            // setHeader
            HttpHeaderUtil.setHeader(br);

            if ("GET".equals(method)) {
                body = Files.readAllBytes(new File("./webapp" + requestPath).toPath());
                
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length, HttpHeaderUtil.HEADER_DATA.get("Accept"));
                responseBody(dos, body);
            }

            if ("POST".equals(method)) {
                int contentLength = Integer.parseInt(HttpHeaderUtil.HEADER_DATA.get("Content-Length"));

                String data = util.IOUtils.readData(br, contentLength);
                Map<String, String> map = HttpRequestUtils.parseQueryString(data);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));

                String location = "http://localhost:8080/index.html";
                if (requestPath.contains("/user/create")) {
                    DataBase.addUser(user);

                    log.debug("CREATE - {}", DataBase.findAll());

                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, location, HttpHeaderUtil.HEADER_DATA.get("Accept"));
                }

                if (requestPath.contains("/user/login")) {
                    User loginUser = DataBase.findUserById(user.getUserId());

                    if (!user.matchPassword(loginUser)) {
                        location = "http://localhost:8080/user/login_failed.html";

                        log.debug("LOGIN - {}", loginUser);

                        DataOutputStream dos = new DataOutputStream(out);
                        response302LoginSuccessHeader(dos, location, HttpHeaderUtil.HEADER_DATA.get("Accept"));
                    } else {
                        log.debug("LOGIN - {}", loginUser);

                        DataOutputStream dos = new DataOutputStream(out);
                        response302LoginSuccessHeader(dos, location, HttpHeaderUtil.HEADER_DATA.get("Accept"));
                    }
                }

            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " +  contentType+ ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: " +  contentType+ ";charset=utf-8\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos, String location, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: " +  contentType+ ";charset=utf-8\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
