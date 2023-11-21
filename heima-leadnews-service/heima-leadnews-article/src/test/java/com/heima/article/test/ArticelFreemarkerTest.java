package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleSercive;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticelFreemarkerTest {
    @Autowired
    ApArticleContentMapper articleContentMapper;
    @Autowired
    Configuration configuration;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    ApArticleSercive apArticleSercive;
    @Test
    public void createStaticUrlTest() throws IOException, TemplateException {
        //获取文章内容
        LambdaQueryWrapper<ApArticleContent> wrapper = new LambdaQueryWrapper<ApArticleContent>().eq(ApArticleContent::getArticleId, "1713527835690131457");
        ApArticleContent apArticleContent = articleContentMapper.selectOne(wrapper);


//        System.out.println(apArticleContent);
        if(apArticleContent!=null)
        {
            //生成文件
            Template template = configuration.getTemplate("article.ftl");

            HashMap<String, Object> content = new HashMap<>();
            content.put("content", JSONArray.parseArray(apArticleContent.getContent()));
//            System.out.println(JSONArray.parseArray(apArticleContent.getContent()));
            StringWriter out=new StringWriter();
            template.process(content,out);
            //上传至minio
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);
            //修改article表保存static_url字段
            System.out.println(path);
            apArticleSercive.lambdaUpdate().eq(ApArticle::getId,apArticleContent.getArticleId()).set(ApArticle::getStaticUrl,path).update();



        }

    }
    @Test
    public void testGenAll() throws TemplateException, IOException {//获取文章内容


        List<ApArticle> list = apArticleSercive.list();
        for (ApArticle apArticle : list) {
            LambdaQueryWrapper<ApArticleContent> wrapper = new LambdaQueryWrapper<ApArticleContent>().eq(ApArticleContent::getArticleId, apArticle.getId());
            ApArticleContent apArticleContent = articleContentMapper.selectOne(wrapper);
            if(apArticleContent!=null)
            {
                //生成文件
                Template template = configuration.getTemplate("article.ftl");

                HashMap<String, Object> content = new HashMap<>();
                content.put("content", JSONArray.parseArray(apArticleContent.getContent()));
//            System.out.println(JSONArray.parseArray(apArticleContent.getContent()));
                StringWriter out=new StringWriter();
                template.process(content,out);
                //上传至minio
                InputStream in = new ByteArrayInputStream(out.toString().getBytes());
                String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);
                //修改article表保存static_url字段
                System.out.println(path);
                apArticleSercive.lambdaUpdate().eq(ApArticle::getId,apArticleContent.getArticleId()).set(ApArticle::getStaticUrl,path).update();



            }

        }

//        System.out.println(apArticleContent);

    }

}
