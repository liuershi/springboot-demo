package cn.infocore.controller;

import cn.infocore.entity.User;
import cn.infocore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 22:57
 * @instructions
 */
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User getById(@PathVariable("id") Integer id){
        User user = userRepository.getOne(id);
        return user;
    }

    @GetMapping("/user")
    public User insertUser(User user) {
        User save = userRepository.save(user);
        return save;
    }
}
