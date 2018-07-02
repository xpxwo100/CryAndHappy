package com.umuck.serverone.server.controler;

import com.umuck.serverone.server.service.HelloRemote;
import com.umuck.serverone.server.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/server")
public class Demo {
    @Autowired
    private TestService testService;
    @Autowired
    private HelloRemote helloRemote;

    @RequestMapping("/test")
    public Object test(){
        List<HashMap<String,Object>> data = testService.selectData(2);
        return data;
    }

    @RequestMapping("/hello/{name}")
    @ResponseBody
    public String index(@PathVariable("name") String name) {

        return helloRemote.hello(name);
    }
    @RequestMapping("/login")
    public String login(Model model) throws Exception {
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
        return "user/sap";
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
