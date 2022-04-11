package com.dmall.testlog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Topic2Service {
    private Logger topicLogger2 = LoggerFactory.getLogger(Topic2Service.class);

    @Autowired
    private LogSample logsample;

    public void printAllLog(int contentSize, int count) {
        printInfoLog(contentSize,count);
        printWarnLog(contentSize,count);
        printErrorLog(contentSize,count);
        printDebugLog(contentSize,count);
    }

    public void printInfoLog(int contentSize, int count) {
        String content = logsample.getString(contentSize);
        for (int i = 0; i < count; i++) {
            topicLogger2.info(content);
        }
    }

    public void printWarnLog(int contentSize, int count) {
        String content = logsample.getString(contentSize);
        for (int i = 0; i < count; i++) {
            topicLogger2.warn(content);
        }
    }

    public void printErrorLog(int contentSize, int count) {
        String content = logsample.getString(contentSize);
        for (int i = 0; i < count; i++) {
            topicLogger2.error(content);
        }
    }
    public void printDebugLog(int contentSize, int count) {
        String content = logsample.getString(contentSize);
        for (int i = 0; i < count; i++) {
            topicLogger2.debug(content);
        }
    }
}
