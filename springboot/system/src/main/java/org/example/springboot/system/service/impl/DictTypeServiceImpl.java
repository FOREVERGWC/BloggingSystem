package org.example.springboot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot.common.common.enums.ResultCode;
import org.example.springboot.system.common.enums.EnableStatus;
import org.example.springboot.system.common.exception.ServiceException;
import org.example.springboot.system.domain.entity.DictType;
import org.example.springboot.system.domain.dto.DictTypeDto;
import org.example.springboot.system.domain.vo.DictTypeVo;
import org.example.springboot.system.mapper.DictTypeMapper;
import org.example.springboot.system.service.IBaseService;
import org.example.springboot.system.service.IDictTypeService;
import jakarta.annotation.Resource;
import org.example.springboot.system.utils.ExcelUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 字典类型服务实现类
 * </p>
 */
@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements IDictTypeService, IBaseService<DictType> {
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public boolean save(DictType entity) {
        entity.setStatus(EnableStatus.NORMAL.getCode());
        return super.save(entity);
    }

    @Override
    public boolean saveOrUpdate(DictType entity) {
        if (entity.getId() == null) {
            return save(entity);
        }
        return super.updateById(entity);
    }

    @Override
    public List<DictTypeVo> getList(DictTypeDto dto) {
        List<DictType> dictTypeList = getWrapper(dto).list();
        if (CollectionUtil.isEmpty(dictTypeList)) {
            return List.of();
        }
        // 组装VO
        return dictTypeList.stream().map(item -> {
            DictTypeVo vo = new DictTypeVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).toList();
    }

    @Override
    public IPage<DictTypeVo> getPage(DictTypeDto dto) {
        Page<DictType> info = getWrapper(dto).page(new Page<>(dto.getPageNo(), dto.getPageSize()));
        if (CollectionUtil.isEmpty(info.getRecords())) {
            return new Page<>(dto.getPageNo(), dto.getPageSize(), 0);
        }
        // 组装VO
        return info.convert(item -> {
            DictTypeVo vo = new DictTypeVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        });
    }

    @Override
    public DictTypeVo getOne(DictTypeDto dto) {
        DictType one = getWrapper(dto).one();
        if (one == null) {
            return null;
        }
        // 组装VO
        DictTypeVo vo = new DictTypeVo();
        BeanUtils.copyProperties(one, vo);
        return vo;
    }

    @Override
    public DictType getByCode(String code) {
        return lambdaQuery()
                .eq(DictType::getCode, code)
                .one();
    }

    @Override
    public void exportExcel(DictType dictType, HttpServletResponse response) {
        ExcelUtils.exportExcel(response, this, dictType, DictType.class, threadPoolTaskExecutor);
    }

    @Override
    public void handleStatus(Long id) {
        DictType dictType = getById(id);
        if (dictType == null) {
            throw new ServiceException(ResultCode.DICT_TYPE_NOT_FOUND_ERROR);
        }
        if (Objects.equals(EnableStatus.NORMAL.getCode(), dictType.getStatus())) {
            dictType.setStatus(EnableStatus.DISABLE.getCode());
        } else {
            dictType.setStatus(EnableStatus.NORMAL.getCode());
        }
        updateById(dictType);
    }

    @Override
    public List<DictType> getPageList(DictType entity, IPage<DictType> page) {
        IPage<DictType> info = getWrapper(entity).page(page);
        if (CollectionUtil.isEmpty(info.getRecords())) {
            return List.of();
        }
        return info.getRecords();
    }

    @Override
    public LambdaQueryChainWrapper<DictType> getWrapper(DictType entity) {
        LambdaQueryChainWrapper<DictType> wrapper = lambdaQuery()
                .eq(entity.getId() != null, DictType::getId, entity.getId())
                .like(StrUtil.isNotBlank(entity.getName()), DictType::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getCode()), DictType::getCode, entity.getCode())
                .eq(entity.getStatus() != null, DictType::getStatus, entity.getStatus());
        if (entity instanceof DictTypeDto dto) {
            Map<String, Object> params = dto.getParams();
            // 创建时间
            Object startCreateTime = params == null ? null : params.get("startCreateTime");
            Object endCreateTime = params == null ? null : params.get("endCreateTime");

            wrapper.between(ObjectUtil.isAllNotEmpty(startCreateTime, endCreateTime),
                    DictType::getCreateTime,
                    startCreateTime, endCreateTime);
        }
        return wrapper;
    }
}
