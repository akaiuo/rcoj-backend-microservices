package com.whoj.whojbackendmodel.model.dto.questionsubmit;

import com.whoj.whojbackcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {
    /**
     * 提交语言
     */
    private String lang;

    /**
     * 提交状态
     */
    private Integer status;

    /**
     * 题目id
     */
    private String questionId;

    /**
     * 用户id 或 用户名
     */
    private String user;

    private static final long serialVersionUID = 1L;
}
