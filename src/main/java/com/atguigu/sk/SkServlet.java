package com.atguigu.sk;

import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Administrator
 * @Date 2020/6/30 20:47
 * @Version 1.0
 */
public class SkServlet extends HttpServlet {
   /* @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、获取请求参数：  商品id ， 用户id(随机生成)
    String pid = req.getParameter("pid");
    String uid = (int)(10000*Math.random())+"";
        System.out.println("uid = " + uid+" , pid = "+ pid);
    //拼接  库存key和 秒杀商品用户列表的key
    String qtKey = "sk:"+ pid + ":qt";
    String usrsKey = "sk:" + pid +":usr";
        System.out.println("qtKey = " + qtKey);
    //2、判断用户是否重复秒杀[用户会存在redis中  set]
    Jedis jedis = new Jedis("192.168.22.130", 6379);
    Boolean sismember = jedis.sismember(usrsKey, uid);
        if(sismember){
        //用户重复秒杀
        System.err.println("userid为："+uid +" 重复秒杀了....");
        resp.getWriter().write("10001");
        return;//结束当前方法
    }
    //3、判断库存是否足够
    String qtStr = null;
        try {
        qtStr = jedis.get(qtKey);
    } catch (Exception e) {
        e.printStackTrace();
    }
        System.out.println("qtStr = " + qtStr);
        if(qtStr==null || qtStr.length()==0){
        //库存值没有获取到，秒杀还未开始
        System.err.println("userid为："+uid +" 秒杀尚未开始....");
        resp.getWriter().write("10002");
        return;//结束当前方法
    }
    int qt = Integer.parseInt(qtStr);
        if(qt<=0){
        //库存不足
        System.err.println("userid为："+uid +" 库存不足....  qt: "+qt);
        resp.getWriter().write("10003");
        return;//结束当前方法
    }
    //4、秒杀
    //将用户添加到秒杀成功的set中
        jedis.sadd(usrsKey , uid);
    //减库存
        jedis.decr(qtKey);
    //响应
        System.out.println(uid+  ": 秒杀成功..");
        resp.getWriter().write("200");
}*/
    static String secKillScript = "local userid=KEYS[1];\r\n"
            + "local prodid=KEYS[2];\r\n"
            + "local qtkey='sk:'..prodid..\":qt\";\r\n"
            + "local usersKey='sk:'..prodid..\":usr\";\r\n"
            + "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n"
            + "if tonumber(userExists)==1 then \r\n"
            + "   return 2;\r\n"
            + "end\r\n"
            + "local num= redis.call(\"get\" ,qtkey);\r\n"
            + "if tonumber(num)<=0 then \r\n"
            + "   return 0;\r\n"
            + "else \r\n"
            + "   redis.call(\"decr\",qtkey);\r\n"
            + "   redis.call(\"sadd\",usersKey,userid);\r\n"
            + "end\r\n"
            + "return 1";

    static String secKillScript2 = "local userExists=redis.call(\"sismember\",\"{sk}:0101:usr\",userid);\r\n"
            + " return 1";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pid = req.getParameter("pid");
        String uid = (int) (10000 * Math.random()) + "";
        System.out.println("uid = " + uid + " , pid = " + pid);
        //拼接  库存key和 秒杀商品用户列表的key
        String qtKey = "sk:" + pid + ":qt";
        String usrsKey = "sk:" + pid + ":usr";
        //2、判断用户是否重复秒杀[用户会存在redis中  set]
        //Jedis jedis = new Jedis("192.168.22.130", 6379);
        //通过连接池获取
        Jedis jedis=JedisPoolUtil.getJedisPoolInstance().getResource();
        String sha1 = jedis.scriptLoad(secKillScript);
        //2.2 使用jedis执行lua脚本
        //参数1：加密过的LUA脚本字符串 ，参数2：脚本执行时需要的参数的数量   参数3：脚本执行时需要参数的实参列表
        Object result = jedis.evalsha(sha1, 2, uid, pid);
        long code = (long) result;
        if (code == 1) {
            resp.getWriter().write("200");
        } else if (code == 0) {
            resp.getWriter().write("10003");
        } else if (code == 2) {
            resp.getWriter().write("10001");
        }
        jedis.close();
    }
    }
