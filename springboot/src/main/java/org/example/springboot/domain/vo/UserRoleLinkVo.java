package org.example.springboot.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.example.springboot.domain.entity.system.Role;
import org.example.springboot.domain.entity.system.User;
import org.example.springboot.domain.entity.system.UserRoleLink;

import java.io.Serial;

/**
 * <p>
 * 用户、角色关系
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户、角色关系实体", description = "用户、角色关系")
public class UserRoleLinkVo extends UserRoleLink {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户
     */
    @Schema(description = "用户")
    private User user;
    /**
     * 角色
     */
    @Schema(description = "角色")
    private Role role;
}
