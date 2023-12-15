package com.tc.easydinner.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huizi.easydinner.exception.Asserts;
import com.huizi.easydinner.util.JwtTokenUtil;
import com.tc.easydinner.domain.MemberDetails;
import com.tc.easydinner.mapper.UmsMemberMapper;
import com.tc.easydinner.model.UmsMember;
import com.tc.easydinner.service.UmsMemberService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会员管理Service实现类
 * Created by macro on 2018/8/3.
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    public UmsMember getByUsername(String username) {
        QueryWrapper<UmsMember> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsMember::getUsername, username);
        List<UmsMember> memberList = memberMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(memberList)) {
            UmsMember member = memberList.get(0);
            return member;
        }
        return null;
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public void register(String username, String password, String telephone) {
        //验证验证码
        /*if (!verifyAuthCode(authCode, telephone)) {
            Asserts.fail("验证码错误");
        }*/
        //查询是否已有该用户
        QueryWrapper<UmsMember> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsMember::getPhone, telephone);
        List<UmsMember> umsMembers = memberMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(umsMembers)) {
            Asserts.fail("该用户已经存在");
        }
        //没有该用户进行添加操作
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPhone(telephone);
        umsMember.setPassword(passwordEncoder.encode(password));
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        //获取默认会员等级并设置
        /*UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            umsMember.setMemberLevelId(memberLevelList.get(0).getId());
        }*/
        memberMapper.insert(umsMember);
    }

    @Override
    public String generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        /*memberCacheService.setAuthCode(telephone, sb.toString());*/
        return sb.toString();
    }

    @Override
    public void updatePassword(String telephone, String password, String authCode) {
        QueryWrapper<UmsMember> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsMember::getPhone, telephone);
        List<UmsMember> memberList = memberMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(memberList)) {
            Asserts.fail("该账号不存在");
        }
        //验证验证码
        if (!verifyAuthCode(authCode, telephone)) {
            Asserts.fail("验证码错误");
        }
        UmsMember umsMember = memberList.get(0);
        umsMember.setPassword(passwordEncoder.encode(password));
        memberMapper.updateByPrimaryKeySelective(umsMember);
    }

    @Override
    public UmsMember getCurrentMember() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        return memberDetails.getUmsMember();
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        UmsMember member = getByUsername(username);
        if (member != null) {
            return new MemberDetails(member);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public String refreshToken(String token) {
        return jwtTokenUtil.refreshHeadToken(token);
    }

    @Override
    public void updateMsgCode(Long userId, String code) {
        LambdaUpdateWrapper<UmsMember> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UmsMember::getId, userId);
        if (StringUtils.isNotBlank(code)) {
            updateWrapper.set(UmsMember::getCode, code);
        }
        memberMapper.update(null, updateWrapper);
    }

    //对输入的验证码进行校验
    private boolean verifyAuthCode(String authCode, String telephone) {
        if (StrUtil.isEmpty(authCode)) {
            return false;
        }
        QueryWrapper<UmsMember> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsMember::getPhone, telephone);
        List<UmsMember> memberList = memberMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(memberList)) {
            UmsMember member = memberList.get(0);
            String realAuthCode = member.getCode();
            return authCode.equals(realAuthCode);
        }
        return false;
    }

}
