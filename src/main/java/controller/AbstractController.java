package controller;

import http.HttpRequest;
import http.HttpResponse;

/**
 * Created by 강홍구 on 2016-12-18.
 */
public class AbstractController implements Controller{
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        String method = httpRequest.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            doGet(httpRequest, httpResponse);
        } else if("POST".equalsIgnoreCase(method)) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doGet (HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    protected void doPost (HttpRequest httpRequest, HttpResponse httpResponse) {

    }
}
