package com.blog.service;

import com.blog.dao.SessionDAO;
import com.blog.dao.mongoimpl.SessionDAOMongo;
import com.blog.dao.mongoimpl.UserDAOMongo;
import com.blog.db.DBManager;
import com.blog.entity.User;
import com.blog.exception.InvalidUserDataException;
import com.blog.util.PasswordUtil;

import static com.blog.constant.ResponseMessage.*;

/**
 * @author Timur Berezhnoi
 */
public class LoginService {

    private final SessionDAO sessionDAO = new SessionDAOMongo(DBManager.INSTANCE.getDataBase());
    private final UserDAOMongo userDAO = new UserDAOMongo(DBManager.INSTANCE.getDataBase());

    public String login(String email, String password) throws InvalidUserDataException {
        validateLogin(email, password);
        return sessionDAO.createSession(email);
    }

    private void validateLogin(String email, String password) throws InvalidUserDataException {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        String passwordPattern = "^.{6,15}$";

        if(email == null || email.length() > 25 || !email.matches(emailPattern)) {
            throw new InvalidUserDataException(INVALID_USER_EMAIL.getMessage());
        }

        if (password == null || !password.matches(passwordPattern)) {
            throw new InvalidUserDataException(INVALID_USER_PASSWORD.getMessage());
        }

        User user = userDAO.getUserByEmail(email);
        if(user == null || !PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new InvalidUserDataException(LOGIN_FAIL.getMessage());
        }
    }
}