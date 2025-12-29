package com.whoj.whojbackendmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.whoj.whojbackendmodel.model.dto.question.JudgeConf;
import com.whoj.whojbackendmodel.model.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 题目封装类
 * @TableName question
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConf judgeConf;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 提交数
     */
    private Integer submitNum;

    /**
     * 通过数
     */
    private Integer acceptNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建题目人的信息
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConf voJudgeConf = questionVO.getJudgeConf();
        if (voJudgeConf != null) {
            question.setJudgeConf(JSONUtil.toJsonStr(voJudgeConf));
        }

        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        questionVO.setJudgeConf(JSONUtil.toBean(question.getJudgeConf(), JudgeConf.class));
        return questionVO;
    }

    private static final long serialVersionUID = 1L;
}