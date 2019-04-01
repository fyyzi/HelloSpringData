package com.fyyzi.common.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Json格式化工具类
 */
@Slf4j
public class JsonFormatUtils {

    /** 私有化构造器,禁止别人创建 */
    private JsonFormatUtils() {
    }

    private static final Gson GSON = new Gson();

    /**
     * 将Object转换为Json字符串
     *
     * @param obj {@link Object} 将会被转换为Json的Object对象
     * @return String 格式 的 Json
     */
    public static String objectToJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将Json串转换为Object
     *
     * @param json  json 将转换为对象的json {@link String}
     * @param clazz {@link Class} 该json将被转换为什么类型的对象
     * @return {@link T} 返回对应{@link Class<T>}类型的对象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 将Object串转换为指定类型的Object
     *
     * @param object object
     * @param clazz  {@link Class}
     * @return {@link T}
     */
    public static <T> T jsonToObject(Object object, Class<T> clazz) {
        String s = objectToJson(object);
        return jsonToObject(s, clazz);
    }

    /**
     * Json转List集合
     *
     * @param json  json
     * @param clazz {@link Class}
     * @param <T>   List集合的泛型
     * @return List<T> {@link ArrayList<T>}
     */
    public static <T> List<T> jsonToObjectList(String json, Class<T> clazz) {
        List<T> ts = new ArrayList<>();
        List<String> bodyStrList = jsonToObject(json, ArrayList.class);
        for (int i = 0; i < bodyStrList.size(); i++) {
            String o = bodyStrList.get(i);
            T t = jsonToObject(o, clazz);
            ts.add(t);
        }
        return ts;
    }
}
