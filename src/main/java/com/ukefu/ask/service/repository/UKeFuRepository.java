package com.ukefu.ask.service.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ukefu.ask.web.model.Message;

/**
 * 
 * @author admin
 *
 */
public interface UKeFuRepository extends ElasticsearchRepository<Message, String> , UKeFuEsCommonRepository {
	
}
