package com.waydeep.umuck.controler;


import com.waydeep.umuck.dao.TestMapperImpl;
import com.waydeep.umuck.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 后端接口
 */
@RestController
@RequestMapping("/user")
public class LoginControler {
    @Autowired
    public TestService testService;
    @RequestMapping(value ="/test", produces="application/json;charset=UTF-8")
    public  Object test(){
        List<HashMap<String,Object>> data = testService.selectData(236381);
        return data;
    }

    @RequestMapping("/login")
    public Object login(@RequestBody HashMap<String,Object> params) {
        HashMap<String,Object> result = new HashMap<>();
        List<HashMap<String,Object>> data = testService.selectData(236381);
        System.out.println("Feign调用成功，这是来自服务端的数据"+params.toString());
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        if(username.equals("18750419067")){
            if(!password.equals("111111")){
                result.put("success", false);
            }
            result.put("success", true);
            result.put("user", true);
        }else{
            result.put("success", false);
        }
        return result;
    }
}
