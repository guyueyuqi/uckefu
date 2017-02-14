package com.ukefu.ask.web.handler.user;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.ask.service.repository.FansRepository;
import com.ukefu.ask.service.repository.TopicCommentRepository;
import com.ukefu.ask.service.repository.TopicRepository;
import com.ukefu.ask.service.repository.UKeFuRepository;
import com.ukefu.ask.service.repository.UserRepository;
import com.ukefu.ask.web.handler.Handler;
import com.ukefu.ask.web.model.Fans;
import com.ukefu.ask.web.model.Message;
import com.ukefu.ask.web.model.Topic;
import com.ukefu.ask.web.model.TopicComment;
import com.ukefu.ask.web.model.User;
import com.ukefu.util.IP;
import com.ukefu.util.IPTools;
import com.ukefu.util.Menu;

@Controller
@RequestMapping("/user")
public class UserController extends Handler{
	
	@Autowired
	private UserRepository userRes ;
	
	@Autowired
    private TopicRepository topicRes;
	
	@Autowired
	private FansRepository fansRes ;
	
	@Autowired
	private UKeFuRepository uKeFuRes ;
	
	@Autowired
    private TopicCommentRepository topicCommentRes;
	
	@Value("${web.upload-path}")
    private String path;
	
	@RequestMapping("/index")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView index(HttpServletRequest request , HttpServletResponse response) {
		return request(super.createRequestPageTempletResponse("redirect:/user/index/"+super.getUser(request).getId()+".html")) ;
    }
	
