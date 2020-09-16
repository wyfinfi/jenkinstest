package com.atguigu.sk;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.Set;

/**
 * @Author Administrator
 * @Date 2020/7/1 10:14
 * @Version 1.0
 */
public class JedisTest {
    public static void main(String[] args) {
        //操作redis
        //1、建立和redis的连接获取连接对象
        //参数1：redis启动所在的服务器ip地址，参数2：redis的端口号
        Jedis jedis = new Jedis("192.168.22.130", 6379);
        //通过连接对象发送redis的命令让redis执行
        String ping = jedis.ping();
        String s = jedis.get("sk:1001:qt");
        System.out.println("s = " + s);
        System.out.println("ping = " + ping);
        HashMap<String, Double> map = new HashMap<>();
        map.put("shenyizhao", 35.5);
        map.put("yangyang", 100.0);
        map.put("anni", 0.0);
        map.put("wangzhuang", 88.1);
        map.put("zhangsan", 66.1);
        map.put("lisi", 55.1);
        jedis.zadd("scores", map);

//        2、查询zset中的数据：查询分数前三名的学生信息*/
//        Tuple就代表set中的值和他的分数
       /* Set<Tuple> scores = jedis.zrevrangeWithScores("scores", 0, 2);
        for (Tuple tuple : scores) {
            System.out.println("学生 = " + tuple.getElement()+" , 分数为："+tuple.getScore());
        }*/
        //3、删除zset：键操作
        //jedis.del("scores");

        //3、关闭redis连接
        jedis.close();
    }
}
