package com.huizi.easydinner.ums.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huizi.easydinner.ums.dto.UmsAdminParam;
import com.huizi.easydinner.ums.entity.UmsAdmin;
import com.huizi.easydinner.ums.entity.UmsResource;
import com.huizi.easydinner.ums.vo.UmsAdminVo;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * <p>
 * 账号表 服务类
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
public interface UmsAdminService extends IService<UmsAdmin> {

    /**
     * 注册功能
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 登录功能
     *
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT的token
     */
    String login(String username, String password);

    /**
     * 刷新token的功能
     *
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);

    /**
     * 根据用户名获取后台管理员
     */
    UmsAdmin getAdminByUsername(String username);

    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 获取指定用户的可访问资源
     */
    List<UmsResource> getResourceList(Long adminId);

    /**
     * 获取后台用户列表
     *
     * @param keyword
     * @param umsAdminVOPage
     * @return
     */
    Page<UmsAdminVo> adminList(String keyword, Page<UmsAdmin> umsAdminVOPage);
}