    @RequestMapping("/index/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = true)
    public ModelAndView index(HttpServletRequest request , HttpServletResponse response, @PathVariable String userid , @Valid String q) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/index")) ;
    	
    	User curruser = userRes.findById(userid) ;
    	if(curruser!=null){
	    	if(super.getUser(request).isLogin()){
	    		view.addObject("fan" , fansRes.findByUserAndCreater(userid , super.getUser(request).getId())) ;
	    	}
	    	view.addObject("curruser", curruser) ;
	    	
	    	view.addObject("follows", fansRes.countByCreater(userid)) ;
	    	
	    	view.addObject("fans", fansRes.countByUser(userid)) ;
	    	
	    	view.addObject("topicList", topicRes.findByCon(new NativeSearchQueryBuilder().withQuery(termQuery("creater" , userid)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC)) , q , super.getP(request) , super.get50Ps(request))) ;
	    	
	    	
	    	int ps = super.getPs(request) ;
	    	NativeSearchQueryBuilder query = new NativeSearchQueryBuilder().withQuery(termQuery("creater" , userid)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC))
	    			.addAggregation(AggregationBuilders.terms("dataid").field("dataid").subAggregation(AggregationBuilders.topHits("top").addSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC))
	    			        .setExplain(true)
	    			        .setSize(1)
	    			        .setFrom(0))
	    			        .size(super.getP(request) * ps)) ;
	    	FacetedPage<TopicComment> topicCommentFacetPage = topicCommentRes.findByCon(query , "dataid" , "top"  , q , super.getP(request) , ps)  ; 
	    	List<String> relaTopics = new ArrayList<String>();
	    	for(int i = 0; i < topicCommentFacetPage.getContent().size() && i<ps; i++){
	    		TopicComment comment = topicCommentFacetPage.getContent().get(i) ;
	    		relaTopics.add(comment.getDataid()) ;
	    	}
	    	List<TopicComment> resultTopicCommentList = new ArrayList<TopicComment>();
	    	if(relaTopics.size() > 0){
		    	Iterable<Topic> topicList = topicRes.findAll(relaTopics) ;
		    	for(Topic topic : topicList){
		    		for(TopicComment comment : topicCommentFacetPage.getContent()){
		        		if(comment.getDataid().equals(topic.getId())){
		        			comment.setTopic(topic); 
		        			resultTopicCommentList.add(comment) ;
		        			break ;
		        		}
		        	}
		    	}
		    	
	    	}
	    	view.addObject("topicCommentList", resultTopicCommentList );
    	}else{
    		view = request(super.createRequestPageTempletResponse("redirect:/")) ;
    	}
    	
    	return view;
    }
    
    @RequestMapping("/comment/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = true)
    public ModelAndView comment(HttpServletRequest request , HttpServletResponse response, @PathVariable String userid , @Valid String q) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/user/comment")) ;
    	
    	User user = super.getUser(request) ;
    	user.setId(userid); ;
    	view.addObject("user", user) ;
    	
    	int ps = super.getPs(request) , p = super.getP(request) + 1 ;	//流式 翻页 默认从 第二页开始
    	NativeSearchQueryBuilder query = new NativeSearchQueryBuilder().withQuery(termQuery("creater" , userid)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC))
    			.addAggregation(AggregationBuilders.terms("dataid").field("dataid").subAggregation(AggregationBuilders.topHits("top").addSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC))
    			        .setExplain(true)
    			        .setSize(1)
    			        .setFrom(0))
    			        .size(p * ps)) ;
    	FacetedPage<TopicComment> topicCommentFacetPage = topicCommentRes.findByCon(query , "dataid" , "top"  , q , p , ps)  ; 
    	List<String> relaTopics = new ArrayList<String>();
    	if(topicCommentFacetPage.getContent().size() == p * ps){
	    	for(int i = topicCommentFacetPage.getContent().size() - 1; i >= topicCommentFacetPage.getContent().size() - ps ; i--){
	    		TopicComment comment = topicCommentFacetPage.getContent().get(i) ;
	    		relaTopics.add(comment.getDataid()) ;
	    	}
	    	Iterable<Topic> topicList = topicRes.findAll(relaTopics) ;
	    	List<TopicComment> resultTopicCommentList = new ArrayList<TopicComment>();
	    	for(Topic topic : topicList){
	    		for(TopicComment comment : topicCommentFacetPage.getContent()){
	        		if(comment.getDataid().equals(topic.getId())){
	        			comment.setTopic(topic); 
	        			resultTopicCommentList.add(comment) ;
	        			break ;
	        		}
	        	}
	    	}
	    	
	    	view.addObject("topicCommentList", resultTopicCommentList );
    	}
    	return view;
    }
    
    @RequestMapping("/profile")
    @Menu(type = "apps" , subtype = "user" , name = "profile" , access = false)
    public ModelAndView profile(HttpServletRequest request , HttpServletResponse response, @Valid String orgi, @Valid String t) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/profile")) ;
    	User user = userRes.getOne(super.getUser(request).getId()) ;
    	if(StringUtils.isBlank(user.getProvince()) || StringUtils.isBlank(user.getCity())){
    		IP ip = IPTools.getInstance().findGeography(request.getRemoteAddr()) ;
    		if(ip.getProvince().equals("0")){
    			user.setProvince("未知");
    		}else{
    			user.setProvince(ip.getProvince());
    		}
    		if(ip.getCity().equals("0")){
    			user.setCity("未知");
    		}else{
    			user.setCity(ip.getCity());
    		}
    	}
    	view.addObject("curruser", user) ;
    	
    	if(!StringUtils.isBlank(t)){
    		view.addObject("t", t) ;
    	}
    	
        return view;
    }
    
    @RequestMapping("/profile/save")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView profilesave(HttpServletRequest request , HttpServletResponse response, @Valid User tuser) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/user/profile.html?msg=1")) ;
    	User user = userRes.getOne(super.getUser(request).getId()) ;
    	
    	User eusers = userRes.findByUsername(tuser.getUsername()) ;
    	
    	if(eusers == null || eusers.getId().equals(user.getId())){
	    	user.setGender(tuser.getGender());
	    	user.setMobile(tuser.getMobile());
	    	user.setUsername(tuser.getUsername());
	    	user.setProvince(tuser.getProvince());
	    	user.setCity(tuser.getCity());
	    	user.setMemo(tuser.getMemo());
	    	userRes.save(user) ;
    	}else{
    		view = request(super.createRequestPageTempletResponse("redirect:/user/profile.html?msg=0")) ;
    	}
    	
    	
        return view ;
    }
    
    @RequestMapping("/profile/avator")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView profilesave(HttpServletRequest request , HttpServletResponse response, @RequestParam(value = "avator", required = false) MultipartFile avator) throws Exception {
    	User user = userRes.getOne(super.getUser(request).getId()) ;
    	FileUtils.writeByteArrayToFile(new File(path , user.getId()), avator.getBytes());
        return request(super.createRequestPageTempletResponse("redirect:/user/profile.html?t=avator")) ;
    }
    
    @RequestMapping("/fans/list/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = true)
    public ModelAndView fanslist(HttpServletRequest request , HttpServletResponse response, @PathVariable String userid) throws Exception {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/fans")) ; 
    	Pageable page = new PageRequest(super.getP(request), super.getPs(request), new Sort(Direction.DESC, "createtime")) ;
    	Page<Fans> fansList = fansRes.findByUser(userid, page) ;
    	
    	List<String> userids = new ArrayList<String>();
    	for(Fans fan : fansList){
    		userids.add(fan.getCreater()) ;
    	}
    	if(userids.size()>0){
    		view.addObject("fansList",new PageImpl<User>(userRes.findAll(userids), page, fansList.getTotalElements()) ) ;
    	}
    	
    	User curruser = userRes.findById(userid) ;
    	if(super.getUser(request).isLogin()){
    		view.addObject("fan" , fansRes.findByUserAndCreater(userid , super.getUser(request).getId())) ;
    	}
    	view.addObject("curruser", curruser) ;
    	
    	view.addObject("follows", fansRes.countByCreater(userid)) ;
    	
    	view.addObject("fans", fansRes.countByUser(userid)) ;
    	
        return view ;
    }
    
    @RequestMapping("/follows/list/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = true)
    public ModelAndView follows(HttpServletRequest request , HttpServletResponse response, @PathVariable String userid) throws Exception {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/follows")) ; 
    	Pageable page = new PageRequest(super.getP(request), super.getPs(request), new Sort(Direction.DESC, "createtime")) ;
    	Page<Fans> fansList = fansRes.findByCreater(userid, page) ;
    	
    	List<String> userids = new ArrayList<String>();
    	for(Fans fan : fansList){
    		userids.add(fan.getUser()) ;
    	}
    	if(userids.size()>0){
    		view.addObject("fansList",new PageImpl<User>(userRes.findAll(userids), page, fansList.getTotalElements()) ) ;
    	}
    	
    	User curruser = userRes.findById(userid) ;
    	if(super.getUser(request).isLogin()){
    		view.addObject("fan" , fansRes.findByUserAndCreater(userid , super.getUser(request).getId())) ;
    	}
    	view.addObject("curruser", curruser) ;
    	
    	view.addObject("follows", fansRes.countByCreater(userid)) ;
    	
    	view.addObject("fans", fansRes.countByUser(userid)) ;
    	
        return view ;
    }
    
    @RequestMapping("/fans/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView fans(HttpServletRequest request , HttpServletResponse response, @PathVariable String userid) throws Exception {
    	User user = userRes.getOne(super.getUser(request).getId()) ;
    	if(fansRes.findByUserAndCreater(userid, user.getId()) == null && !user.getId().equals(userid)){
	    	Fans fans = new Fans();
	    	fans.setCreater(user.getId());
	    	fans.setUser(userid);
	    	fansRes.save(fans) ;
	    	
	    	user.setFollows(user.getFollows()+1);
	    	userRes.save(user) ;
	    	
	    	User target = userRes.getOne(userid) ;
	    	target.setFans(fansRes.countByUser(userid));
	    	userRes.save(target) ;
    	}
        return request(super.createRequestPageTempletResponse("redirect:/user/index/{userid}.html")) ;
    }
    
    @RequestMapping("/unfans/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView unfans(HttpServletRequest request , HttpServletResponse response, @PathVariable String userid) throws Exception {
    	User user = userRes.getOne(super.getUser(request).getId()) ;
    	Fans fan = fansRes.findByUserAndCreater(userid, user.getId()) ; 
    	if(fan != null){
	    	fansRes.delete(fan);
	    	if(user.getFollows() > 0){
	    		user.setFollows(user.getFollows()-1);
	    		userRes.save(user) ;
	    	}
	    	
	    	User target = userRes.getOne(userid) ;
	    	target.setFans(fansRes.countByUser(userid));
	    	userRes.save(target) ;
    	}
    	
    	
        return request(super.createRequestPageTempletResponse("redirect:/user/index/{userid}.html")) ;
    }
    
    @RequestMapping("/center")
    @Menu(type = "apps" , subtype = "user" , name="center" , access = false)
    public ModelAndView center(HttpServletRequest request , HttpServletResponse response, @Valid String orgi, @Valid String q) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/center")) ;
    	User user = userRes.getOne(super.getUser(request).getId()) ;
    	view.addObject("curruser", user) ;
    	
    	view.addObject("topicList", topicRes.findByCon(new NativeSearchQueryBuilder().withQuery(termQuery("creater" , super.getUser(request).getId())).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC)) , q , super.getP(request) , super.get50Ps(request))) ;
    	
        return view;
    }
    
    @RequestMapping("/center/answer")
    @Menu(type = "apps" , subtype = "user" , name="answer" , access = false)
    public ModelAndView answer(HttpServletRequest request , HttpServletResponse response, @Valid String orgi, @Valid String q) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/answer")) ;
    	User user = userRes.getOne(super.getUser(request).getId()) ;
    	view.addObject("curruser", user) ;
    	
    	int ps = super.getPs(request) ;
    	NativeSearchQueryBuilder query = new NativeSearchQueryBuilder().withQuery(termQuery("creater" , super.getUser(request).getId())).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC))
    			.addAggregation(AggregationBuilders.terms("dataid").field("dataid").subAggregation(AggregationBuilders.topHits("top")
    			        .setExplain(true)
    			        .setSize(1)
    			        .setFrom(0))
    			        .size(super.getP(request) * ps)) ;
    	FacetedPage<TopicComment> topicCommentFacetPage = topicCommentRes.findByCon(query , "dataid" , "top" , q , super.getP(request) , ps)  ; 
    	List<String> relaTopics = new ArrayList<String>();
    	for(int i = 0; i < topicCommentFacetPage.getContent().size() && i<ps; i++){
    		TopicComment comment = topicCommentFacetPage.getContent().get(i) ;
    		relaTopics.add(comment.getDataid()) ;
    	}
    	List<TopicComment> resultTopicCommentList = new ArrayList<TopicComment>();
    	if(relaTopics.size() > 0){
	    	Iterable<Topic> topicList = topicRes.findAll(relaTopics) ;
	    	for(Topic topic : topicList){
	    		for(TopicComment comment : topicCommentFacetPage.getContent()){
	        		if(comment.getDataid().equals(topic.getId())){
	        			comment.setTopic(topic); 
	        			resultTopicCommentList.add(comment) ;
	        			break ;
	        		}
	        	}
	    	}
	    	
    	}
    	view.addObject("topicCommentList", resultTopicCommentList );
        return view;
    }
    
    @RequestMapping("/center/fans")
    @Menu(type = "apps" , subtype = "user" , name="fans" , access = false)
    public ModelAndView centerfans(HttpServletRequest request , HttpServletResponse response, @Valid String orgi, @Valid String q) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/centerfans")) ;
    	String userid = super.getUser(request).getId() ;
    	Pageable page = new PageRequest(super.getP(request), super.getPs(request), new Sort(Direction.DESC, "createtime")) ;
    	Page<Fans> fansList = fansRes.findByUser(userid, page) ;
    	
    	List<String> userids = new ArrayList<String>();
    	for(Fans fan : fansList){
    		userids.add(fan.getCreater()) ;
    	}
    	if(userids.size()>0){
    		view.addObject("fansList",new PageImpl<User>(userRes.findAll(userids), page, fansList.getTotalElements()) ) ;
    	}
    	
        return view;
    }
    
    @RequestMapping("/center/follows")
    @Menu(type = "apps" , subtype = "user" , name="follows" , access = false)
    public ModelAndView centerfollows(HttpServletRequest request , HttpServletResponse response, @Valid String orgi, @Valid String q) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/centerfollows")) ;
    	String userid = super.getUser(request).getId() ;
    	Pageable page = new PageRequest(super.getP(request), super.getPs(request), new Sort(Direction.DESC, "createtime")) ;
    	Page<Fans> fansList = fansRes.findByCreater(userid, page) ;
    	
    	List<String> userids = new ArrayList<String>();
    	for(Fans fan : fansList){
    		userids.add(fan.getUser()) ;
    	}
    	if(userids.size()>0){
    		view.addObject("fansList",new PageImpl<User>(userRes.findAll(userids), page, fansList.getTotalElements()) ) ;
    	}
    	
    	
        return view;
    }
    
    @RequestMapping("/message/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView message(HttpServletRequest request , HttpServletResponse response,@PathVariable String userid) throws Exception {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/user/sendmsg")) ;
    	User curruser = userRes.getOne(userid) ;
    	view.addObject("curruser",curruser) ;
    	return view ;
    }
    
    @RequestMapping("/sendmsg/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView sendmsg(HttpServletRequest request , HttpServletResponse response,@PathVariable String userid,@Valid Message message , @RequestHeader(value = "referer", required = false) String referer) throws Exception {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/user/messages.html")) ;
    	if(!StringUtils.isBlank(referer)){
    		if(referer.indexOf("?") > 0){
    			view = request(super.createRequestPageTempletResponse("redirect:"+referer+"&msg=sendmsg")) ;
    		}else{
    			view = request(super.createRequestPageTempletResponse("redirect:"+referer+"?msg=sendmsg")) ;
    		}
    	}
    	if(message!=null){
    		message.setCreater(super.getUser(request).getId());
    		message.setFromuser(super.getUser(request).getId());
    		message.setTouser(userid);
    		
    		message.setStatus("0");
    		
    		message.setUserid(super.getUser(request).getId());
    		message.setOwner(userid);
    		message.setMsgtype("recive");
    		message.setOrgi(super.getUser(request).getOrgi());
    		uKeFuRes.save(message) ;
    		
    		Message owner = new Message();
    		
    		owner.setCreater(super.getUser(request).getId());
    		owner.setFromuser(super.getUser(request).getId());
    		owner.setTouser(userid);
    		
    		owner.setOrgi(super.getUser(request).getOrgi());
    		owner.setUserid(userid);
    		owner.setStatus("1");
    		owner.setOwner(super.getUser(request).getId());
    		owner.setMsgtype("send");
    		owner.setContent(message.getContent());
    		
    		uKeFuRes.save(owner) ;
    		
    	}
    	return view ;
    }
    
    @RequestMapping("/messages")
    @Menu(type = "apps" , subtype = "user" , name="messages" , access = false)
    public ModelAndView messages(HttpServletRequest request , HttpServletResponse response, @Valid String orgi, @Valid String q , @Valid String op) {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/messages")) ;
    	if(!StringUtils.isBlank(op) && op.equals("load")){
    		view = request(super.createRequestPageTempletResponse("/apps/user/message")) ;
    	}
    	final String userid = super.getUser(request).getId() ;
    	int ps = super.getPs(request) ;
    	NativeSearchQueryBuilder query = new NativeSearchQueryBuilder().withQuery(termQuery("owner" , userid)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC))
    			.addAggregation(AggregationBuilders.terms("userid").field("userid").subAggregation(AggregationBuilders.topHits("top").addSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC))
    			        .setExplain(true)
    			        .setSize(1)
    			        .setFrom(0))
    			        .size(super.getP(request) * ps)) ;
    	FacetedPage<Serializable> messageFacetPage = uKeFuRes.findByCon(Message.class , query , q , super.getP(request) , ps)  ; 
    	List<String> messages = new ArrayList<String>();
    	for(int i = messageFacetPage.getContent().size() - 1; i>=super.getP(request) * ps && i >= messageFacetPage.getContent().size() - ps ; i--){
    		Message message = (Message) messageFacetPage.getContent().get(i) ;
    		messages.add(message.getUserid()) ;
    	}
    	List<Message> messageList = new ArrayList<Message>();
    	if(messages.size() > 0){
	    	List<User> userList = userRes.findAll(messages) ;
	    	for(int i = messageFacetPage.getContent().size() - 1; i>=super.getP(request) * ps && i >= messageFacetPage.getContent().size() - ps ; i--){
	    		Message message = (Message) messageFacetPage.getContent().get(i) ;
	    		for(User user: userList){
	    			if(message.getUserid().equals(user.getId())){
	    				message.setTarget(user); messageList.add(message) ;break ;
	    			}
	    		}
	    		
	    	}
    	}
    	view.addObject("msgList", messageList);
    	
        return view;
    }
    
    
    @RequestMapping("/message/delete/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView messagedelete(HttpServletRequest request , HttpServletResponse response,@PathVariable String userid) throws Exception {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/user/messages.html")) ;
    	uKeFuRes.deleteByQuery(termQuery("userid", userid) , Message.class);
    	return view ;
    }
    
    @RequestMapping("/message/list/{userid}")
    @Menu(type = "apps" , subtype = "user" , access = false)
    public ModelAndView messagelist(HttpServletRequest request , HttpServletResponse response,@PathVariable String userid) throws Exception {
    	ModelAndView view = request(super.createAppsTempletResponse("/apps/user/messagelist")) ;
    	view.addObject("msgList", uKeFuRes.findByUserid(userid, super.getP(request), super.getPs(request))) ;
    	return view ;
    }
    
    
}