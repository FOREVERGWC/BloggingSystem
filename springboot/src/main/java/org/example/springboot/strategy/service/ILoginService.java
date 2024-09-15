package org.example.springboot.strategy.service;

import org.example.springboot.domain.model.LoginBody;
import org.example.springboot.domain.model.LoginUser;

/**
 * <p>
 * 登录服务类
 * </p>
 */
public interface ILoginService {
    // TODO OpenIDSessionLogin
    /**
     * 登录
     *
     * @param body 登录请求体
     * @return 结果
     */
    LoginUser login(LoginBody body);
}
