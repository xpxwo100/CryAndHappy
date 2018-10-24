package com.umfuck.demo.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@FeignClient(name= "spring-cloud-producer",fallback =SchedualServiceHiHystric.class)
public interface IloginRemote {

    @RequestMapping(value = "/user/login")
    public Object login(@RequestBody HashMap<String, Object> params);

}
