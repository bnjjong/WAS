package controller;

import http.HttpRequest;
import http.HttpResponse;

/**
 * Created by 강홍구 on 2016-12-18.
 */
public interface Controller {
    void service (HttpRequest httpRequest, HttpResponse httpResponse);
}
