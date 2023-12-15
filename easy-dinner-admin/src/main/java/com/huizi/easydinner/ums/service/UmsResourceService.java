package com.huizi.easydinner.ums.service;

import com.huizi.easydinner.ums.entity.UmsResource;


import java.util.List;

/**
 * 后台资源管理Service
 * Created by macro on 2020/2/2.
 */
public interface UmsResourceService {


    /**
     * 查询全部资源
     */
    List<UmsResource> listAll();
}
