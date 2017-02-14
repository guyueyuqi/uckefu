package com.ukefu.ask.web.handler.admin.topic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.ask.service.repository.TopicCommentRepository;
import com.ukefu.ask.service.repository.TopicRepository;
import com.ukefu.ask.service.repository.TopicViewRepository;
import com.ukefu.ask.service.repository.UserRepository;
import com.ukefu.ask.web.handler.Handler;
import com.ukefu.ask.web.model.Topic;
import com.ukefu.ask.web.model.TopicComment;
import com.ukefu.ask.web.model.User;
import com.ukefu.core.UKDataContext;
import com.ukefu.util.Menu;

@Controller
@RequestMapping("/admin/topic")
public class AdminTopicController extends Handler{
	
	@Autowired
    private TopicRepository topicRes;
	
	@Autowired
	private UserRepository userRes ;
	
	@Autowired
	private TopicViewRepository topicViewRes ;
	
	@Autowired
	private TopicCommentRepository topicCommentRes ;
	
	@RequestMapping("/index")
	@Menu(type = "admin" , subtype = "topic" , access = false , admin = true)
    public ModelAndView admin(HttpServletRequest request , @Valid String q) {
		ModelAndView view = request(super.createAdminTempletResponse("/admin/topic/index"));
		FacetedPage<Topic> defaultTopicList = topicRes.getTopicByCate(UKDataContext.AskSectionType.DEFAULT.toString() , q, super.getP(request) , super.getPs(request)) ;
		
		view.addObject("defaultTopicList", processTopicCreater(defaultTopicList)) ;
        return view;
    }
	
	@RequestMapping("/comment")
	@Menu(type = "admin" , subtype = "comment" , access = false , admin = true)
    public ModelAndView comment(HttpServletRequest request , @Valid String q) {
		ModelAndView view = request(super.createAdminTempletResponse("/admin/topic/comment"));
		FacetedPage<TopicComment> topicCommentList = topicCommentRes.findByCon(new NativeSearchQueryBuilder(), q, super.getP(request) , super.getPs(request)) ;
		List<String> topics = new ArrayList<String>();
		for(TopicComment comment : topicCommentList.getContent()){
			topics.add(comment.getDataid()) ;
		}
		if(topics.size()>0){
			Iterable<Topic> topicList = topicRes.findAll(topics) ;
			for(TopicComment comment : topicCommentList.getContent()){
				for(Topic topic :topicList){
					if(topic.getId().equals(comment.getDataid())){
						comment.setTopic(topic); break ;
					}
				}
			}
		}
		
		view.addObject("topicCommentList", processCreater(topicCommentList)) ;
        return view;
    }
	
	
	/**
	 * 处理 创建人
	 * @param defaultTopicList
	 */
	protected FacetedPage<Topic> processTopicCreater(FacetedPage<Topic> defaultTopicList){
		List<String> users = new ArrayList<String>();
		for(Topic topic : defaultTopicList.getContent()){
			users.add(topic.getCreater()) ;
		}
		List<User> userList = userRes.findAll(users) ;
		for(Topic topic : defaultTopicList.getContent()){
			for(User user : userList){
				if(topic.getCreater().equals(user.getId())){
					topic.setUser(user); break ;
				}
			}
		}
		return defaultTopicList ;
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