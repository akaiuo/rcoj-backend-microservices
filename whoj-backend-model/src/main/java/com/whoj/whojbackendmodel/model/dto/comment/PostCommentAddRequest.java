package com.whoj.whojbackendmodel.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostCommentAddRequest implements Serializable{

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 评论内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}
