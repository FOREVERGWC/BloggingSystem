package org.example.springboot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.springboot.common.enums.Gender;
import org.example.springboot.common.enums.ResultCode;
import org.example.springboot.common.enums.UserStatus;
import org.example.springboot.common.exception.CustomException;
import org.example.springboot.domain.dto.UserDto;
import org.example.springboot.domain.entity.system.Role;
import org.example.springboot.domain.entity.system.User;
import org.example.springboot.domain.vo.UserVo;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.service.IRoleService;
import org.example.springboot.service.IUserRoleLinkService;
import org.example.springboot.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户信息服务实现类
 * </p>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private IRoleService roleService;
    @Resource
    private IUserRoleLinkService userRoleLinkService;

    @Override
    public boolean save(User entity) {
        validateUsernameAvailable(entity.getId(), entity.getUsername());
        validatePhoneAvailable(entity.getId(), entity.getPhone());
        validateEmailAvailable(entity.getId(), entity.getEmail());
        entity.setNickname(StrUtil.isNotBlank(entity.getNickname()) ? entity.getNickname() : entity.getUsername());
        entity.setName(StrUtil.isNotBlank(entity.getName()) ? entity.getName() : "");
        entity.setAvatar(StrUtil.isNotBlank(entity.getAvatar()) ? entity.getAvatar() : "");
        entity.setGender(StrUtil.isNotBlank(entity.getGender()) ? entity.getGender() : Gender.UNKNOWN.getCode());
        entity.setStatus(UserStatus.NORMAL.getCode());
        entity.setPhone(StrUtil.isNotBlank(entity.getPhone()) ? entity.getPhone() : "");
        entity.setOpenId(StrUtil.isNotBlank(entity.getOpenId()) ? entity.getOpenId() : "");
        entity.setBalance(BigDecimal.ZERO);
        entity.setLoginIp("");
        return super.save(entity);
    }

    @Transactional
    @Override
    public boolean saveOrUpdate(User entity) {
        if (entity.getId() == null) {
            return save(entity);
        }
        validateUsernameAvailable(entity.getId(), entity.getUsername());
        validatePhoneAvailable(entity.getId(), entity.getPhone());
        validateEmailAvailable(entity.getId(), entity.getEmail());
        return super.updateById(entity);
    }

    @Override
    public List<UserVo> getList(UserDto dto) {
        List<User> userList = getWrapper(dto).list();
        if (CollectionUtil.isEmpty(userList)) {
            return List.of();
        }
        // 角色
        List<Long> userIdList = userList.stream().map(User::getId).toList();
        Map<Long, List<Long>> roleIdListMap = roleService.mapRoleIdsByUserIds(userIdList);
        Map<Long, List<Role>> roleMap = roleService.mapByUserIds(userIdList);
        // 组装VO
        return userList.stream().map(item -> {
            UserVo vo = new UserVo();
            BeanUtils.copyProperties(item, vo);
            vo.setRoleIdList(roleIdListMap.getOrDefault(item.getId(), List.of()));
            vo.setRoleList(roleMap.getOrDefault(item.getId(), List.of()));
            return vo;
        }).toList();
    }

    @Override
    public IPage<UserVo> getPage(UserDto dto) {
        Page<User> info = getWrapper(dto).page(new Page<>(dto.getPageNo(), dto.getPageSize()));
        if (CollectionUtil.isEmpty(info.getRecords())) {
            return new Page<>(dto.getPageNo(), dto.getPageSize(), 0);
        }
        // 角色
        List<Long> userIdList = info.getRecords().stream().map(User::getId).toList();
        Map<Long, List<Long>> roleIdListMap = roleService.mapRoleIdsByUserIds(userIdList);
        Map<Long, List<Role>> roleMap = roleService.mapByUserIds(userIdList);
        // 组装VO
        return info.convert(item -> {
            UserVo vo = new UserVo();
            BeanUtils.copyProperties(item, vo);
            vo.setRoleIdList(roleIdListMap.getOrDefault(item.getId(), List.of()));
            vo.setRoleList(roleMap.getOrDefault(item.getId(), List.of()));
            return vo;
        });
    }

    @Override
    public UserVo getOne(UserDto dto) {
        User one = getWrapper(dto).one();
        if (one == null) {
            return null;
        }
        // 角色
        List<Long> roleIdList = userRoleLinkService.listRoleIdsByUserId(one.getId());
        List<Role> roleList = roleService.listByUserId(one.getId());
        // 组装VO
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(one, vo);
        vo.setRoleIdList(roleIdList);
        vo.setRoleList(roleList);
        return vo;
    }

    @Override
    public User getByUsername(String username) {
        return lambdaQuery()
                .eq(User::getUsername, username)
                .one();
    }

    @Override
    public User getByPhone(String phone) {
        return lambdaQuery()
                .eq(User::getPhone, phone)
                .one();
    }

    @Override
    public User getByEmail(String email) {
        return lambdaQuery()
                .eq(User::getEmail, email)
                .one();
    }

    @Override
    public void handleStatus(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new CustomException(ResultCode.USER_NOT_FOUND_ERROR);
        }
        if (Objects.equals(UserStatus.NORMAL.getCode(), user.getStatus())) {
            user.setStatus(UserStatus.DISABLE.getCode());
        } else {
            user.setStatus(UserStatus.NORMAL.getCode());
        }
        updateById(user);
    }

    /**
     * 校验用户名是否重复
     *
     * @param id       主键ID
     * @param username 用户名
     */
    private void validateUsernameAvailable(Long id, String username) {
        if (StrUtil.isBlank(username)) {
            return;
        }
        User user = getByUsername(username);
        if (user == null) {
            return;
        }
        if (id == null) {
            throw new RuntimeException("注册失败！用户名已存在");
        }
        if (!Objects.equals(id, user.getId())) {
            throw new RuntimeException("修改失败！用户名已存在");
        }
    }

    /**
     * 校验电话是否重复
     *
     * @param id    主键ID
     * @param phone 电话
     */
    private void validatePhoneAvailable(Long id, String phone) {
        if (StrUtil.isBlank(phone)) {
            return;
        }
        User user = getByPhone(phone);
        if (user == null) {
            return;
        }
        if (id == null) {
            throw new RuntimeException("注册失败！电话已存在");
        }
        if (!Objects.equals(id, user.getId())) {
            throw new RuntimeException("修改失败！电话已存在");
        }
    }

    /**
     * 校验邮箱是否重复
     *
     * @param id    主键ID
     * @param email 邮箱
     */
    private void validateEmailAvailable(Long id, String email) {
        if (StrUtil.isBlank(email)) {
            return;
        }
        User user = getByEmail(email);
        if (user == null) {
            return;
        }
        if (id == null) {
            throw new RuntimeException("注册失败！邮箱已存在");
        }
        if (!Objects.equals(id, user.getId())) {
            throw new RuntimeException("修改失败！邮箱已存在");
        }
    }

    /**
     * 组装查询包装器
     *
     * @param entity 用户信息
     * @return 结果
     */
    private LambdaQueryChainWrapper<User> getWrapper(User entity) {
        LambdaQueryChainWrapper<User> wrapper = lambdaQuery()
                .eq(entity.getId() != null, User::getId, entity.getId())
                .like(StrUtil.isNotBlank(entity.getUsername()), User::getUsername, entity.getUsername())
                .like(StrUtil.isNotBlank(entity.getNickname()), User::getNickname, entity.getNickname())
                .like(StrUtil.isNotBlank(entity.getName()), User::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getGender()), User::getGender, entity.getGender())
                .eq(entity.getBirthday() != null, User::getBirthday, entity.getBirthday())
                .like(StrUtil.isNotBlank(entity.getStatus()), User::getStatus, entity.getStatus())
                .like(StrUtil.isNotBlank(entity.getPhone()), User::getPhone, entity.getPhone())
                .like(StrUtil.isNotBlank(entity.getEmail()), User::getEmail, entity.getEmail())
                .like(StrUtil.isNotBlank(entity.getOpenId()), User::getOpenId, entity.getOpenId())
                .eq(entity.getBalance() != null, User::getBalance, entity.getBalance())
                .like(StrUtil.isNotBlank(entity.getLoginIp()), User::getLoginIp, entity.getLoginIp());
        if (entity instanceof UserDto dto) {
            Map<String, Object> params = dto.getParams();
            // 创建时间
            Object startCreateTime = params == null ? null : params.get("startCreateTime");
            Object endCreateTime = params == null ? null : params.get("endCreateTime");

            wrapper.between(ObjectUtil.isAllNotEmpty(startCreateTime, endCreateTime),
                    User::getCreateTime,
                    startCreateTime, endCreateTime);
        }
        return wrapper;
    }
}
