package com.waydeep.umuck.controler;

import com.waydeep.umuck.dao.TestMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class TestService {

    @Autowired
    public TestMapperImpl testMapperImpl;

    public List<HashMap<String,Object>> selectData(int id){
        return testMapperImpl.selectData(id);
    }
}
