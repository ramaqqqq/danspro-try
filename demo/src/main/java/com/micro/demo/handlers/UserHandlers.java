package com.micro.demo.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.micro.demo.helpers.Helpers;
import com.micro.demo.middleware.Middleware;
import com.micro.demo.models.dao.UserDao;
import com.micro.demo.models.entity.UserEntity;

@Service
public class UserHandlers {

    @Autowired
    private UserDao userDao;

    public Map<String, Object> addUser(UserEntity h) {

        String hashedPassword = Helpers.hashPassword(h.getPassword());
        h.setPassword(hashedPassword);

        UserEntity user = new UserEntity();
        user.setUsername(h.getUsername());
        user.setEmail(h.getEmail());
        user.setPassword(hashedPassword);

        UserEntity savedUser = userDao.save(user);

        String token = Middleware.createToken(
            String.valueOf(savedUser.getId()),
            savedUser.getUsername(),
            savedUser.getEmail()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("user", savedUser);
        response.put("accessToken", token);

        return response;
    }

    public Map<String, Object> loginUser(UserEntity user) {
        UserEntity dbUser = userDao.findFirstByEmail(user.getEmail());

        if (dbUser == null || !Helpers.verifyPassword(dbUser.getPassword(), user.getPassword())) {
            Helpers.log("error", "In Server: bad request");
            throw new RuntimeException("Invalid email or password");
        }

        String token = Middleware.createToken(
                String.valueOf(dbUser.getId()),
                dbUser.getUsername(),
                dbUser.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("id", dbUser.getId());
        response.put("username", dbUser.getUsername());
        response.put("email", dbUser.getEmail());
        response.put("accessToken", token);

        return response;
    }

}
