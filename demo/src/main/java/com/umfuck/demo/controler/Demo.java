package com.umfuck.demo.controler;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class Demo {

    @RequestMapping("/doHello")
    public Object doHello(){

        return "dddddddd";
    }

}
