package org.example.springboot.mapper;

import org.example.springboot.domain.entity.system.RolePermissionLink;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色、权限关系Mapper接口
 * </p>
 */
@Mapper
public interface RolePermissionLinkMapper extends BaseMapper<RolePermissionLink> {

}
