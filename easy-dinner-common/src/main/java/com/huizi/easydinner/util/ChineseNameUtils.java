package com.huizi.easydinner.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @DESCRIPTION:实体类属性名称根据swagger注释转汉字
 * @AUTHOR: 12615
 * @DATE: 2023/6/27 10:03
 */
public class ChineseNameUtils {
    public static String toJsonString(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // 将实体类转换成 JSON 格式的字符串
        String jsonString = mapper.writeValueAsString(obj);

        // 将 JSON 格式的字符串中的属性名称替换成对应的汉字
        Map<String, String> map = getPropertyChineseNameMap(obj.getClass());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonString = jsonString.replaceAll("\"" + entry.getKey() + "\"", "\"" + entry.getValue() + "\"");
        }

        return jsonString;
    }

    public static Map<String, String> getPropertyChineseNameMap(Class<?> clazz) {
        Map<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
            if (apiModelProperty != null) {
                map.put(field.getName(), apiModelProperty.value());
            }
        }
        return map;
    }
}
