package com.ukefu.ask.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ukefu.ask.web.model.Fans;

/**
 * 
 * @author admin
 *
 */
public interface FansRepository extends PagingAndSortingRepository<Fans, String> {

	Fans findByUserAndCreater(String user , String creater);
	
	int countByCreater(String creater) ;
	
	int countByUser(String user) ;
	
	Page<Fans> findByCreater(String creater , Pageable pageable);
	
	Page<Fans> findByUser(String user , Pageable pageable) ;
    
}
