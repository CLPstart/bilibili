package com.tiangong.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DemoDao {
    Long query(@Param("id") Long id);
}
