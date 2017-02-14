package com.ukefu.ask.web.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
@Document(indexName = "uckefu", type = "uc_ask_topiccomment")
public class TopicComment implements UKAgg{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4911955236794918875L;
	private String id ;
	private String username;
	private String creater ;
	private Date createtime = new Date() ;
	@Field(index = FieldIndex.not_analyzed , type = FieldType.String)
	private String dataid ;
	
	private String content ; 	//评论内容

	private Date updatetime = new Date() ;
	
	private boolean optimal ;	//是否最佳答案
	
	private int up ;			//点赞数量
	private int comments ;		//回复数量
	
	private boolean admin ;
	
	private String cate ;
	
	private String optype ;
	private String ipcode ;
	private String country ;
	private String province ;
	private String city ;
	private String isp ;
	private String region ;
	
	private int rowcount ;
	
	private TopicView view ;
	
	private Topic topic ;
	
	private User user ;
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getIpcode() {
		return ipcode;
	}
	public void setIpcode(String ipcode) {
		this.ipcode = ipcode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getIsp() {
		return isp;
	}
	public void setIsp(String isp) {
		this.isp = isp;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isOptimal() {
		return optimal;
	}
	public void setOptimal(boolean optimal) {
		this.optimal = optimal;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public int getUp() {
		return up;
	}
	public void setUp(int up) {
		this.up = up;
	}
	public int getComments() {
		return comments;
	}
	public void setComments(int comments) {
		this.comments = comments;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	@OneToOne(cascade  =  CascadeType.PERSIST)
    @JoinColumns ({
        @JoinColumn(name  =   "view" , referencedColumnName  =   "dataid" ),
        @JoinColumn(name  =   "creater" , referencedColumnName  =   "creater" )
    })
	public TopicView getView() {
		return view;
	}
	public void setView(TopicView view) {
		this.view = view;
	}
	@Transient
	public Topic getTopic() {
		return topic;
	}
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	@Transient
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getCate() {
		return cate;
	}
	public void setCate(String cate) {
		this.cate = cate;
	}
	@Transient
	public int getRowcount() {
		return rowcount;
	}
	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
}
