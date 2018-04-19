package com.waydeep.umuck.controler;


import com.waydeep.umuck.dao.TestMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class Demo {
    @Autowired
    public TestMapperImpl testMapperImpl;
    @RequestMapping("/test")
    public Object test(){
        List<HashMap<String,Object>> data = testMapperImpl.selectData(1);
        return data;
    }


    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        return "hello "+name+"，this is first messge";
    }
}
