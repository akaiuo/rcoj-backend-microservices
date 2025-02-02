package com.whoj.whojbackendmodel.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 判题用例（json 对象数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConf judgeConf;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目参考答案
     */
    private String answer;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}