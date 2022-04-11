package com.dmall.testlog.controller;


import com.dmall.testlog.service.PolarService;
import com.dmall.testlog.service.Topic1Service;
import com.dmall.testlog.service.Topic2Service;
import com.dmall.testlog.service.Topic3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class PolarServiceController {
    private Logger logger = LoggerFactory.getLogger("STDOUT");
    @Autowired
    private PolarService polarService;
    @Autowired
    private Topic1Service topicService;
    @Autowired
    private Topic2Service topic2Service;
    @Autowired
    private Topic3Service topic3Service;

    //http://127.0.0.1:8080/logs/async/app/all/1000/50
    //http://127.0.0.1:8080/logs/async/topic/all/1000/50
    @RequestMapping("/logs/async/{type}/{level}/{size}/{count}")
    public String asyncPrintLog(@PathVariable("size") int size,
                                @PathVariable("count") int count,
                                @PathVariable("type") String type,
                                @PathVariable("level") String level) {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = sdf3.format(date);
        if ("topic".equals(type)) {
            if ("info".equals(level)) {
                new Thread(() -> topicService.printInfoLog(size, count)).start();
                new Thread(() -> topic2Service.printInfoLog(size, count)).start();
                new Thread(() -> topic3Service.printInfoLog(size, count)).start();
            } else if ("warn".equals(level)) {
                new Thread(() -> topicService.printWarnLog(size, count)).start();
                new Thread(() -> topic2Service.printWarnLog(size, count)).start();
                new Thread(() -> topic3Service.printWarnLog(size, count)).start();
            } else if ("error".equals(level)) {
                new Thread(() -> topicService.printErrorLog(size, count)).start();
                new Thread(() -> topic2Service.printErrorLog(size, count)).start();
                new Thread(() -> topic3Service.printErrorLog(size, count)).start();
            } else if ("debug".equals(level)) {
                new Thread(() -> topicService.printDebugLog(size, count)).start();
                new Thread(() -> topic2Service.printDebugLog(size, count)).start();
                new Thread(() -> topic3Service.printDebugLog(size, count)).start();
            } else {
                new Thread(() -> topicService.printAllLog(size, count)).start();
                new Thread(() -> topic2Service.printAllLog(size, count)).start();
                new Thread(() -> topic3Service.printAllLog(size, count)).start();
            }
        } else {
            if ("info".equals(level)) {
                new Thread(() -> polarService.printInfoLog(size, count)).start();
            } else if ("warn".equals(level)) {
                new Thread(() -> polarService.printWarnLog(size, count)).start();
            } else if ("error".equals(level)) {
                new Thread(() -> polarService.printErrorLog(size, count)).start();
            } else if ("debug".equals(level)) {
                new Thread(() -> polarService.printDebugLog(size, count)).start();
            } else {
                new Thread(() -> polarService.printAllLog(size, count)).start();
            }
        }
//        System.out.println(dateStr + logger.getName());
        return "size" + size + "<br>"
                + " count: " + count + "<br>"
                + "success" + dateStr;
    }


}
