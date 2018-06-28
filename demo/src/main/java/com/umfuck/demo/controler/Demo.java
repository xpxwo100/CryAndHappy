package com.umfuck.demo.controler;


import com.umfuck.demo.config.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/demo")
public class Demo {
    @Autowired
    private RedisUtils redisUtils;

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

    @RequestMapping("/list2")
    public String userList2(Model model) throws Exception {
       /* model.addAttribute("hello","Hello, Spring Boot!");
        User u = new User();
        u.setId(13);
        u.setName("asjdj");
        u.setBirthday(new Date());
        u.setSalary(456789);
        User u2 = new User();
        u2.setId(323323);
        u2.setName("dadadadad");
        List list = new ArrayList();
        list.add(u);
        list.add(u2);
        model.addAttribute("userList", list);*/
        return "user/login";
    }


    @RequestMapping("/loadSapLog")
    @ResponseBody
    public Object loadSapLog(){
        HashMap<String,Object> result = new HashMap<>();
        HashMap<String,Object> val = new HashMap<>();
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
        result.put("result",val);
        result.put("success", true);
        return  result;
    }
}
