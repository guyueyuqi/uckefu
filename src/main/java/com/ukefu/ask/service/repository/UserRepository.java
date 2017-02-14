package com.ukefu.ask.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.ask.web.model.User;

/**
 * 
 * @author admin
 *
 */
public interface UserRepository extends JpaRepository<User, String> {

    User findById(String id);

    User findByUsername(String username);
    
    User findByEmail(String email);
    
    User findByUsernameOrEmail(String username , String email);
    
    Page<User> findAll(Pageable pageable);
    
    Page<User> findByUsertype(String usertype,Pageable pageable);
    
    List<User> findBySkill(String skill) ;

}
