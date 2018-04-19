package com.neo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HelloController {

    private String hello;
   // @Autowired
    //private HelloRemote helloRemote;
    @RequestMapping("/hello")
    public String from() {
        return this.hello;
    }

  /*  @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name) {
        return helloRemote.hello(name);
    }*/
}