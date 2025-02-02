package com.whoj.whojbackendmodel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@TableName(value = "question_comment_thumb")
@Data
@Builder
public class QuestionCommentThumb implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long commentId;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
