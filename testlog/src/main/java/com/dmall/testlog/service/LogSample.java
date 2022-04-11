package com.dmall.testlog.service;

import org.springframework.stereotype.Service;

@Service
public class LogSample {
    private String content="LogSampleLogSampl,1210700|000000036830139091171769877,1210700|000000036830139091171769!@#$%^&*()</n";

    private StringBuffer userDefStr = new StringBuffer();

    public String getString(int length) {
        while (true) {
            userDefStr.append(content);
            if (userDefStr.length() > length) {
                return userDefStr.toString().substring(0, length);
            }
        }
    }
}
