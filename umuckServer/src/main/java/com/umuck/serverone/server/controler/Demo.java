package com.umuck.serverone.server.controler;

import com.umuck.serverone.server.service.HelloRemote;
import com.umuck.serverone.server.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
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
    public String index(@PathVariable("name") String name) {
        return helloRemote.hello(name);
    }
}
