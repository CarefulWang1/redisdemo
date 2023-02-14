package com.wuwei.redisdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wuwei.redisdemo.mapper.UserMapper;
import com.wuwei.redisdemo.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Controller
public class RedisController {    //RedisTemplate
    //  两个类型
    @Autowired
    private  StringRedisTemplate  stringRedisTemplate;  // 操作k-v全是字符串


    @Autowired
    private RedisTemplate <String,Object> redisTemplate;// 操作对象的

//    @Autowired
//    private RedisTemplate<String, User> userRedisTemplate;// 只改变了json序列化器
                //  user     userRedisTemplate
    //  role    dept  menu    20+   配置一个通用的   po

   @Autowired
    private UserMapper  userMapper;

    @RequestMapping("index")
    @ResponseBody
   public  String index(){
        String key = stringRedisTemplate.opsForValue().get("aaaaa45454");
        // 报错
        System.out.println(key);   // java操作redis取 key  若key不存在 则返回null
        return "index";
   }

    @RequestMapping("getkey")
    @ResponseBody
    public  String index1(){
        String value = stringRedisTemplate.opsForValue().get("aaa");

        return value;
    }

    @RequestMapping("setkey")
    @ResponseBody
    public  String index2(){
         stringRedisTemplate.opsForValue().set("aaa","wxx哈哈",15, TimeUnit.SECONDS);
        // 报错
        stringRedisTemplate.opsForList(); //List

        return " set success!";
    }
    @RequestMapping("delkey")
    @ResponseBody
    public  String index3(){
        stringRedisTemplate.delete("aaa");
        // 报错

        return " del success!";
    }

    @RequestMapping("setKeyObject")
    @ResponseBody
    public  String index4(){
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();//where条件设置器
        queryWrapper.eq("user_id",111);
        User meifei = userMapper.selectOne(queryWrapper);
        System.out.println(meifei);// 从数据库查到的孟飞对象
        redisTemplate.opsForValue().set("wxx",meifei);//孟飞对象存到redis
        // redis底层用到对象IO流    存储对象  该类必须要进行序列化
        // 1. 自动转成json存    2.使用redis提供的序列化器

        return " setKeyObject success!";
    }
    @RequestMapping("setKeyObjectJson")
    @ResponseBody
    public  String index5(){
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();//where条件设置器
        queryWrapper.eq("user_id",111);
        User meifei = userMapper.selectOne(queryWrapper);
        System.out.println(meifei);// 从数据库查到的孟飞对象
        redisTemplate.opsForValue().set("mengfei",meifei);//孟飞对象存到redis
        // 使用redis提供的json序列化器
        return " setKeyObjectJson success!";
    }

    @RequestMapping("phonePage")
    public  String index6(){  // 来到输入手机号 的哪个页面
        return "phonePage";
    }


    @RequestMapping("phoneCode")
    public  String index7(String phone, Model model){  // 输入手机号提交   发送验证码

        // key =15303668637Time
            if(checkNum(phone+"Time")){  //先判断次数     数字验证码phoneCode     phoneTime(一天发次数)
                System.out.println("发送验证码 6个数字  失效时间120s");
                //手机号  发送验证码 6个数字  失效时间120s
                stringRedisTemplate.opsForValue().set(phone+"Code",randomNum(),120,TimeUnit.SECONDS);
                model.addAttribute("phone",phone);
            }
        return "phonePage";
    }


    private String randomNum(){
        StringBuffer  sb=new StringBuffer();
        for (int i = 0; i <6 ; i++) {
            sb.append(new Random().nextInt(10));
        }

        return  sb.toString();
    }

    /**
     *   2.手机号  发送验证码 6个数字  失效时间120s
     *
     *
     *
     *   1.每天3次
     *
     *
     *   验证的过程 :去redis里面取key 对比数字验证码
     *
     */


        private boolean checkNum(String phoneTime){  // 验证次数 key= phoneTime
            //第一次进来需要去redis取次数值
            String count = stringRedisTemplate.opsForValue().get(phoneTime);

            if(count==null){  // null 一次也没发 根本没这个手机号的key
                //
                stringRedisTemplate.opsForValue().set(phoneTime,"1",1,TimeUnit.DAYS);
                return  true;
            }else if(Integer.parseInt(count)<3){  // key 次数增1
                stringRedisTemplate.opsForValue().increment(phoneTime);

                return  true;
            }else if (Integer.parseInt(count)>=3){
                System.out.println("超过3次了！！！！");
                return  false;
            }
            return  false;

        }








    @RequestMapping("checkCode")
    public  String index8(String phone,String code){  // 输入手机号提交   发送验证码
        String s = stringRedisTemplate.opsForValue().get(phone + "Code");

//        List<String> aClass = stringRedisTemplate.opsForList().range("class", 0, -1);

//        Set<String> aClass1 = stringRedisTemplate.opsForSet().members("class");
        if(code.equals(s)){
            System.out.println("验证码输入正确！！");
        }else{
            System.out.println("验证码输入正确！！");
        }   // else if   ==  验证码 过期了 120s    null

        return "phonePage";
    }





}
