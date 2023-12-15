package com.huizi.easydinner;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


/**
 * @PROJECT_NAME: personal
 * @DESCRIPTION:mybatis-plus代码生成器
 * @AUTHOR: 12615
 * @DATE: 2022/12/15 17:58
 */

public class MybatisPlusGenerator {

    public static void main(String[] args) {

        String moduleName = scanner("模块名");
        String childrenSortPackageName = scanner("子级分类包名");
        String[] tableNames = scanner("表名，多个英文逗号分割").split(",");
        List<String> tables = Arrays.asList(tableNames);
        Props props = new Props("generator.properties");
        String driverName = props.getStr("dataSource.driverName");

        String username = props.getStr("dataSource.username");

        String url = props.getStr("dataSource.url");

        String basePackage = props.getStr("package.base");

        String password = props.getStr("dataSource.password");

        String outputDir = System.getProperty("user.dir") + "/" + moduleName + "/src/main/java";

        String pathInfo = System.getProperty("user.dir") + "/" + moduleName + "/src/main/resources/mapper/" + childrenSortPackageName;
        /**
         * 1.数据库配置(设置数据源)
         配置数据库连接以及需要使用的字段
         */
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(url, username, password).dbQuery(new MySqlQuery())
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler());

        FastAutoGenerator fastAutoGenerator = FastAutoGenerator.create(dataSourceConfigBuilder);
        /**
         * 2.全局配置
         */
        fastAutoGenerator.globalConfig(builder -> {
            builder.author("gengwei") // 设置作者
                    .enableSwagger() // 开启 swagger 模式
                    .fileOverride() // 覆盖已生成文件
                    .dateType(DateType.TIME_PACK)//时间策略
                    .commentDate("yyyy-MM-dd") //格式化时间格式
                    .disableOpenDir() //禁止打开输出目录
                    .outputDir(outputDir); // 指定输出目录
        });
        /**
         * 3.包配置
         */
        fastAutoGenerator.packageConfig(builder -> {
            builder.parent(basePackage)// 设置父包名
                    .moduleName(childrenSortPackageName)
                    .controller("controller")
                    .service("service")
                    .serviceImpl("service.impl")
                    .mapper("mapper")
                    .entity("entity") //设置entity包名
                    .other("dto") // 设置dto包名
                    .pathInfo(Collections.singletonMap(OutputFile.mapperXml, pathInfo)); // 设置mapperXml生成路径

        });
        /**
         * 4.策略配置
         */
        fastAutoGenerator.strategyConfig(builder -> {
            builder.enableCapitalMode()    // 开启大写命名
                    .enableSkipView()   // 开启跳过视图
                    .disableSqlFilter() // 禁用sql过滤
                    .addInclude(tables) // 设置需要生成的表名
                    .addTablePrefix("t_", "c_"); // 设置过滤表前缀*//*
        });


        /**
         * 4.1 Entity策略配置
         */
        fastAutoGenerator.strategyConfig(builder -> {
            builder.entityBuilder()
                    .idType(IdType.AUTO)
                    .naming(NamingStrategy.underline_to_camel)
                    .columnNaming(NamingStrategy.underline_to_camel)
                    .enableLombok();
        });

        /**
         * 4.2 Controller策略配置
         */
        fastAutoGenerator.strategyConfig(builder -> {
            builder.controllerBuilder()
                    .enableRestStyle()  // 开启生成@RestController控制器
                    .enableHyphenStyle();
        });
        /**
         * 4.3 Service策略配置
         格式化service接口和实现类的文件名称，去掉默认的ServiceName前面的I ----
         */
        fastAutoGenerator.strategyConfig(builder -> {
            builder.serviceBuilder()
                    .formatServiceFileName("%sService")
                    .formatServiceImplFileName("%sServiceImpl");
        });

        /**
         * 4.4 Mapper策略配置
         格式化 mapper文件名,格式化xml实现类文件名称
         */
        fastAutoGenerator.strategyConfig(builder -> {
            builder.mapperBuilder()
                    .enableMapperAnnotation()   // 开启 @Mapper 注解
                    .formatMapperFileName("%sMapper")
                    .formatXmlFileName("%sMapper");
        });

        fastAutoGenerator.templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    /**
     * 读取控制台内容信息
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(("请输入" + tip + "："));
        if (scanner.hasNext()) {
            String next = scanner.next();
            if (StrUtil.isNotEmpty(next)) {
                return next;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }
}

