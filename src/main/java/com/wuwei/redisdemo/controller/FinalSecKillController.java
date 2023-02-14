package com.wuwei.redisdemo.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;

@RestController
@RequestMapping("/redisTest")
public class FinalSecKillController {
    @Autowired
    private RedisTemplate redisTemplate;

    String secKillScript = "local userid=KEYS[1];\r\n" +
            "local productid=KEYS[2];\r\n" +
            "local kckey='sku:'..productid..\":kc\";\r\n" +
            "local usersKey='sku:'..productid..\":user\";\r\n" +
            "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" +
            "if tonumber(userExists)==1 then \r\n" +
            "   return 2;\r\n" +
            "end\r\n" +
            "local num= redis.call(\"get\" ,kckey);\r\n" +
            "if tonumber(num)<=0 then \r\n" +
            "   return 0;\r\n" +
            "else \r\n" +
            "   redis.call(\"decr\",kckey);\r\n" +
            "   redis.call(\"sadd\",usersKey,userid);\r\n" +
            "end\r\n" +
            "return 1";


    @PostMapping("/secKill")
    public boolean testRedis(String uid, String productid) {
        /*模拟不同用户*/
        Random random = new Random();
        int i = random.nextInt(10000);
        uid = "user" + i;
        //1 uid和prodid非空判断
        if (uid == null || productid == null) {
            return false;
        }
        //调用lua脚本并执行
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);//返回类型是Long
        redisScript.setScriptText(secKillScript);
        Object execute = redisTemplate.execute(redisScript, Arrays.asList(uid, productid), "");


        String reString = String.valueOf(execute);
        if ("0".equals(reString)) {
            System.err.println("已抢空！！");
            return false;
        } else if ("1".equals(reString)) {
            System.out.println("抢购成功！！！！");
            return true;
        } else if ("2".equals(reString)) {
            System.err.println("该用户已抢过！！");
            return false;
        } else {
            System.err.println("抢购异常！！");
            return false;
        }

    }
}
