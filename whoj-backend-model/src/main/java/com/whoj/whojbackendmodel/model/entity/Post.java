package com.whoj.whojbackendmodel.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "post")
@Data
@Builder
public class Post implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String content;
    private String title;
    private String preview;
    private String tags;
    private Integer editorType;
    private Long userId;
    private Integer thumbNum;
    private Integer favourNum;
    private Integer commentNum;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
