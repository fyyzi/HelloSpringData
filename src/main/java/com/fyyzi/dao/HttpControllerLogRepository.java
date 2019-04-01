package com.fyyzi.dao;

import com.fyyzi.entity.HttpControllerLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HttpControllerLogRepository extends JpaRepository<HttpControllerLog, Long> {
}
