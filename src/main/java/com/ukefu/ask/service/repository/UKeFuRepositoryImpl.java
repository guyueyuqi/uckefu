package com.ukefu.ask.service.repository;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import com.ukefu.ask.util.query.UKAggTopResultExtractor;
import com.ukefu.ask.util.query.UKResultMapper;
import com.ukefu.ask.web.model.Message;

@Component
public class UKeFuRepositoryImpl implements UKeFuEsCommonRepository{
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Autowired
	public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
		this.elasticsearchTemplate = elasticsearchTemplate;
        if(!elasticsearchTemplate.indexExists("uckefu")){
        	elasticsearchTemplate.createIndex("uckefu") ;
        }
        if(!elasticsearchTemplate.typeExists("uckefu" , "uc_ask_message")){
        	elasticsearchTemplate.putMapping(Message.class) ;
        }
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public FacetedPage<Serializable> findByCon(Class clazz,NativeSearchQueryBuilder searchQueryBuilder,  String q , final int p , final int ps) {
		FacetedPage<Serializable> pages  = null ;
		if(!StringUtils.isBlank(q)){
		   	searchQueryBuilder.withQuery(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND)) ;
		}
    	SearchQuery searchQuery = searchQueryBuilder.build();
	    if(elasticsearchTemplate.indexExists(clazz)){
	    	if(!StringUtils.isBlank(q)){
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, clazz, new UKResultMapper());
	    	}else{
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, clazz , new UKAggTopResultExtractor("userid" , "top"));
	    	}
	    }
	    return pages ; 
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void deleteByQuery(QueryBuilder query , Class type) {
		DeleteQuery deleteQuery = new DeleteQuery();
    	deleteQuery.setQuery(query);
		elasticsearchTemplate.delete(deleteQuery, type);
		elasticsearchTemplate.refresh(type, false);
	}

	@Override
	public FacetedPage<Message> findByUserid(String userid , int p , int ps) {
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder() ;
		searchQueryBuilder.withQuery(termQuery("userid" , userid)) ;
		searchQueryBuilder.withPageable(new PageRequest(p, ps)) ;
		return elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), Message.class) ;
	}
}
