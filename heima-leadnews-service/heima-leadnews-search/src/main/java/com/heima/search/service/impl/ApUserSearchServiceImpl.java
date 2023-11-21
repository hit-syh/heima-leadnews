package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.HistorySearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.thread.AppThreadLocalUtils;
import org.apache.hadoop.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void insert(String keword, Integer userId) {
        //1.搜索当前关键词
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keword));
        ApUserSearch apUserSearch = mongoTemplate.findOne(query, ApUserSearch.class);
        //2.存在 更新
        if(apUserSearch!=null)
        {
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
            return;
        }
        //3.不存在 判断数量超过10
        apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keword);
        apUserSearch.setCreatedTime(new Date());
        query=Query.query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.DESC,"createdTime"));
        List<ApUserSearch> apUserSearchList = mongoTemplate.find(query, ApUserSearch.class);
        if(apUserSearchList==null || apUserSearchList.size()<10)
        {
            mongoTemplate.save(apUserSearch);
        }
        else
        {
            ApUserSearch lastUserSearch = apUserSearchList.get(apUserSearchList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getId())),apUserSearch);

        }


    }

    @Override
    public ResponseResult load() {

        ApUser user = AppThreadLocalUtils.getUser();
        Query query = Query.query(Criteria.where("userId").is(user.getId())).with(Sort.by(Sort.Direction.DESC,"createdTime"));

        List<ApUserSearch> apUserSearches = mongoTemplate.find(query, ApUserSearch.class);
        return ResponseResult.okResult(apUserSearches);
    }

    @Override
    public ResponseResult del(HistorySearchDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        Query query = Query.query(Criteria.where("userId").is(user.getId()).and("id").is(dto.getId()));
        ApUserSearch one = mongoTemplate.findOne(query, ApUserSearch.class);
        if(one==null)
        {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        else
        {
            mongoTemplate.remove(one);
            return ResponseResult.okResult("删除成功");
        }

    }
}
