package com.huizi.easydinner.component;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @DESCRIPTION:获取所有已经注册的Mapper
 * @AUTHOR: 12615
 * @DATE: 2023/5/11 14:59
 */
@Component
public class MapperRegistryHelper {

    @Autowired
    private SqlSession sqlSession;

    public Map<String, BaseMapper<?>> getAllMappers() {
        MapperRegistry mapperRegistry = sqlSession.getConfiguration().getMapperRegistry();
        Collection<Class<?>> mapperClasses = mapperRegistry.getMappers();
        Map<String, BaseMapper<?>> mapperMap = new HashMap<>();
        for (Class<?> mapperClass : mapperClasses) {
            BaseMapper<?> mapper = (BaseMapper<?>) sqlSession.getMapper(mapperClass);
            String simpleName = mapperClass.getSimpleName();
            String replace = simpleName.replace("Mapper", "");
            mapperMap.put(replace, mapper);
        }
        return mapperMap;
    }
}
