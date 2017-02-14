package com.ukefu.ask.service.repository;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import com.ukefu.ask.web.model.TopicView;

@Component
public class TopicViewRepositoryImpl implements TopicViewEsCommonRepository{
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
		this.elasticsearchTemplate = elasticsearchTemplate;
        if(!elasticsearchTemplate.indexExists("uckefu")){
        	elasticsearchTemplate.createIndex("uckefu") ;
        }
        if(!elasticsearchTemplate.typeExists("uckefu" , "uc_ask_topicview")){
        	elasticsearchTemplate.putMapping(TopicView.class) ;
        }
    }
	@Override
	public long findByIpcode(String id , String ip , String optype) {
		long views = 0 ;
		if(elasticsearchTemplate.indexExists(TopicView.class)){
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(termQuery("ipcode" , ip)).must(termQuery("optype" , optype)).must(termQuery("dataid" , id)) ;
			views = elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build() , TopicView.class) ;
		}
		return views;
	}
}
