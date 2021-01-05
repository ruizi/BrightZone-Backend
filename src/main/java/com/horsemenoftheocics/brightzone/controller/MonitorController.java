package com.horsemenoftheocics.brightzone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MonitorController {

    @GetMapping(path = "/index")
    public @ResponseBody String index() {
        return "helloWord";
    }

}
