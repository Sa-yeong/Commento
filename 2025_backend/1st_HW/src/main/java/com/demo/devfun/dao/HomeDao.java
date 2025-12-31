package com.demo.devfun.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import  java.util.Map;

public interface HomeDao {
    int getTestCount();
    List<Map<String, Object>> getRequests();
}
