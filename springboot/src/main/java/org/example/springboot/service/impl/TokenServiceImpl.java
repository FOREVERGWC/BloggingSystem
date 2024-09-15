package org.example.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.springboot.common.Constants;
import org.example.springboot.common.enums.ResultCode;
import org.example.springboot.common.exception.CustomException;
import org.example.springboot.domain.model.LoginUser;
import org.example.springboot.service.ITokenService;
import org.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 令牌服务实现类
 * </p>
 */
@Service
public class TokenServiceImpl implements ITokenService {
    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public String getAuthorization(HttpServletRequest request) {
        String token = request.getHeader(Constants.TOKEN);
        if (StrUtil.isNotBlank(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    @Override
    public LoginUser getLoginUser(HttpServletRequest request) {
        String authorization = getAuthorization(request);
        if (StrUtil.isBlank(authorization)) {
            throw new CustomException(ResultCode.TOKEN_CHECK_ERROR);
        }
        TokenUtils.verifyToken(authorization);
        String username = TokenUtils.getUsernameByToken(authorization);
        // TODO 从Redis获取
        LoginUser user = (LoginUser) userDetailsService.loadUserByUsername(username);
        user.setToken(authorization);
        return user;
    }
}