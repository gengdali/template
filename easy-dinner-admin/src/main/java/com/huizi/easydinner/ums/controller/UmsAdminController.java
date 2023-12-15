package com.huizi.easydinner.ums.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huizi.easydinner.api.CommonPage;
import com.huizi.easydinner.api.CommonResult;
import com.huizi.easydinner.exception.Asserts;
import com.huizi.easydinner.ums.dto.UmsAdminChildrenExportDto;
import com.huizi.easydinner.ums.dto.UmsAdminExportDto;
import com.huizi.easydinner.ums.dto.UmsAdminLoginParam;
import com.huizi.easydinner.ums.dto.UmsAdminParam;
import com.huizi.easydinner.ums.entity.UmsAdmin;
import com.huizi.easydinner.ums.service.UmsAdminService;
import com.huizi.easydinner.ums.vo.UmsAdminVo;
import com.huizi.easydinner.util.ExcelByPOIUtil;
import com.huizi.easydinner.util.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 账号表 前端控制器
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
@Api(tags = "账号管理")
@RestController
@RequestMapping("/admin")
public class UmsAdminController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private UmsAdminService umsAdminService;

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @GetMapping("/adminList")
    public CommonResult<CommonPage<UmsAdminVo>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        Page<UmsAdminVo> adminList = umsAdminService.adminList(keyword, new Page<>(pageNum, pageSize));
        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = umsAdminService.register(umsAdminParam);
        if (umsAdmin == null) {
            return CommonResult.failed();
        }
        return CommonResult.success(umsAdmin);
    }

    @ApiOperation(value = "登录以后返回token")
    @PostMapping(value = "/login")
    public CommonResult login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        String token = umsAdminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @GetMapping(value = "/refreshToken")
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = umsAdminService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "登出功能")
    @PostMapping(value = "/logout")
    public CommonResult logout() {
        return CommonResult.success(null);
    }

    @ApiOperation(value = "列表导出")
    @PostMapping(value = "/export")
    public void export(HttpServletRequest request, HttpServletResponse response) {
        List<UmsAdmin> list = umsAdminService.list();
        List<UmsAdminExportDto> collect = list.stream().map(s -> {
            UmsAdminExportDto umsAdminExportDto = new UmsAdminExportDto();
            BeanUtils.copyProperties(s, umsAdminExportDto);
            return umsAdminExportDto;
        }).collect(Collectors.toList());
        collect.forEach(k -> {
            ArrayList<UmsAdminChildrenExportDto> umsAdminChildrenExportDtos = new ArrayList<>();

            UmsAdminChildrenExportDto umsAdminChildrenExportDto1 = new UmsAdminChildrenExportDto();
            umsAdminChildrenExportDto1.setNumber("1");
            UmsAdminChildrenExportDto umsAdminChildrenExportDto2 = new UmsAdminChildrenExportDto();
            umsAdminChildrenExportDto2.setNumber("2");
            umsAdminChildrenExportDtos.add(umsAdminChildrenExportDto1);
            umsAdminChildrenExportDtos.add(umsAdminChildrenExportDto2);
            k.setChildren(umsAdminChildrenExportDtos);
        });
        if (collect.size() > 0) {
            ExportParams exportParams = new ExportParams();
            Workbook wb = ExcelExportUtil.exportExcel(exportParams, UmsAdminExportDto.class, collect);
            ExcelByPOIUtil.writeResponse("导出", wb, request, response);
        } else {
            Asserts.fail("无数据可下载");
        }

    }

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public CommonResult<String> upload(@RequestParam("file") MultipartFile file, String filePath) {
        try {
            FileUtils.uploadByFilePath(file, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success("上传成功");
    }

    @ApiOperation("下载文件")
    @PostMapping("/download")
    public CommonResult<String> download(String filePath, HttpServletResponse response) {
        try {
            FileUtils.downLoadByFilePath(filePath, response);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success("下载成功");
    }
}
