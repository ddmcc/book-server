package com.soft.book.api;

import com.soft.book.constant.HeaderConstant;
import com.soft.book.entity.Result;
import com.soft.book.entity.bo.User;
import com.soft.book.service.UserService;
import com.soft.book.utils.MapHelper;
import com.soft.book.utils.RandomCodeUtil;
import com.soft.book.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 用户controller
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil<String, String> redisUtil;


    /**
     * 登录
     *
     *  登录成功 生成token，并将token和userId关联，存到redis中，token设置过期时间为30分钟
     *  每次访问重置过期时间，如果超过30分钟没访问，token过期redis会清除掉token，
     *  下次再带着这个token来获取数据则返回未登录
     *
     * @param user      用户
     * @return              ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user) {
        // 参数校验
        if (StringUtils.isBlank(user.getUserName())) {
            return ResponseEntity.badRequest().body(Result.fail("用户名不得为空！"));
        }

        if (StringUtils.isBlank(user.getPassword())) {
            return ResponseEntity.badRequest().body(Result.fail("密码不得为空！"));
        }

        // 先根据用户名查询
        User existUser = userService.getByUserName(user.getUserName());
        // 为空 查无此人或者密码不正确
        if (existUser == null || !user.getPassword().equals(existUser.getPassword())) {
            return ResponseEntity.badRequest().body(Result.fail("用户名或密码错误！"));
        }

        // 登陆成功 生成token
        String token = RandomCodeUtil.generateUuid();
        // 键：token 值：用户id  过期时间30分钟
        redisUtil.set(token, existUser.getUserId().toString(), 30, TimeUnit.MINUTES);

        return ResponseEntity.ok(
                Result.ok("登录成功！", MapHelper.ofLinkedMap("userName", existUser.getUserName(), "token", token))
        );
    }


    /**
     * 注册
     *
     * @param user      账号密码
     * @return              ResponseEntity
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        // 参数校验
        if (StringUtils.isBlank(user.getUserName())) {
            return ResponseEntity.badRequest().body(Result.fail("用户名不得为空！"));
        }

        if (StringUtils.isBlank(user.getPassword())) {
            return ResponseEntity.badRequest().body(Result.fail("密码不得为空！"));
        }

        if (!user.getPassword().equals(user.getConfirm())) {
            return ResponseEntity.badRequest().body(Result.fail("两次输入密码不一致！"));
        }

        // 先根据用户名查询
        User existUser = userService.getByUserName(user.getUserName());
        // 用户已存在
        if (existUser != null) {
            return ResponseEntity.badRequest().body(Result.fail("该用户已存在！"));
        }

        // 注册 插入数据库
        if (userService.register(user.getUserName(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Result.ok("注册成功！请登录", MapHelper.ofLinkedMap("userName", user.getUserName())));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.ok("注册失败！"));
    }


    @GetMapping("/loginStatus")
    public ResponseEntity loginStatus(HttpServletRequest request) {
        // 从header中取token
        String token = request.getHeader(HeaderConstant.HEADER_TOKEN);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.ok(Result.ok(""));
        }

        // 用token获取放在redis中的userId
        String userId = redisUtil.get(token);
        if (StringUtils.isBlank(userId)) {
            return ResponseEntity.ok(Result.ok(""));
        }

        return ResponseEntity.ok(Result.ok("",
                MapHelper.ofLinkedMap("userName", userService.getNameById(Integer.valueOf(userId))))
        );
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        // 从header中取token
        String token = request.getHeader(HeaderConstant.HEADER_TOKEN);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.ok(Result.ok(""));
        }

        redisUtil.set(token, "");
        return ResponseEntity.ok(Result.ok(""));
    }
}
