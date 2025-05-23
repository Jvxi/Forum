package com.xiaoRed.mapper;

import com.xiaoRed.entity.vindicate.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LikeMapper {
    @Select("select * from db_likes")
    List<Like> selectList();
}
