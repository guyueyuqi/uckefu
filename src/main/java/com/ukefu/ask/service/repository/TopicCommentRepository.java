package com.ukefu.ask.service.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ukefu.ask.web.model.TopicComment;

public interface TopicCommentRepository extends  ElasticsearchRepository<TopicComment, String> , TopicCommentEsCommonRepository {
}
