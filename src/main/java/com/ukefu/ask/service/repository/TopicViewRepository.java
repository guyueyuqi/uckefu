package com.ukefu.ask.service.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ukefu.ask.web.model.TopicView;

public interface TopicViewRepository extends  ElasticsearchRepository<TopicView, String> , TopicViewEsCommonRepository {
}
