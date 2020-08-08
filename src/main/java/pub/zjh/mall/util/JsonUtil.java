package pub.zjh.mall.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pub.zjh.mall.pojo.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(ALWAYS);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
        }
        return null;
    }

    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error", e);
        }
        return null;
    }

    public static <T> T string2Obj(String str, Class<T> tClass) {
        if (StringUtils.isEmpty(str) || tClass == null) {
            return null;
        }
        try {
            return tClass.equals(String.class) ? (T) str : objectMapper.readValue(str, tClass);
        } catch (IOException e) {
            log.warn("Parse String to Object error", e);
        }
        return null;
    }

    public static <T> T string2Obj(String str, TypeReference typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return typeReference.getType().equals(String.class) ? (T) str : objectMapper.readValue(str, typeReference);
        } catch (IOException e) {
            log.warn("Parse String to Object error", e);
        }
        return null;
    }

    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error", e);
        }
        return null;
    }


    public static void main(String[] args) {
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("zjh");
        User user2 = new User();
        user2.setId(2);
        user2.setEmail("zjh2");

        List<User> userList = Lists.newArrayList();
        userList.add(user1);
        userList.add(user2);

        String userListStr = obj2StringPretty(userList);
        log.info("userListStr:{}", userListStr);

        TypeReference typeReference = new TypeReference<List<User>>() {
        };
        List<User> userList1 = string2Obj(userListStr, typeReference);
        log.info("userList1:{}", userList1);

        List<User> userList2 = string2Obj(userListStr, List.class, User.class);
        log.info("userList2:{}", userList2);
    }


}
