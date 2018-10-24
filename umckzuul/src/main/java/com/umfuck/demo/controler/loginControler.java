package com.umfuck.demo.controler;


import com.umfuck.demo.config.RedisUtils;
import com.umfuck.demo.remote.IHelloRemote;
import com.umfuck.demo.remote.IloginRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/user")
public class loginControler {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IHelloRemote helloRemote;
    @Autowired
    private IloginRemote loginRemote;


    @RequestMapping("/doHello")
    @ResponseBody
    public Object doHello(){
       return  getUserByName();
    }

    public String getUserByName() {
       // redisUtils.set("name","fuck");
       // String s = (String) redisUtils.get("name");
        return "testData";
    }

    @RequestMapping("/main")
    public String userList2(HttpSession session, HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        return "user/login";
    }


    @RequestMapping("/loadSapLog.do")
    @ResponseBody
    public Object loadSapLog(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response){
        HashMap<String,Object> val = new HashMap<>();
        HashMap<String,Object> result = new HashMap<>();
        System.out.println(redisUtils.toString());
        if(redisUtils.get("loadSapLog")!= null){
             val = (HashMap<String, Object>) redisUtils.get("loadSapLog");
        }else{
            List list = new ArrayList();
            for(int i =0;i<10;i++){
                HashMap<String,Object> maps = new HashMap<>();
                maps.put("apiCode","Vendor");
                maps.put("companyID",1248);
                maps.put("reqBody","rstagdaydabdabdbaydybhb");
                maps.put("hasErr",false);
                list.add(maps);
            }
            val.put("data",list);
            val.put("count",list.size());
            redisUtils.set("loadSapLog",val);
        }
        result.put("result",val);
        result.put("success", true);
        Object s = helloRemote.hello(val);
        System.out.println(s.toString());
        return  result;
    }

    @RequestMapping("/deLoadSapLog")
    @ResponseBody
    public Object deLoadSapLog(){
        redisUtils.remove("loadSapLog");
        return  null;
    }

    @RequestMapping("/login.do")
    @ResponseBody
    public Object login(HttpSession session, HttpServletRequest request,
                        HttpServletResponse response,@RequestBody HashMap<String,Object> params){
        HashMap<String,Object> result = new HashMap<>();
        String username = (String) params.get("username");
        String password = (String) params.get("password");

        result = (HashMap<String, Object>) loginRemote.login(params);
        if((boolean)result.get("success")== true && result.get("user")!=null){
            session.setAttribute("user",session.getId());
        }
        return  result;
    }

}
