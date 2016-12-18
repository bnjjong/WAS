package controller;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by 강홍구 on 2016-12-18.
 */
public class ListUserController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(ListUserController.class);

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String path = httpRequest.getRequestPath();
        Map<String, String> cookie = HttpRequestUtils.parseCookies(httpRequest.getRequestHeader("Cookie"));


        if (!isLogined(cookie)) {
            path = "/index.html";
        }

        try {
            httpResponse.forward(path.trim(), httpRequest.getRequestHeader("Accept"));
        } catch (IOException e) {
            log.error("Error - {} ", e.getMessage());
        }
    }

    private boolean isLogined (Map<String, String> cookie) {
        return Boolean.parseBoolean(cookie.get("logined"));
    }
}
