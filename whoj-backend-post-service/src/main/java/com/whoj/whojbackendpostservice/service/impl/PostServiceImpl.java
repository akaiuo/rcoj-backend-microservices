package com.whoj.whojbackendpostservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whoj.whojbackcommon.common.ErrorCode;
import com.whoj.whojbackcommon.constant.CommonConstant;
import com.whoj.whojbackcommon.exception.BusinessException;
import com.whoj.whojbackcommon.utils.SqlUtils;
import com.whoj.whojbackendmodel.model.dto.post.PostQueryRequest;
import com.whoj.whojbackendmodel.model.entity.*;
import com.whoj.whojbackendmodel.model.vo.PostGetVO;
import com.whoj.whojbackendmodel.model.vo.UserVO;
import com.whoj.whojbackendpostservice.mapper.PostFavourMapper;
import com.whoj.whojbackendpostservice.mapper.PostMapper;
import com.whoj.whojbackendpostservice.mapper.PostStarMapper;
import com.whoj.whojbackendpostservice.service.PostService;
import com.whoj.whojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostStarMapper postStarMapper;

    @Resource
    private PostFavourMapper postFavourMapper;

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
    public PostGetVO getPostGetVO(Post post, HttpServletRequest request) {
        PostGetVO postGetVO = BeanUtil.copyProperties(post, PostGetVO.class);
        postGetVO.setTags(JSONUtil.toList(post.getTags(), String.class));
        User user = userFeignClient.getById(post.getUserId());
        UserVO userVO = userFeignClient.getUserVO(user);
        postGetVO.setUserVO(userVO);
        postGetVO.setPreview(null);
        // 当前用户是否点赞或收藏
        User loginUser;
        try {
            loginUser = userFeignClient.getLoginUser(request);
        }catch (BusinessException ignore) {
            loginUser = null;
        }
        if (loginUser != null) {
            QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>();
            postFavourQueryWrapper.eq("userId", loginUser.getId());
            postFavourQueryWrapper.eq("postId", post.getId());
            PostFavour postFavour = postFavourMapper.selectOne(postFavourQueryWrapper);
            if (postFavour != null) {
                postGetVO.setHasFavour(1);
            }
            QueryWrapper<PostStar> postStarQueryWrapper = new QueryWrapper<>();
            postStarQueryWrapper.eq("userId", loginUser.getId());
            postStarQueryWrapper.eq("postId", post.getId());
            PostStar postStar = postStarMapper.selectOne(postStarQueryWrapper);
            if (postStar != null) {
                postGetVO.setHasStar(1);
            }
        }
        return postGetVO;
    }

    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }

        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        List<String> tags = postQueryRequest.getTags();
        Date createAfter = postQueryRequest.getCreateAfter();
        Date createBefore = postQueryRequest.getCreateBefore();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ge(createAfter != null, "createTime", createAfter);
        queryWrapper.le(createBefore != null, "createTime", createBefore);
        queryWrapper.eq("isDelete", 0);
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

    /**
     * 帖子点赞
     * @param postId
     * @param request
     * @return
     */
    @Override
    public boolean favourPost(Long postId, HttpServletRequest request) {
        QueryWrapper<PostFavour> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("postId", postId);
        PostFavour postFavour = postFavourMapper.selectOne(queryWrapper);
        if (postFavour != null) {
            return false;
        }
        postFavour = PostFavour.builder()
                .userId(loginUser.getId())
                .postId(postId)
                .build();
        postFavourMapper.insert(postFavour);
        // 帖子点赞数+1
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        post.setFavourNum(post.getFavourNum() + 1);
        postMapper.updateById(post);
        return true;
    }

    /**
     * 取消帖子点赞
     * @param postId
     * @param request
     * @return
     */
    @Override
    public boolean cancelFavourPost(Long postId, HttpServletRequest request) {
        QueryWrapper<PostFavour> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("postId", postId);
        int i = postFavourMapper.delete(queryWrapper);
        if (i == 0) {
            return false;
        }
        // 评论点赞数-1
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        post.setFavourNum(post.getFavourNum() - 1);
        postMapper.updateById(post);
        return true;
    }

    /**
     * 帖子收藏
     * @param postId
     * @param request
     * @return
     */
    @Override
    public boolean starPost(Long postId, HttpServletRequest request) {
        QueryWrapper<PostStar> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("postId", postId);
        PostStar postStar = postStarMapper.selectOne(queryWrapper);
        if (postStar != null) {
            return false;
        }
        postStar = PostStar.builder()
                .userId(loginUser.getId())
                .postId(postId)
                .build();
        postStarMapper.insert(postStar);
        // 帖子点赞数+1
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        post.setStarNum(post.getStarNum() + 1);
        postMapper.updateById(post);
        return true;
    }

    /**
     * 取消帖子收藏
     * @param postId
     * @param request
     * @return
     */
    @Override
    public boolean cancelStarPost(Long postId, HttpServletRequest request) {
        QueryWrapper<PostStar> queryWrapper = new QueryWrapper<>();
        User loginUser = userFeignClient.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("postId", postId);
        int i = postStarMapper.delete(queryWrapper);
        if (i == 0) {
            return false;
        }
        // 评论点赞数-1
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);        }
        post.setStarNum(post.getStarNum() - 1);
        postMapper.updateById(post);
        return true;
    }

    /**
     * 问题id（Page） -> 帖子id -> 帖子Vo（List） -> 帖子Vo（Page）
     * @param solutionPostPage
     * @return
     */
    @Override
    public Page<PostGetVO> getPostSolutionVOPage(Page<QuestionSolutionPost> solutionPostPage) {
        Page<Post> postPage = new Page<>(solutionPostPage.getCurrent(), solutionPostPage.getSize());
        List<Post> postList = new ArrayList<>();
        // 根据分页中的各条帖子对应id，查出每条帖子
        solutionPostPage.getRecords().forEach(questionSolutionPost -> {
            Post post = postMapper.selectById(questionSolutionPost.getPostId());
            if (post != null) {
                postList.add(post);
            }
        });
        postPage.setRecords(postList);
        postPage.setTotal(postList.size());
        return getPostSubmitVOPage(postPage);
    }
}
