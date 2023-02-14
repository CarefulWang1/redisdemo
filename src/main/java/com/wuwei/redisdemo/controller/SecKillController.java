package com.wuwei.redisdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Random;

@Controller
public class SecKillController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

        @RequestMapping("seckillPage")
        public  String indexPage(){
            return "seckillPage";
        }
        //  单机版   测试
        //  压力测试  压测  并发  2000   200 测试  bug
    @RequestMapping("doseckill")
    @ResponseBody
    public  String doseckill( String productid ){
            //  正常来说 应该  再取 session  user   随机一个 userid

                //   "sku:1101:kc"   =  10
            String userid="user"+new Random().nextInt(10000);//0 -10000  用户清单里面的值
        // userid="user1314";   重复秒杀
        return String.valueOf(seckill(userid,productid));
    }

        private  boolean  seckill(String  userid,String productid){// 用户  、商品
            // 1.验证非空处理   空 return false;
                  if(!StringUtils.hasLength(userid)||!StringUtils.hasLength(productid)){
                      System.out.println("空值处理");
                      return  false;
                  }
            // 2.拼接redis里面的key
            String  kcKey="sku:"+productid+":kc";   // 不是瞎定义的   sku:最小销售单元  品牌号颜色号尺寸号
            String usersKey ="sku:"+   productid+":user";  //真正用户清单key  sku:1101:user

            // 3.秒杀  { 库存-      添加用户列表}

    // 秒杀 过程    秒杀还未开始    秒杀已经结束    重复秒杀
            String  kc=stringRedisTemplate.opsForValue().get(kcKey);
            //  先去取redis   kcKey    "sku:1101:kc"
            if(kc==null){// null
                System.err.println("秒杀还未开始!");
                return  false;
            }
            if(Integer.parseInt(kc)<=0){// null
                System.err.println("秒杀已经结束!");
                return  false;
            }
            if( stringRedisTemplate.opsForSet().isMember(usersKey,userid)){
                System.err.println("不能重复秒杀！");
                return  false;
            }

  //  10 件     10     -91     91+10  =101   超卖
            stringRedisTemplate.setEnableTransactionSupport(true);// 开启对事务的支持
//             stringRedisTemplate.watch(kcKey);//先监视库存key
//            stringRedisTemplate.multi();
//           stringRedisTemplate.opsForValue().decrement(kcKey);
//           stringRedisTemplate.opsForSet().add(usersKey,userid);// user5454  user7777
//            List<Object> exec = stringRedisTemplate.exec();//   [ ok ，exception]
//  ERR EXEC without MULTI       错误 执行的时候没有发现组队 MULTI
            //  相当于两个连接       multi             exec
          //


            SessionCallback<Object> callback=new SessionCallback<Object>() {
                @Override
                public  Object execute(RedisOperations redisOperations) throws DataAccessException {
                  // 秒杀的业务逻辑   if<0 验证
                    redisOperations.watch(kcKey);//先监视库存key
                    String o = (String)redisOperations.opsForValue().get(kcKey);// 获取真实库存数
                    redisOperations.multi();
                    int i=Integer.parseInt(o);
                    i=i-1;
                    if(i<0){
                        return null;  //库存不足了   预判断一下
                    }
                    redisOperations.opsForValue().decrement(kcKey);
                    redisOperations.opsForSet().add(usersKey,userid);
                    //[ok .OK   -0 ]
                    return redisOperations.exec();// 实际的执行结果是一个list集合
                }
            };

            List<Object> resultList =(List<Object>) stringRedisTemplate.execute(callback);
                if(resultList==null||resultList.size()==0){
                    System.err.println("秒杀失败！！");
                    return  false;
                }

            System.out.println("秒杀成功！！！");

            return  true; //秒杀环节 ！！

                    }
}
