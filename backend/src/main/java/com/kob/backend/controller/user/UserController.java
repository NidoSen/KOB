package com.kob.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserMapper userMapper;

    @GetMapping ("/user/all/")
    public List<User> getAll() {
        return userMapper.selectList(null);
    }

    @GetMapping ("/user/{userId}/")
    public User getuser(@PathVariable int userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }

    @GetMapping ("/user/add/{userId}/{username}/{password}/")
    public String addUser(
            @PathVariable int userId,
            @PathVariable String username,
            @PathVariable String password) {
        if (password.length() < 6) {
            return "密码太短";
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encondedPassword = passwordEncoder.encode(password);
        User user = new User(userId, username, encondedPassword);
        userMapper.insert(user);
        return "Add User Successfully!";
    }

    @GetMapping ("/user/delete/{userId}/")
    public String deleteUser (@PathVariable int userId) {
        userMapper.deleteById(userId);
        return "Delete User Successfully!";
    }
}
