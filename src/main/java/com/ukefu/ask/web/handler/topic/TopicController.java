package com.ukefu.ask.web.handler.topic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.ask.service.repository.TopicCommentRepository;
import com.ukefu.ask.service.repository.TopicRepository;
import com.ukefu.ask.service.repository.TopicViewRepository;
import com.ukefu.ask.service.repository.UserRepository;
import com.ukefu.ask.web.handler.Handler;
import com.ukefu.ask.web.model.Topic;
import com.ukefu.ask.web.model.TopicComment;
import com.ukefu.ask.web.model.TopicView;
import com.ukefu.ask.web.model.User;
import com.ukefu.core.UKDataContext;
import com.ukefu.util.IP;
import com.ukefu.util.IPTools;
import com.ukefu.util.Menu;
import com.ukefu.util.UKTools;

@Controller
@RequestMapping("/topic")
public class TopicController extends Handler{
	
	@Autowired
    private TopicRepository topicRes;
	
	@Autowired
	private UserRepository userRes ;
	
	@Autowired
	private TopicViewRepository topicViewRes ;
	
	@Autowired
	private TopicCommentRepository topicCommentRes ;

    @RequestMapping("/detail/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView index(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/topic/detail")) ;
    	if(!StringUtils.isBlank(id)){
    		Topic topic = topicRes.findOne(id) ;
    		if(topic != null){
	    		String ip = request.getRemoteAddr();
	    		long data = UKTools.ipToLong(ip) ;
	    		long views = topicViewRes.findByIpcode(id , String.valueOf(data) , "views") ;
	    		
	    		if(views == 0){	//以下代码记录 topic 的阅读用户信息，包括用户名。阅读时间，用户IP信息 ， 每个IP只记录一次
		    		topic.setViews(topic.getViews()+1);
		    		topicRes.save(topic) ;
		    		
		    		User user = super.getUser(request) ;
		    		
		    		TopicView topicView = new TopicView();
		    		topicView.setCratetime(new Date());
		    		topicView.setDataid(id);
		    		topicView.setOptype(UKDataContext.AskOperatorType.VIEWS.toString());
		    		topicView.setIpcode(String.valueOf(data));
		    		topicView.setUsername(user.getUsername());
		    		
		    		
		    		IP ipdata = IPTools.getInstance().findGeography(request.getRemoteAddr()) ;
		    		
		    		topicView.setCountry(ipdata.getCountry());
		    		topicView.setProvince(ipdata.getProvince());
		    		topicView.setCity(ipdata.getCity());
		    		topicView.setRegion(ipdata.toString()+"["+ip+"]");
		    		
		    		topicViewRes.save(topicView) ;
	    		}
	    		view.addObject("topicUser",userRes.findById(topic.getCreater()) ) ;
	    		
	    		view.addObject("topic",topic) ;
	    		
	    		/**
	    		 * 
	    		 */
	    		view.addObject("topicCommentList", processCreater(topicCommentRes.findByDataid(id , super.getP(request) , super.getPs(request)))) ;
	    		
	    		view.addObject("relaAnswersList", topicRes.getTopicByCateAndRela(UKDataContext.AskSectionType.DEFAULT.toString() , "answers" , super.getP(request) , 15)) ;
	    		
	    		view.addObject("relaViewsList", topicRes.getTopicByCateAndRela(UKDataContext.AskSectionType.DEFAULT.toString() , "views" , super.getP(request) , 15)) ;
    		}
    	}
        return view;
    }
    @RequestMapping("/add")
    @Menu(type = "apps" , subtype = "topic" , access = false)
    public ModelAndView add(HttpServletRequest request , HttpServletResponse response) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/topic/add")) ; 
        return view;
    }
    @RequestMapping("/edit/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = false)
    public ModelAndView edit(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/topic/edit")) ; 
    	view.addObject("topic", topicRes.findOne(id)) ;
        return view;
    }
    @RequestMapping("/update/{topicid}")
    @Menu(type = "apps" , subtype = "topic" , access = false)
    public ModelAndView update(HttpServletRequest request , HttpServletResponse response , @PathVariable String topicid, @Valid Topic topic) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{topicid}.html")) ;
    	Topic otopic = topicRes.findOne(topicid) ;
    	if(otopic!=null && topic !=null){
    		otopic.setTitle(topic.getTitle());
    		otopic.setContent(topic.getContent());
    		otopic.setCate(topic.getCate());
    		otopic.setPrice(topic.getPrice());
    		otopic.setUpdatetime(new Date());
    		topicRes.save(otopic) ;
    	}
        return view;
    }
    @RequestMapping("/save")
    @Menu(type = "apps" , subtype = "topic" , access = false)
    public ModelAndView save(HttpServletRequest request , HttpServletResponse response, @Valid Topic topic) {
    	User user = super.getUser(request) ;
    	topic.setCreater(user.getId());
    	topic.setUsername(user.getUsername());
    	topicRes.save(topic) ;
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/"+topic.getId()+".html")) ;
        return view;
    }
    
    @RequestMapping("/top/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView top(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		Topic topic = topicRes.findOne(id) ;
    		topic.setTop(true);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    @RequestMapping("/untop/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView untop(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		Topic topic = topicRes.findOne(id) ;
    		topic.setTop(false);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    @RequestMapping("/essence/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView essence(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		Topic topic = topicRes.findOne(id) ;
    		topic.setEssence(true);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    @RequestMapping("/unessence/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView unessence(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		Topic topic = topicRes.findOne(id) ;
    		topic.setEssence(false);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    
    @RequestMapping("/finish/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView finish(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		Topic topic = topicRes.findOne(id) ;
    		topic.setFinish(true);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    @RequestMapping("/unfinish/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView unfinish(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		Topic topic = topicRes.findOne(id) ;
    		topic.setFinish(false);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    
    @RequestMapping("/comment/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView comment(HttpServletRequest request , HttpServletResponse response, @PathVariable String id , @Valid TopicComment comment) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		User user = super.getUser(request) ;
    		comment.setUsername(user.getUsername());
    		comment.setCreater(user.getId());
    		comment.setAdmin("0".equals(user.getUsertype()));
    		comment.setDataid(id);
    		comment.setId(null);

    		IP ipdata = IPTools.getInstance().findGeography(request.getRemoteAddr()) ;
    		
    		comment.setCountry(ipdata.getCountry());
    		comment.setProvince(ipdata.getProvince());
    		comment.setCity(ipdata.getCity());
    		comment.setRegion(ipdata.toString()+"["+request.getRemoteAddr()+"]");
    		
    		Topic topic = topicRes.findOne(id) ;
    		topic.setAnswers(topic.getAnswers()+1);
    		topic.setUpdatetime(new Date());
    		topicRes.save(topic) ;
    		
    		comment.setCate(topic.getCate());
    		topicCommentRes.save(comment) ;
    	}
        return view;
    }
    
    @RequestMapping("/delete/{id}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView delete(HttpServletRequest request , HttpServletResponse response, @PathVariable String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/")) ;
    	if(!StringUtils.isBlank(id)){
    		topicRes.delete(id);
    	}
        return view;
    }
    
    @RequestMapping("/rmcomment/{id}/{commentid}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView rmcomment(HttpServletRequest request , HttpServletResponse response, @PathVariable String id, @PathVariable String commentid) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(commentid)){
    		topicCommentRes.delete(commentid);
    		
    		Topic topic = topicRes.findOne(id) ;
    		if(topic.getComments()>0){
    			topic.setAnswers(topic.getAnswers()-1);
    		}else{
    			topic.setAnswers(0);
    		}
    		topicRes.save(topic) ;
    	}
        return view;
    }
    
    @RequestMapping("/commentup/{id}/{commentid}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView commentup(HttpServletRequest request , HttpServletResponse response, @PathVariable String id, @PathVariable String commentid) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/topic/up")) ;
    	if(!StringUtils.isBlank(id)){
    		TopicComment topicComment = topicCommentRes.findOne(commentid) ;
    		if(topicComment.getView() == null){
	    		topicComment.setUp(topicComment.getUp()+1);
	    		
	    		User user = super.getUser(request) ;
	    		
	    		TopicView topicView = new TopicView() ;
	    		
	    		long data = UKTools.ipToLong(request.getRemoteAddr()) ;
	    		
	    		topicView.setCratetime(new Date());
	    		topicView.setDataid(id);
	    		topicView.setOptype(UKDataContext.AskOperatorType.UPS.toString());
	    		topicView.setIpcode(String.valueOf(data));
	    		topicView.setUsername(user.getUsername());
	    		
	    		topicView.setDataid(commentid);
	    		
	    		
	    		IP ipdata = IPTools.getInstance().findGeography(request.getRemoteAddr()) ;
	    		
	    		topicView.setCountry(ipdata.getCountry());
	    		topicView.setProvince(ipdata.getProvince());
	    		topicView.setCity(ipdata.getCity());
	    		topicView.setRegion(ipdata.toString()+"["+request.getRemoteAddr()+"]");
	    		
	    		topicViewRes.save(topicView) ;
	    		topicComment.setView(topicView);
    		}else{
    			if(topicComment.getUp() > 0){
    				topicComment.setUp(topicComment.getUp()-1);
    			}else{
    				topicComment.setUp(0);
    			}
    			topicViewRes.delete(topicComment.getView().getId());
    			topicComment.setView(null);
    		}
    		
    		topicCommentRes.save(topicComment) ;
    		
    		view.addObject("topicComment", topicComment) ;
    	}
        return view;
    }
    
    @RequestMapping("/optimal/{id}/{commentid}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView optimal(HttpServletRequest request , HttpServletResponse response, @PathVariable String id, @PathVariable String commentid) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		TopicComment topicComment = topicCommentRes.findOne(commentid) ;
    		
    		List<TopicComment> topicCommentList = topicCommentRes.findByOptimal(id) ;
    		for(TopicComment tc : topicCommentList){
    			tc.setOptimal(false);
    			topicCommentRes.save(tc) ;
    		}
    		
    		topicComment.setOptimal(true);
    		topicCommentRes.save(topicComment) ;
    		
    		Topic topic = topicRes.findOne(id) ;
    		topic.setAccept(true);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    @RequestMapping("/unoptimal/{id}/{commentid}")
    @Menu(type = "apps" , subtype = "topic" , access = true)
    public ModelAndView unoptimal(HttpServletRequest request , HttpServletResponse response, @PathVariable String id, @PathVariable String commentid) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/topic/detail/{id}.html")) ;
    	if(!StringUtils.isBlank(id)){
    		TopicComment topicComment = topicCommentRes.findOne(commentid) ;
    		topicComment.setOptimal(false);
    		topicCommentRes.save(topicComment) ;
    		
    		Topic topic = topicRes.findOne(id) ;
    		topic.setAccept(false);
    		topicRes.save(topic) ;
    	}
        return view;
    }
    
    /**
	 * 处理 创建人
	 * @param defaultTopicList
	 */
	protected FacetedPage<TopicComment> processCreater(FacetedPage<TopicComment> topicCommentList){
		List<String> users = new ArrayList<String>();
		for(TopicComment topicComment : topicCommentList.getContent()){
			users.add(topicComment.getCreater()) ;
		}
		List<User> userList = userRes.findAll(users) ;
		for(TopicComment topicComment : topicCommentList.getContent()){
			for(User user : userList){
				if(topicComment.getCreater().equals(user.getId())){
					topicComment.setUser(user); break ;
				}
			}
		}
		return topicCommentList ;
	}
}