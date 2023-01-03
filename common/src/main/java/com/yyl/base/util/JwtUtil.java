package com.yyl.base.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yyl.base.common.CommonConstant;
import com.yyl.base.common.ConfigConstant;
import com.yyl.base.common.TokenStatus;
import com.yyl.base.context.ApplicationContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 16:24
 */

public class JwtUtil {
    /**
     * 过期时间
     */
    private final static long EXPIRE_TIME = ApplicationContextHolder.getEnvironment()
            .getProperty(ConfigConstant.WEB_JWT_EXPIRE_TIME,Long.class, CommonConstant.DEFAULT_WEB_JWT_EXPIRE_TIME);

    /**
     * 生成token
     * @param username
     * @param secret
     * @return
     */
    public static String sign(String username, String secret) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带username信息
        return JWT.create().withClaim("loginname", username).withExpiresAt(date).sign(algorithm);
    }

    /**
     * 获取当前用户名
     * @return
     */
    public static String getLoginname() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null){
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader(CommonConstant.ACCESS_TOKEN);
            return getLoginname(token);
        }
        return null;
    }

    /**
     * 根据token获取用户名
     * @param token
     * @return
     */
    public static String getLoginname(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("loginname").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * token验证
     * @param token
     * @param username
     * @param secret
     * @return
     */
    public static TokenStatus verify(String token, String username, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).withClaim("loginname", username).build();
            verifier.verify(token);
            return TokenStatus.VALID;
        }catch (TokenExpiredException e){
            return TokenStatus.EXPIRE;
        }catch (Exception exception) {
            return TokenStatus.INVALID;
        }
    }
}
