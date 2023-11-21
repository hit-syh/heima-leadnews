package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.ApAuthMapper;
import com.heima.article.service.ApArticleSercive;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.article.vos.ArticleUserInfoVo;
import com.heima.model.behavior.constants.RedisPrefixConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleSercive {
    @Autowired ApArticleMapper apArticleMapper;
    @Override
    public ResponseResult load(ArticleHomeDto dto, short type) {
        //校验参数
        if(dto.getSize()==null ||dto.getSize()==0)
            dto.setSize(10);
        if(StringUtils.isBlank(dto.getTag()))
        {
            dto.setTag("__all__");
        }

        List<ApArticle> apArticleList = apArticleMapper.loadArticleList(dto, type);
        return ResponseResult.okResult(apArticleList);

    }
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private ApAuthMapper apAuthMapper;
    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;
    @Autowired
    private CacheService cacheService;
    @Override
    public ResponseResult savaArticle(ArticleDto dto) {
        //1.检查参数
        if(dto==null)
        {
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApAuthor apAuthor = apAuthMapper.selectOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getWmUserId, dto.getAuthorId()));
        if(apAuthor==null)
        {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"作者移动端不存在用户");
        }
        dto.setAuthorId(apAuthor.getUserId().longValue());
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto,apArticle);
        //2.判断是否存在id
        //2.1 不存在id  保存  文章  文章配置  文章内容
        if(dto.getId()==null)
        {

            save(apArticle);

            //保存配置
            ApArticleConfig articleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(articleConfig);

            //保存内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);

        }

        //2.2 存在id   修改  文章  文章内容
        else
        {
            //修改文章
            updateById(apArticle);

            //修改内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, apArticle.getId()));
            apArticleContentMapper.updateById(apArticleContent);
        }

        //异步调用生成静态html并加入es
        articleFreemarkerService.buildMinIO(apArticle,dto.getContent());
        return ResponseResult.okResult(apArticle.getId());
    }

    @Override
    public ResponseResult load_article_behavior(ArticleInfoDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        if(user==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH,"没登陆");
        if(dto.getArticleId()==null ||dto.getAuthorId()==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        ArticleUserInfoVo articleUserInfoVo = new ArticleUserInfoVo();
        //加载点赞
        if(cacheService.hExists(RedisPrefixConstants.LIKES_ARTICLE+dto.getArticleId(),user.getId().toString()))
            articleUserInfoVo.setIslike(true);
        if(cacheService.hExists(RedisPrefixConstants.UNLIKES_ARTICLE+dto.getArticleId(),user.getId().toString()))
            articleUserInfoVo.setIsunlike(true);
        //收藏
        if(cacheService.hExists(RedisPrefixConstants.ARTICLE_COLLECTION+dto.getArticleId(), String.valueOf(user.getId())))
            articleUserInfoVo.setIscollection(true);
        if(cacheService.hExists(RedisPrefixConstants.FOLLOW_AUTHOR+user.getId(), String.valueOf(dto.getAuthorId()))
                && cacheService.hExists(RedisPrefixConstants.AUTHOR_FANS+dto.getAuthorId(), String.valueOf(user.getId())))
            articleUserInfoVo.setIsfollow(true);
        return ResponseResult.okResult(articleUserInfoVo);

    }
}
