package com.tiangong.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tiangong.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.util
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-14  10:36
 * @Description: token工具类
 * @Version: 1.0
 */
public class TokenUtil {

    //token签名的签发者
    private static final String ISSUER = "签发者";
    
    /**
    * @description: 生成token信息
    * @author: ChenLipeng 
    * @date: 2022/6/14 13:26
    * @param: userId
    * @return: java.lang.String
    **/
    public static String generateToken(Long userId) throws Exception{
        //设置RSA256加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        //获得日历类实例
        Calendar calendar = Calendar.getInstance();
        //设置当前时间
        calendar.setTime(new Date());
        //设置token过期时间
        calendar.add(Calendar.HOUR, 1);
        return JWT.create().withKeyId(String.valueOf(userId)) //将用户id放入token
                .withIssuer(ISSUER) //将签名发放者放入token
                .withExpiresAt(calendar.getTime()) //将token获取时间及过期时间放入token
                .sign(algorithm); //将加密算法放入token
    }

    /**
    * @description: 验证token
    * @author: ChenLipeng
    * @date: 2022/6/14 13:25
    * @param: token
    * @return: java.lang.Long
    **/
    public static Long verifyToken(String token){
        try {
            //获取RSA256加密算法
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            //获取JWT的验证实例
            JWTVerifier verifier = JWT.require(algorithm).build();
            //验证token并解码
            DecodedJWT jwt = verifier.verify(token);
            //从token中获取用户id
            String userId = jwt.getKeyId();
            //返回用户id
            return Long.valueOf(userId);
        } catch (TokenExpiredException e) {
            throw new ConditionException("555", "token过期！");
        }catch (Exception e){
            throw new ConditionException("非法用户token！");
        }
    }

    /**
    * @description: 生成refreshToken
    * @author: ChenLipeng
    * @date: 2022/7/15 19:02
    * @param: userId
    * @return: java.lang.String
    **/
    public static String generateRefreshToken(Long userId) throws Exception {
        //设置RSA256加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        //获得日历类实例
        Calendar calendar = Calendar.getInstance();
        //设置当前时间
        calendar.setTime(new Date());
        //设置token过期时间
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId)) //将用户id放入token
                .withIssuer(ISSUER) //将签名发放者放入token
                .withExpiresAt(calendar.getTime()) //将token获取时间及过期时间放入token
                .sign(algorithm); //将加密算法放入token
    }
}
