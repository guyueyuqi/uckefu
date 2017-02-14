package com.ukefu.ask.web.handler;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.ask.service.repository.TopicCommentRepository;
import com.ukefu.ask.service.repository.TopicRepository;
import com.ukefu.ask.service.repository.UserRepository;
import com.ukefu.ask.web.model.Topic;
import com.ukefu.ask.web.model.TopicComment;
import com.ukefu.ask.web.model.User;
import com.ukefu.core.UKDataContext;
import com.ukefu.util.Menu;

@Controller
public class ApplicationController extends Handler{
	
	@Autowired
    private TopicRepository topicRes;
	
	@Autowired
	private UserRepository userRes ;
	
	@Autowired
	private TopicCommentRepository topicCommentRes ;

	@RequestMapping("/")
	@Menu(type = "apps" , subtype = "index" , access = true)
    public ModelAndView admin(HttpServletRequest request , @Valid String q) {
		ModelAndView view = request(super.createAppsTempletResponse("/index"));
		FacetedPage<Topic> defaultTopicList = topicRes.getTopicByCate(UKDataContext.AskSectionType.DEFAULT.toString() , q, super.getP(request) , super.get50Ps(request)) ;
		
		view.addObject("defaultTopicList", processCreater(defaultTopicList)) ;
		
		view.addObject("relaAnswersList", topicRes.getTopicByCateAndRela(UKDataContext.AskSectionType.DEFAULT.toString() , "answers" , super.getP(request) , 15)) ;
		
		view.addObject("relaViewsList", topicRes.getTopicByCateAndRela(UKDataContext.AskSectionType.DEFAULT.toString() , "views" , super.getP(request) , 15)) ;
		
		NativeSearchQueryBuilder query = new NativeSearchQueryBuilder().withQuery(termQuery("cate" , UKDataContext.AskSectionType.DEFAULT.toString()))
    			.addAggregation(AggregationBuilders.terms("creater").field("creater").order(Terms.Order.count(false)).subAggregation(AggregationBuilders.topHits("top")
    			        .setExplain(true)
    			        .setSize(1)
    			        .setFrom(0))) ;
    	FacetedPage<TopicComment> topicCommentFacetPage = topicCommentRes.findByCon(query , "creater", "top" , q , super.getP(request) , super.getPs(request))  ; 
    	List<String> topUsers = new ArrayList<String>();
    	for(int i = 0; i < topicCommentFacetPage.getContent().size(); i++){
    		TopicComment comment = topicCommentFacetPage.getContent().get(i) ;
    		topUsers.add(comment.getCreater()) ;
    	}
    	if(topUsers.size() > 0){
    		List<TopicComment> topUserCommentList = new ArrayList<TopicComment>()  ;
    		List<User> topUsersList = userRes.findAll(topUsers) ;
    		for(TopicComment comment : topicCommentFacetPage.getContent()){
    			for(User user:topUsersList){
	    			if(comment.getCreater().equals(user.getId())){
	    				comment.setUser(user); topUserCommentList.add(comment) ; break ;
	    			}
    			}
    		}
	    	view.addObject("topUserCommentList", topUserCommentList) ;
    	}
		
        return view;
    }
	
	
	@RequestMapping("/my")
	@Menu(type = "apps" , subtype = "my" , access = true)
    public ModelAndView my(HttpServletRequest request , @Valid String q) {
		ModelAndView view = request(super.createAppsTempletResponse("/apps/topic/index"));
		FacetedPage<Topic> defaultTopicList = topicRes.getTopicByCateAndUser(UKDataContext.AskSectionType.DEFAULT.toString() , q , super.getUser(request).getId() , super.getP(request) , super.get50Ps(request)) ;
		view.addObject("defaultTopicList", processCreater(defaultTopicList)) ;
		
        return view;
    }
	@RequestMapping("/finish")
	@Menu(type = "apps" , subtype = "finish" , access = true)
    public ModelAndView finish(HttpServletRequest request , @Valid String q) {
		ModelAndView view = request(super.createAppsTempletResponse("/apps/topic/index"));
		FacetedPage<Topic> defaultTopicList = topicRes.findByCon(new NativeSearchQueryBuilder().withQuery(termQuery("cate" , UKDataContext.AskSectionType.DEFAULT.toString())).withQuery(termQuery("finish" , false)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC)) , q, super.getP(request) , super.get50Ps(request)) ;
		view.addObject("defaultTopicList", processCreater(defaultTopicList)) ;
		
		return view ;
	}
	
	@RequestMapping("/accept")
	@Menu(type = "apps" , subtype = "accept" , access = true)
    public ModelAndView accept(HttpServletRequest request , @Valid String q) {
		ModelAndView view = request(super.createAppsTempletResponse("/apps/topic/index"));
		FacetedPage<Topic> defaultTopicList = topicRes.findByCon(new NativeSearchQueryBuilder().withQuery(termQuery("cate" , UKDataContext.AskSectionType.DEFAULT.toString())).withQuery(termQuery("accept" , true)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC)) , q , super.getP(request) , super.get50Ps(request)) ;
		view.addObject("defaultTopicList", processCreater(defaultTopicList)) ;
		
		return view ;
	}
	
	@RequestMapping("/essence")
	@Menu(type = "apps" , subtype = "essence" , access = true)
    public ModelAndView essence(HttpServletRequest request , @Valid String q) {
		ModelAndView view = request(super.createAppsTempletResponse("/apps/topic/index"));
		FacetedPage<Topic> defaultTopicList = topicRes.findByCon(new NativeSearchQueryBuilder().withQuery(termQuery("cate" , UKDataContext.AskSectionType.DEFAULT.toString())).withQuery(termQuery("essence" , true)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC)) , q , super.getP(request) , super.get50Ps(request)) ;
		view.addObject("defaultTopicList", processCreater(defaultTopicList)) ;
		
		return view ;
	}
	/**
	 * 处理 创建人
	 * @param defaultTopicList
	 */
	protected FacetedPage<Topic> processCreater(FacetedPage<Topic> defaultTopicList){
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
}