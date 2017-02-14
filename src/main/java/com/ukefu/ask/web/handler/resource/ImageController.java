package com.ukefu.ask.web.handler.resource;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ukefu.ask.web.handler.Handler;
import com.ukefu.util.Menu;

@Controller
@RequestMapping("/res")
public class ImageController extends Handler{
	
	@Value("${web.upload-path}")
    private String path;
	
    @RequestMapping("/image/{id}")
    @Menu(type = "resouce" , subtype = "image" , access = true)
    public void index(HttpServletResponse response, @PathVariable String id) throws IOException {
    	File avatar = new File(path ,id) ;
    	if(avatar.exists() && !StringUtils.isBlank(id)){
    		response.getOutputStream().write(FileUtils.readFileToByteArray(new File(path ,id)));
    	}else{
    		response.sendRedirect("/images/user/default.png");
    	}
    	return ;
    }
    
}