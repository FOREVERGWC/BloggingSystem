package org.example.springboot.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.example.springboot.common.enums.LoginType;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * PC端登录请求体
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@Schema(name = "PC端登录请求体", description = "PC端登录请求体")
public class LoginBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 3117188356336879282L;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @NotBlank(message = "{username.NotBlank}")
    private String username;
    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotBlank(message = "{password.NotBlank}")
    private String password;
    /**
     * 手机
     */
    @Schema(description = "手机")
    private String phone;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
    /**
     * 唯一标识
     */
    @Schema(description = "唯一标识")
    private String uuid;
    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;
    /**
     * 登录类型
     */
    @Schema(description = "登录类型")
    @NotNull(message = "{loginType.NotNull}")
    private LoginType loginType;
}
