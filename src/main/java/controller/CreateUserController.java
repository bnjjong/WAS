package controller;

import http.HttpRequest;
import http.HttpResponse;
import model.User;

/**
 * Created by 강홍구 on 2016-12-18.
 */
public class CreateUserController extends AbstractController {

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String location = "http://localhost:8080/index.html";

        String userId = httpRequest.getRequestParam("userId");
        String password = httpRequest.getRequestParam("password");
        String name = httpRequest.getRequestParam("name");
        String email = httpRequest.getRequestParam("email");

        User newUser = new User(userId, password, name, email);
        newUser.saveUser();

        httpResponse.sendRedirect(location, false);
    }
}
