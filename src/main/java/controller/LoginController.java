package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

/**
 * Created by 강홍구 on 2016-12-18.
 */
public class LoginController extends AbstractController{

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        boolean isLogined = false;

        String location = "http://localhost:8080/user/login_failed.html";
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
