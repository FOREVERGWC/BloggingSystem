package org.example.springboot.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.example.springboot.domain.entity.Role;

import java.io.Serial;

/**
 * <p>
 * 角色
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "角色实体", description = "角色")
public class RoleVo extends Role {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private String statusText;
}
