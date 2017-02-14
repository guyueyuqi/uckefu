package com.ukefu.ask.web.handler.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.ask.service.repository.UserRepository;
import com.ukefu.ask.web.handler.Handler;
import com.ukefu.util.Menu;

@Controller
public class AdminController extends Handler{
	
	@Autowired
	private UserRepository userRepository;
	
    @RequestMapping("/admin")
    @Menu(type = "admin" , subtype = "content" , access = false , admin = true)
    public ModelAndView index(HttpServletRequest request) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/admin/index"));
        return view;
    }
    
    @RequestMapping("/admin/content")
    @Menu(type = "admin" , subtype = "content" , access = false , admin = true)
    public ModelAndView content(ModelMap map) {
    	return request(super.createAdminTempletResponse("/admin/content"));
    }

}