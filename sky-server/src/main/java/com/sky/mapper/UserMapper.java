package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserMapper {


    /**
     * 根据用户id查询用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户信息
     *
     * @param user 用户信息
     */
    void insert(User user);
}