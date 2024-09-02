package org.example.springboot.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * PC端注册请求体
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "PC端注册请求体", description = "PC端注册请求体")
public class RegisterBody implements Serializable {
    @Serial
    private static final long serialVersionUID = -8064099675697144174L;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @NotBlank(message = "{account.username.NotBlank}")
    private String username;
    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotBlank(message = "{account.password.NotBlank}")
    private String password;
    /**
     * 确认密码
     */
    @Schema(description = "确认密码")
    private String confirmPwd;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;
}
