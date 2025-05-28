package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session"; // 替换为实际的APPID

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;



    /**
     * 微信登录
     *
     * @param userLoginDTO 登录参数
     * @return 用户信息
     */
    public User wxLogin(UserLoginDTO userLoginDTO){

        
        //调用微信接口服务，获得当前微信用户的openid
        String openid = getOpenId(userLoginDTO.getCode());
        
        if(openid == null){
            //登录失败
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断openid是否为空,如果为空登录失败

        User user = userMapper.getByOpenid(openid);


        //判断当前用户是否为新用户

        if(user == null){
            //如果是新用户，自动完成注册
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 返回这个用户对象

        return user;
    }

    /**
     * 调用微信接口服务，获得当前微信用户的openid
     *
     * @param code 微信登录凭证
     * @return openid
     */

    private String getOpenId(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid()); // 替换为实际的APPID
        map.put("secret", weChatProperties.getSecret()); // 替换为实际的APPSECRET
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }


}