package com.neo.web;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name= "spring-cloud-producer")
public interface HelloRemote {

    @RequestMapping(value = "/demo/hello")
    public String hello(@RequestParam(value = "name") String name);

}
