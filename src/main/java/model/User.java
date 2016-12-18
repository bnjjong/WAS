package model;

import db.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);

    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public boolean matchPassword (String password) {
        if (this.password.equals(password)) {
            return true;
        }

        return false;
    }

    public void saveUser () {
        DataBase.addUser(this);
        log.debug("SAVED RESULT - {}", DataBase.findUserById(this.userId));
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
