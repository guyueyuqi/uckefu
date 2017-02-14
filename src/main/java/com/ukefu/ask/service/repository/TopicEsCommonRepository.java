package com.ukefu.ask.service.repository;

import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.ukefu.ask.web.model.Topic;

public interface TopicEsCommonRepository {
	public FacetedPage<Topic> getTopicByCate(String cate ,String q, int p, int ps) ;
	
	public FacetedPage<Topic> getTopicByCateAndUser(String cate , String q ,String user , int p, int ps) ;
	
	public FacetedPage<Topic> getTopicByCateAndRela(String cate , String field, int p, int ps) ;
	
	public FacetedPage<Topic> findByCon(NativeSearchQueryBuilder searchQueryBuilder, String q, int p,int ps);
}
