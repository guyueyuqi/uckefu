package com.ukefu.ask.service.repository;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import com.ukefu.ask.util.query.UKAggTopResultExtractor;
import com.ukefu.ask.util.query.UKResultMapper;
import com.ukefu.ask.web.model.TopicComment;

@Component
public class TopicCommentRepositoryImpl implements TopicCommentEsCommonRepository{
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Autowired
	public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
		this.elasticsearchTemplate = elasticsearchTemplate;
        if(!elasticsearchTemplate.indexExists("uckefu")){
        	elasticsearchTemplate.createIndex("uckefu") ;
        }
        if(!elasticsearchTemplate.typeExists("uckefu" , "uc_ask_topiccomment")){
        	elasticsearchTemplate.putMapping(TopicComment.class) ;
        }
    }
	@Override
	public FacetedPage<TopicComment> findByDataid(String id , int p , int ps) {
		FacetedPage<TopicComment> pages  = null ;
	    SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("dataid" , id)).withSort(new FieldSortBuilder("optimal").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC)).build().setPageable(new PageRequest(p, ps)) ;
	    if(elasticsearchTemplate.indexExists(TopicComment.class)){
	    	pages = elasticsearchTemplate.queryForPage(searchQuery, TopicComment.class);
	    }
	    return pages ; 
	}
	
	@Override
	public List<TopicComment> findByOptimal(String dataid) {
		List<TopicComment> commentList  = null ;
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("dataid" , dataid)).withQuery(termQuery("optimal" , true)).build();
	    if(elasticsearchTemplate.indexExists(TopicComment.class)){
	    	commentList = elasticsearchTemplate.queryForList(searchQuery, TopicComment.class);
	    }
	    return commentList ;
	}
	
	@Override
	public FacetedPage<TopicComment> findByCon(NativeSearchQueryBuilder searchQueryBuilder , String field , String aggname,  String q , final int p , final int ps) {
		FacetedPage<TopicComment> pages  = null ;
		if(!StringUtils.isBlank(q)){
		   	searchQueryBuilder.withQuery(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND)) ;
		}
    	SearchQuery searchQuery = searchQueryBuilder.build();
	    if(elasticsearchTemplate.indexExists(TopicComment.class)){
	    	if(!StringUtils.isBlank(q)){
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, TopicComment.class  , new UKResultMapper());
	    	}else{
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, TopicComment.class , new UKAggTopResultExtractor(field , aggname));
	    	}
	    }
	    return pages ; 
	}
	@Override
	public FacetedPage<TopicComment> findByCon(
			NativeSearchQueryBuilder searchQueryBuilder, String q, int p, int ps) {
		searchQueryBuilder.withPageable(new PageRequest(p, ps)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC)) ;
		searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("content").fragmentSize(200)) ;
		if(!StringUtils.isBlank(q)){
		   	searchQueryBuilder.withQuery(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND)) ;
		}
	    return elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), TopicComment.class  , new UKResultMapper()) ;
	}
}
