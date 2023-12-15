package com.huizi.easydinner.ums.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huizi.easydinner.ums.entity.UmsResource;
import com.huizi.easydinner.ums.mapper.UmsResourceMapper;
import com.huizi.easydinner.ums.service.UmsResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 后台资源管理Service实现类
 * Created by macro on 2020/2/2.
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {
    @Autowired
    private UmsResourceMapper resourceMapper;


    @Override
    public List<UmsResource> listAll() {
        LambdaQueryWrapper<UmsResource> umsResourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        return resourceMapper.selectList(umsResourceLambdaQueryWrapper);
    }
}
