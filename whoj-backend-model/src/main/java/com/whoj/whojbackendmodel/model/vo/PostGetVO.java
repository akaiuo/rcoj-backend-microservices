package com.whoj.whojbackendmodel.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.whoj.whojbackendmodel.model.entity.Post;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class PostGetVO implements Serializable {

    private Long id;
    private String content;
    private String title;
    private String preview;
    private List<String> tags;
    private Integer editorType;
    private UserVO userVO;
    private Integer favourNum;
    private Integer starNum;
    private Integer commentNum;
    private Integer hasFavour;
    private Integer hasStar;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;

    public static PostGetVO objToVo(Post post) {
        PostGetVO postGetVO = new PostGetVO();
        BeanUtil.copyProperties(post, postGetVO);
        return postGetVO;
    }

    private static final long serialVersionUID = 1L;
}
