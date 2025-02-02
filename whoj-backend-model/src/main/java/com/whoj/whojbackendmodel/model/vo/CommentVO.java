package com.whoj.whojbackendmodel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 是否点赞
     */
    private Integer hasThumb;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建帖子人信息
     */
    private UserVO userVO;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
