package com.ukefu.ask.service.repository;

import java.io.Serializable;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.ukefu.ask.web.model.Message;


public interface UKeFuEsCommonRepository {
	@SuppressWarnings("rawtypes")
	public FacetedPage<Serializable> findByCon(Class clazz,NativeSearchQueryBuilder searchQueryBuilder, String q, int p,int ps);
	
	@SuppressWarnings("rawtypes")
	public void deleteByQuery(QueryBuilder query , Class type) ;
	
	public FacetedPage<Message> findByUserid(String userid, int p, int ps);
}
