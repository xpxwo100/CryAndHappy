package com.waydeep.umuck.controler;


import com.waydeep.umuck.dao.TestMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class Demo {
    @Autowired
    public TestMapperImpl testMapperImpl;
    @Autowired
    public TestService testService;
    @RequestMapping(value ="/test", produces="application/json;charset=UTF-8")
    public @ResponseBody Object test(){
        List<HashMap<String,Object>> data = testService.selectData(236381);
        return data;
    }


    @RequestMapping("/hello")
    public String index(@RequestParam String name) {

        return "Feign调用成功，"+name+"，这是来自服务端的数据";
    }
}
