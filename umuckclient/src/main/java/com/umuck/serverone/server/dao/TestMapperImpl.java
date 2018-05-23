package com.umuck.serverone.server.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Mapper
public interface TestMapperImpl {
    public List<HashMap<String,Object>> selectData(int i);
}
