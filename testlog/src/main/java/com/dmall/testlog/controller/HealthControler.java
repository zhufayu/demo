package com.dmall.testlog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthControler {
    @GetMapping("/ready")
    public String ready() {
        return "OK";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    //http://localhost:8080/hello
    @RequestMapping(value = "/hello", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String createTableForTidb() throws Exception {
        return "hello1213";
    }

}
