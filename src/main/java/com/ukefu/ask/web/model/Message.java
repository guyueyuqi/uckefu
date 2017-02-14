package com.ukefu.ask.web.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "uckefu", type = "uc_ask_message")
public class Message implements UKAgg{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2082746543063986898L;
	private String id ;
	private String creater ;
	private Date createtime = new Date();
	private Date updatetime = new Date() ;
	private String msgtype ;		// 消息类型，  发出的私信 ， 收到的私信， 系统消息
	private String userid ;
	private String owner ;		
	private String content ;
	private String status ;
	private String fromuser ;
	private String touser ;
	private String orgi ;
	
	private User target ;
	
	private int rowcount ;
	
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

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFromuser() {
		return fromuser;
	}

	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}
	@Transient
	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}
	public String getOrgi() {
		return orgi;
	}
	@Transient
	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
