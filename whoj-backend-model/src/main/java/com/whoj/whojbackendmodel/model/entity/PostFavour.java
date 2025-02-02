package com.whoj.whojbackendmodel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("post_favour")
@Data
@Builder
public class PostFavour implements Serializable {

  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  private Long postId;
  private Long userId;
  private Date createTime;
  private Date updateTime;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
