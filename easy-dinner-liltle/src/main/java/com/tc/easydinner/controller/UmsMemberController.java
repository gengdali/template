package com.tc.easydinner.controller;


import com.huizi.easydinner.api.CommonResult;
import com.huizi.easydinner.util.HuaWeiSMSUtil;
import com.tc.easydinner.bo.SendMsgRequest;
import com.tc.easydinner.model.UmsMember;
import com.tc.easydinner.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 人员管理
 * </p>
 *
 * @author gengwei
 * @since 2023-03-16
 */
@Api(tags = "成员账户管理")
@RestController
@RequestMapping("/umsMember")
public class UmsMemberController {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String telephone) {
        memberService.register(username, password, telephone);
        return CommonResult.success(null, "注册成功");
    }

    @ApiOperation("会员登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestParam String username,
                              @RequestParam String password) {
        String token = memberService.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("手机验证码登录")
    @PostMapping("/loginByCode")
    @ResponseBody
    public CommonResult loginByCode(@RequestParam String username,
                                    @RequestParam String password) {
        String token = memberService.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取会员信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult info(Principal principal) {
        if (principal == null) {
            return CommonResult.unauthorized(null);
        }
        UmsMember member = memberService.getCurrentMember();
        return CommonResult.success(member);
    }

    @ApiOperation("获取短信验证码")
    @RequestMapping(value = "/getAuthCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult sendMsg(@RequestBody SendMsgRequest sendMsgRequest) {
        String code = HuaWeiSMSUtil.num().toString();
        try {
            HuaWeiSMSUtil.sendSms(code, sendMsgRequest.getPhone());
            //HttpSession session = request.getSession();
            //session.setAttribute("code", code);
            memberService.updateMsgCode(sendMsgRequest.getUserId(), code);
            return CommonResult.success("验证码：" + code, "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed("验证码发送失败");
        }
    }

    @ApiOperation("会员修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam String telephone,
                                       @RequestParam String password,
                                       @RequestParam String authCode) {
        memberService.updatePassword(telephone, password, authCode);
        return CommonResult.success(null, "密码修改成功");
    }


    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }


}

