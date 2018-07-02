package com.waydeep.umuck.controler;


import com.waydeep.umuck.dao.TestMapperImpl;
import com.waydeep.umuck.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 后端接口
 */
@RestController
@RequestMapping("/demo")
public class Demo {
    @Autowired
    public TestMapperImpl testMapperImpl;
    @Autowired
    public TestService testService;
    @RequestMapping(value ="/test", produces="application/json;charset=UTF-8")
    public  Object test(){
        List<HashMap<String,Object>> data = testService.selectData(236381);
        return data;
    }

    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        System.out.println("Feign调用成功，"+name+"，这是来自服务端的数据");
        return "Feign调用成功，"+name+"，这是来自服务端的数据";
    }

}
