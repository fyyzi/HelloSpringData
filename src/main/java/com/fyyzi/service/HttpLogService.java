package com.fyyzi.service;

import com.fyyzi.entity.HttpControllerLog;

public interface HttpLogService {
    /**
     * 根据ID查询
     * @param id {@link Long}
     * @return {@link HttpControllerLog}
     */
    HttpControllerLog getById(Long id);

    /**
     * 将对象保存到数据库中
     * @param httpControllerLog {@link HttpControllerLog}
     * @return {@link HttpControllerLog}
     */
    HttpControllerLog save(HttpControllerLog httpControllerLog);

    /**
     * 将 Json 格式的入参和出参报春到数据库里
     * @param requestJson 入参转Json
     * @param responseJson 出参转Json
     */
    void save(String requestJson, String responseJson);
}
