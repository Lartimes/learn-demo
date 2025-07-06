package org.bankai.controller;

import org.bankai.mapper.UserActiveMapper;
import org.bankai.mapper.UserMapper;
import org.bankai.model.User;
import org.bankai.service.SseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserActiveMapper userActiveMapper;

    @Autowired
    private SseManager sseManager;

    @PostMapping("/login")
    public R login(@RequestBody User user) {
        User dest = userMapper.selectByInfo(user.getName(), user.getPassword());
        if (dest != null) {
            user.setUid(dest.getUid());
            return R.ok(user);
        }
        return R.error();
    }

     @GetMapping("/connection/{uid}")
    public SseEmitter connection(@PathVariable("uid") String uid) {
         System.out.println("进来了uid : " + uid);
        return sseManager.getActiveSseEmitter(uid);
    }

    /**
     *
     * 1.手动下线
     * 2.失效之后，http调用该方法 ， 进行通知
     * @param uid
     * @return
     */
    @GetMapping("/expire")
    public R expire(@RequestParam("uid") Integer uid) {
        userActiveMapper.updateExpire(uid , new Date());
//      TODO  下线联系人通知
        return R.ok(true);
    }



    record R(Object data, boolean flag, int status) {
        public static R ok(Object data) {
            return new R(data, true, 200);
        }

        public static R error() {
            return new R(null, false, 500);
        }
    }


}
