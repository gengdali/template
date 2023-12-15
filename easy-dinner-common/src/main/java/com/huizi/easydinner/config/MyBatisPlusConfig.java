package com.huizi.easydinner.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-plus
 * 分页插件配置
 */
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //添加分页插件
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
        //设置请求的页面大于最大页的操作，true调回首页，false继续请求，默认是false
        pageInterceptor.setOverflow(false);
        //单页分页的条数限制，默认五限制
        pageInterceptor.setMaxLimit(500L);
        //设置数据库类型
        pageInterceptor.setDbType(DbType.MYSQL);

        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }
}
