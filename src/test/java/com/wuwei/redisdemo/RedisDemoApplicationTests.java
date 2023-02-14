package com.wuwei.redisdemo;

import com.wuwei.redisdemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
class RedisDemoApplicationTests {
    @Autowired
    UserMapper userMapper;
    //  它是一个接口  但是idea没有直接找到实现类 UserMapperImpl  动态代理生成的


    @Test
    void contextLoads() {
    // 整合mybatis mybatis-plus  ok
      //  System.out.println(userMapper.selectList(null));
        //Jedis  是面向java的操作redis的工具对象   无需spring集成 这里只是方便而已  添加jar包main方法即可
        Jedis  jedis=new Jedis("192.168.72.128",6379);
        System.out.println(jedis);
        jedis.set("sku:wuwei1","36516351213");
        jedis.set("sku:wuwei1","36516351213");
        jedis.set("sku:wuwei2:aa","36516351213");

        jedis.close();

//
//        jedis.set("jedisKey","success!!哈哈");
//
//        System.out.println(jedis.get("jedisKey"));
//
//        Transaction tx = jedis.multi();//已经开始组队了    意味着  redis的事务开始了
//
//        tx.set("tx100","qqqq1");
//        jedis.set("wwww","qqqq1");
//        tx.exec();
//
//
//        jedis.set("tx redis4","qqqq2");
////        jedis.incr("tx redis1");
//        jedis.set("tx redis6","qqqq3");
//
//

        /**
         * 1. 正常执行   一个事务流程  但组队里没命令
         * 2.启动报错
         * 3. 值正常进去了  但是不是事务进去的 是jedis 正常客户端请求进去
         *
         *   关系型数据库             sql 结构化查询语言   悲观锁
         *
         *   非关系型              no sql   命令
         *                      redis    k-v
         */



      /*  List<Object> resultList = tx.exec();// java需要反馈 结果
        System.out.println(resultList);  //错误结果返回了  1个命令还是个错
        // jedis.close();  资源需要关闭
            //  resultList  ==[]    size()==0
        if(resultList==null||resultList.size()==0){
            // 不成功 !!!
        }
        for (Object obj:resultList) {
            if(obj instanceof String){
                System.out.println("字符串类型");
            }
            if(obj instanceof Exception){
                System.out.println("异常错误类型");
            }
        }
*/
    }

}
