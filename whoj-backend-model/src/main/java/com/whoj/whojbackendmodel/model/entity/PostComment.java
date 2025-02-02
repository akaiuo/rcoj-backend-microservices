package com.whoj.whojbackendmodel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@TableName("post_comment")
@Data
@Builder
public class PostComment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String content;
    private Long postId;
    private Integer thumbNum;
    private Integer replyNum;
    private Long userId;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
