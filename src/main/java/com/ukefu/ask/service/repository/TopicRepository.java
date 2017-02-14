package com.ukefu.ask.service.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ukefu.ask.web.model.Topic;

public interface TopicRepository extends  ElasticsearchRepository<Topic, String> , TopicEsCommonRepository {
	
}
