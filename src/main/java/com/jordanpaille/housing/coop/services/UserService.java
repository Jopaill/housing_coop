package com.jordanpaille.housing.coop.services;

import com.jordanpaille.housing.coop.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;

    // constructor
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<UserDetails> getAllUsers() {
        return userDao.getAllUsers();
    }
}
