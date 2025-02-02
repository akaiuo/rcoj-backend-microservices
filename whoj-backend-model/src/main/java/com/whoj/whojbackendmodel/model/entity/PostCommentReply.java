package com.whoj.whojbackendmodel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("post_comment_reply")
@Data
@Builder
public class PostCommentReply implements Serializable {

  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  private Long commentId;
  private Long userId;
  private String content;
  private Date createTime;
  private Date updateTime;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
