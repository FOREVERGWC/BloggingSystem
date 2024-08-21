package org.example.springboot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.springboot.domain.dto.UserRoleLinkDto;
import org.example.springboot.domain.entity.Role;
import org.example.springboot.domain.entity.User;
import org.example.springboot.domain.entity.UserRoleLink;
import org.example.springboot.domain.vo.UserRoleLinkVo;
import org.example.springboot.mapper.RoleMapper;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.mapper.UserRoleLinkMapper;
import org.example.springboot.service.IUserRoleLinkService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户、角色关系服务实现类
 * </p>
 */
@Service
public class UserRoleLinkServiceImpl extends ServiceImpl<UserRoleLinkMapper, UserRoleLink> implements IUserRoleLinkService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;

    @Transactional
    @Override
    public void saveBatchByUserIdAndRoleIds(Long userId, List<Long> roleIds) {
        lambdaUpdate()
                .eq(UserRoleLink::getUserId, userId)
                .remove();
        if (CollectionUtil.isEmpty(roleIds)) {
            return;
        }
        List<UserRoleLink> linkList = roleIds.stream()
                .<UserRoleLink>map(item -> UserRoleLink.builder()
                        .userId(userId)
                        .roleId(item)
                        .build())
                .toList();
        saveBatch(linkList);
    }

    @Override
    public void removeByUserId(Serializable userId) {
        lambdaUpdate()
                .eq(UserRoleLink::getUserId, userId)
                .remove();
    }

    @Override
    public void removeByUserIds(Collection<?> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        lambdaUpdate()
                .in(UserRoleLink::getUserId, userIds)
                .remove();
    }

    @Override
    public void removeByRoleId(Serializable roleId) {
        lambdaUpdate()
                .eq(UserRoleLink::getRoleId, roleId)
                .remove();
    }

    @Override
    public void removeByRoleIds(Collection<?> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return;
        }
        lambdaUpdate()
                .in(UserRoleLink::getRoleId, roleIds)
                .remove();
    }

    @Override
    public List<UserRoleLink> listByUserId(Serializable userId) {
        return Optional.ofNullable(lambdaQuery()
                        .eq(UserRoleLink::getUserId, userId)
                        .list())
                .orElse(List.of());
    }

    @Override
    public List<UserRoleLink> listByUserIds(Collection<?> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return List.of();
        }
        return Optional.ofNullable(lambdaQuery()
                        .in(UserRoleLink::getUserId, userIds)
                        .list())
                .orElse(List.of());
    }

    @Override
    public List<UserRoleLink> listByRoleId(Serializable roleId) {
        return Optional.ofNullable(lambdaQuery()
                        .eq(UserRoleLink::getRoleId, roleId)
                        .list())
                .orElse(List.of());
    }

    @Override
    public List<UserRoleLink> listByRoleIds(Collection<?> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return List.of();
        }
        return Optional.ofNullable(lambdaQuery()
                        .in(UserRoleLink::getRoleId, roleIds)
                        .list())
                .orElse(List.of());
    }

    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        List<UserRoleLink> linkList = listByUserId(userId);
        return linkList.stream().map(UserRoleLink::getRoleId).toList();
    }

    @Override
    public List<Long> listRoleIdsByUserIds(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return List.of();
        }
        List<UserRoleLink> linkList = listByUserIds(userIds);
        return linkList.stream().map(UserRoleLink::getRoleId).toList();
    }

    @Override
    public List<Long> listUserIdsByRoleId(Long roleId) {
        List<UserRoleLink> linkList = listByRoleId(roleId);
        return linkList.stream().map(UserRoleLink::getUserId).toList();
    }

    @Override
    public List<Long> listUserIdsByRoleIds(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return List.of();
        }
        List<UserRoleLink> linkList = listByRoleIds(roleIds);
        return linkList.stream().map(UserRoleLink::getUserId).toList();
    }

    @Override
    public List<UserRoleLinkVo> getList(UserRoleLinkDto dto) {
        List<UserRoleLink> userRoleLinkList = getWrapper(dto).list();
        if (CollectionUtil.isEmpty(userRoleLinkList)) {
            return List.of();
        }
        // 用户
        List<Long> userIdList = userRoleLinkList.stream().map(UserRoleLink::getUserId).toList();
        List<User> userList = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIdList));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, item -> item));
        // 角色
        List<Long> roleIdList = userRoleLinkList.stream().map(UserRoleLink::getRoleId).toList();
        List<Role> roleList = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIdList));
        Map<Long, Role> roleMap = roleList.stream().collect(Collectors.toMap(Role::getId, item -> item));
        // 组装VO
        return userRoleLinkList.stream().map(item -> {
            UserRoleLinkVo vo = new UserRoleLinkVo();
            BeanUtils.copyProperties(item, vo);
            vo.setUser(userMap.getOrDefault(item.getUserId(), User.builder().name("已删除").build()));
            vo.setRole(roleMap.getOrDefault(item.getRoleId(), Role.builder().name("已删除").build()));
            return vo;
        }).toList();
    }

    @Override
    public IPage<UserRoleLinkVo> getPage(UserRoleLinkDto dto) {
        Page<UserRoleLink> info = getWrapper(dto).page(new Page<>(dto.getPageNo(), dto.getPageSize()));
        if (CollectionUtil.isEmpty(info.getRecords())) {
            return new Page<>(dto.getPageNo(), dto.getPageSize(), 0);
        }
        // 用户
        List<Long> userIdList = info.getRecords().stream().map(UserRoleLink::getUserId).toList();
        List<User> userList = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIdList));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, item -> item));
        // 角色
        List<Long> roleIdList = info.getRecords().stream().map(UserRoleLink::getRoleId).toList();
        List<Role> roleList = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIdList));
        Map<Long, Role> roleMap = roleList.stream().collect(Collectors.toMap(Role::getId, item -> item));
        // 组装VO
        return info.convert(item -> {
            UserRoleLinkVo vo = new UserRoleLinkVo();
            BeanUtils.copyProperties(item, vo);
            vo.setUser(userMap.getOrDefault(item.getUserId(), User.builder().name("已删除").build()));
            vo.setRole(roleMap.getOrDefault(item.getRoleId(), Role.builder().name("已删除").build()));
            return vo;
        });
    }

    @Override
    public UserRoleLinkVo getOne(UserRoleLinkDto dto) {
        UserRoleLink one = getWrapper(dto).one();
        if (one == null) {
            return null;
        }
        // 用户
        User user = Optional.ofNullable(userMapper.selectById(one.getUserId())).orElse(User.builder().name("已删除").build());
        // 角色
        Role role = Optional.ofNullable(roleMapper.selectById(one.getRoleId())).orElse(Role.builder().name("已删除").build());
        // 组装VO
        UserRoleLinkVo vo = new UserRoleLinkVo();
        BeanUtils.copyProperties(one, vo);
        vo.setUser(user);
        vo.setRole(role);
        return vo;
    }

    /**
     * 组装查询包装器
     *
     * @param dto 用户、角色关系
     * @return 结果
     */
    private LambdaQueryChainWrapper<UserRoleLink> getWrapper(UserRoleLinkDto dto) {
        Map<String, Object> params = dto.getParams();
        // 创建时间
        Object startCreateTime = params == null ? null : params.get("startCreateTime");
        Object endCreateTime = params == null ? null : params.get("endCreateTime");
        return lambdaQuery()
                .eq(dto.getId() != null, UserRoleLink::getId, dto.getId())
                .eq(dto.getUserId() != null, UserRoleLink::getUserId, dto.getUserId())
                .eq(dto.getRoleId() != null, UserRoleLink::getRoleId, dto.getRoleId())
                .between(ObjectUtil.isAllNotEmpty(startCreateTime, endCreateTime), UserRoleLink::getCreateTime, startCreateTime, endCreateTime);
    }
}
