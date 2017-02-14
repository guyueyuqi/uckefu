package com.ukefu.ask.service.repository;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

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

import com.ukefu.ask.util.query.UKResultMapper;
import com.ukefu.ask.web.model.Topic;

@Component
public class TopicRepositoryImpl implements TopicEsCommonRepository{
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        if(!elasticsearchTemplate.indexExists(Topic.class)){
        	elasticsearchTemplate.createIndex(Topic.class) ;
        }
        if(!elasticsearchTemplate.typeExists("uckefu" , "uc_ask_topic")){
        	elasticsearchTemplate.putMapping(Topic.class) ;
        }
    }
	@Override
	public FacetedPage<Topic> getTopicByCate(String cate , String q, final int p , final int ps) {

		FacetedPage<Topic> pages  = null ;
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(termQuery("cate" , cate)).withSort(new FieldSortBuilder("top").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC));
	    if(!StringUtils.isBlank(q)){
	    	searchQueryBuilder.withQuery(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND)) ;
	    }
	    searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title").fragmentSize(200)) ;
	    SearchQuery searchQuery = searchQueryBuilder.build().setPageable(new PageRequest(p, ps)) ;
	    if(elasticsearchTemplate.indexExists(Topic.class)){
	    	if(!StringUtils.isBlank(q)){
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, Topic.class , new UKResultMapper());
	    	}else{
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, Topic.class);
	    	}
	    }
	    return pages ; 
	}
	
	@Override
	public FacetedPage<Topic> getTopicByCateAndUser(String cate  , String q , String user ,final int p , final int ps) {

		FacetedPage<Topic> pages  = null ;
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(termQuery("cate" , cate)).withQuery(termQuery("creater" , user)).withSort(new FieldSortBuilder("top").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC));
		
		if(!StringUtils.isBlank(q)){
		   	searchQueryBuilder.withQuery(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND)) ;
		}
		SearchQuery searchQuery = searchQueryBuilder.build().setPageable(new PageRequest(p, ps));
		if(elasticsearchTemplate.indexExists(Topic.class)){
	    	
			if(!StringUtils.isBlank(q)){
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, Topic.class, new UKResultMapper());
	    	}else{
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, Topic.class);
	    	}
	    }
	    return pages ; 
	}
	
	@Override
	public FacetedPage<Topic> findByCon(NativeSearchQueryBuilder searchQueryBuilder,  String q , final int p , final int ps) {
		FacetedPage<Topic> pages  = null ;
		if(!StringUtils.isBlank(q)){
		   	searchQueryBuilder.withQuery(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND)) ;
		}
    	SearchQuery searchQuery = searchQueryBuilder.build().setPageable(new PageRequest(p, ps));
	    if(elasticsearchTemplate.indexExists(Topic.class)){
	    	if(!StringUtils.isBlank(q)){
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, Topic.class  , new UKResultMapper());
	    	}else{
	    		pages = elasticsearchTemplate.queryForPage(searchQuery, Topic.class);
	    	}
	    }
	    return pages ; 
	}
	
	@Override
	public FacetedPage<Topic> getTopicByCateAndRela(String cate ,String field , int p, int ps) {

		FacetedPage<Topic> pages  = null ;
	    SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("cate" , cate)).withSort(new FieldSortBuilder(field).unmappedType("integer").order(SortOrder.DESC)).build().setPageable(new PageRequest(p, ps));
	    if(elasticsearchTemplate.indexExists(Topic.class)){
	    	pages = elasticsearchTemplate.queryForPage(searchQuery, Topic.class);
	    }
	    return pages ; 
	}
}
