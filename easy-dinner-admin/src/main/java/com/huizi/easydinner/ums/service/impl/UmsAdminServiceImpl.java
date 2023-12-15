package com.huizi.easydinner.ums.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huizi.easydinner.bo.AdminUserDetails;
import com.huizi.easydinner.exception.Asserts;
import com.huizi.easydinner.ums.dto.UmsAdminParam;
import com.huizi.easydinner.ums.entity.UmsAdmin;
import com.huizi.easydinner.ums.entity.UmsResource;
import com.huizi.easydinner.ums.mapper.UmsAdminMapper;
import com.huizi.easydinner.ums.mapper.UmsMenuMapper;
import com.huizi.easydinner.ums.mapper.UmsResourceMapper;
import com.huizi.easydinner.ums.service.UmsAdminService;
import com.huizi.easydinner.ums.vo.UmsAdminVo;
import com.huizi.easydinner.util.JwtTokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 账号表 服务实现类
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdmin> implements UmsAdminService {

    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UmsMenuMapper umsMenuMapper;
    @Autowired
    private UmsResourceMapper resourceMapper;

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        LambdaQueryWrapper<UmsAdmin> umsAdminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        umsAdminLambdaQueryWrapper.eq(UmsAdmin::getUsername, umsAdminParam.getUsername());
        List<UmsAdmin> umsAdminList = adminMapper.selectList(umsAdminLambdaQueryWrapper);
        if (umsAdminList.size() > 0) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        adminMapper.insert(umsAdmin);
        return umsAdmin;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                Asserts.fail("密码不正确");
            }
            if (!userDetails.isEnabled()) {
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
//            updateLoginTimeByUsername(username);
            /*insertLoginLog(username);*/
        } catch (AuthenticationException e) {
            /*LOGGER.warn("登录异常:{}", e.getMessage());*/
        }
        return token;
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户信息
        UmsAdmin admin = getAdminByUsername(username);
        if (admin != null) {
            List<UmsResource> resourceList = getResourceList(admin.getId());
            return new AdminUserDetails(admin, resourceList);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdmin admin;
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername, username);
        List<UmsAdmin> adminList = list(wrapper);
        if (adminList != null && adminList.size() > 0) {
            admin = adminList.get(0);
            return admin;
        }
        return null;
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        List<UmsResource> resourceList = resourceMapper.selectList(new LambdaQueryWrapper<UmsResource>());
        if (CollUtil.isNotEmpty(resourceList)) {
            return resourceList;
        }
        return resourceList;
    }

    @Override
    public Page<UmsAdminVo> adminList(String keyword, Page<UmsAdmin> umsAdminVOPage) {

        Page<UmsAdminVo> pageList = adminMapper.adminList(keyword, umsAdminVOPage);
        return pageList;
    }
}
