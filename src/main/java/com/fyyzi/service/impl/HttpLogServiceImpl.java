package com.fyyzi.service.impl;

import com.fyyzi.dao.HttpControllerLogRepository;
import com.fyyzi.entity.HttpControllerLog;
import com.fyyzi.service.HttpLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class HttpLogServiceImpl implements HttpLogService {

    @Autowired
    private HttpControllerLogRepository httpControllerLogRepository;

    @Override
    public HttpControllerLog getById(Long id) {
        Optional<HttpControllerLog> byId = httpControllerLogRepository.findById(id);
        boolean present = false;
        while (!present) {
            present = byId.isPresent();
        }
        return byId.get();
    }

    @Override
    public HttpControllerLog save(HttpControllerLog httpControllerLog) {
        httpControllerLog.setCreateDate(new Date());
        httpControllerLog.setCreateUserId(1L);
        httpControllerLog.setCreateUserName("123");

        return httpControllerLogRepository.save(httpControllerLog);
    }

    @Override
    public void save(String requestJson, String responseJson) {
        HttpControllerLog httpControllerLog = new HttpControllerLog();
        httpControllerLog.setRequestJson(requestJson);
        httpControllerLog.setResponseJson(responseJson);
        save(httpControllerLog);
    }
}
