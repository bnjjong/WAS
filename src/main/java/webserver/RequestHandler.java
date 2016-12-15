package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

            String line = br.readLine();
            log.debug("request line : {}", line);
            if(line == null) {
                return;
            }

            byte[] body = new byte[0];
            String method = HttpRequestUtils.getMethod(line, " ");
            String url = HttpRequestUtils.getUrl(line, " ");

            String requestPath = url;
            String contentType = "empty";
            // header 먼저 collection


            if ("GET".equals(method)) {
                body = Files.readAllBytes(new File("./webapp" + requestPath).toPath());
                while(!"".equals(line)) {
                    line = br.readLine();
                    HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                    contentType = HttpRequestUtils.getContentType(pair);
                    if (!"empty".equals(contentType)) {
                        break;
                    }
                    log.debug("request line : {}", line);
                }

                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length, contentType);
                responseBody(dos, body);
            }

            if ("POST".equals(method)) {
                int length = -1;
                while(!"".equals(line)) {
                    line = br.readLine();
                    HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                    length = HttpRequestUtils.getContentLength(pair);
                    if (length != -1) {
                        while(!"".equals(line)) {
                            line = br.readLine();
                        }
                    }
                    log.debug("request line : {}", line);
                }

                String data = util.IOUtils.readData(br, length);
                Map<String, String> map = HttpRequestUtils.parseQueryString(data);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                DataBase.addUser(user);
                log.debug("{}", DataBase.findAll());

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos);
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

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + "http://localhost:8080/index.html");
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
