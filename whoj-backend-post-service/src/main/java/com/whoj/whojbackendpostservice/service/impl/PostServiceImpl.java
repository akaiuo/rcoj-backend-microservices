package com.whoj.whojbackendpostservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.constant.CommonConstant;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackcommon.utils.SqlUtils;
import com.whoj.whojbackendmodel.model.dto.post.PostQueryRequest;
import com.whoj.whojbackendmodel.model.entity.Post;
import com.whoj.whojbackendmodel.model.entity.User;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;
import com.whoj.whojbackendpostservice.mapper.PostMapper;
import com.whoj.whojbackendpostservice.service.PostService;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public void validPost(Post post) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = post.getContent();
        String title = post.getTitle();
        String preview = post.getPreview();
        Integer editorType = post.getEditorType();
        // 不能存在null值
        if (StringUtils.isAnyBlank(content, title, preview) || editorType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 参数校验
        if (title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (preview.length() > 350) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简述过长");
        }
        if (content.length() > 20000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (editorType != 1 && editorType != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编辑器类型错误");
        }
    }

    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        List<String> tags = postQueryRequest.getTags();
        Date createAfter = postQueryRequest.getCreateAfter();
        Date createBefore = postQueryRequest.getCreateBefore();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        if (tags != null) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ge(createAfter != null, "createTime", createAfter);
        queryWrapper.le(createBefore != null, "createTime", createBefore);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<PostGetVO> getPostSubmitVOPage(Page<Post> postPage) {
        List<Post> records = postPage.getRecords();
        Page<PostGetVO> postGetVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollUtil.isEmpty(records)) {
            return postGetVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = records.stream().map(Post::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<PostGetVO> postGetVOList = records.stream().map(post -> {
            PostGetVO postGetVO = PostGetVO.objToVo(post);
            postGetVO.setContent(null);
            Long userId = post.getUserId();
            User user = userIdUserListMap.get(userId).get(0);
            postGetVO.setUserVO(userFeignClient.getUserVO(user));
            return postGetVO;
        }).toList();
        postGetVOPage.setRecords(postGetVOList);
        return postGetVOPage;
    }
}
