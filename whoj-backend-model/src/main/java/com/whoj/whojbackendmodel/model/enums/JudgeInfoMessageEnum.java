package com.whoj.whojbackendmodel.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题信息消息枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("通过", 0),
    MANY_ERROR("多种错误", -1),
    WRONG_ANSWER("答案错误", 1),
    COMPILE_ERROR("编译错误", 2),
    RUNTIME_ERROR("运行错误", 3),
    TIME_LIMIT_EXCEEDED("超出时间限制", 4),
    MEMORY_LIMIT_EXCEEDED("超出内存限制", 5),
    WAITING("等待中", 6),
    PRESENTATION_ERROR("格式错误", 7),
    OUTPUT_LIMIT_EXCEEDED("输出溢出", 8),
    DANGEROUS_OPERATION("危险操作", 9),
    SYSTEM_ERROR("系统错误", 10);

    private final String text;

    private final Integer value;

    JudgeInfoMessageEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
