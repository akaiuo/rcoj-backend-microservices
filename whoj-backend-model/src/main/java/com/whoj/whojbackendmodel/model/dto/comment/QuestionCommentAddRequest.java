package com.whoj.whojbackendmodel.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionCommentAddRequest implements Serializable{

    /**
     * 问题id
     */
    private Long questionId;

    /**
     * 评论内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}
