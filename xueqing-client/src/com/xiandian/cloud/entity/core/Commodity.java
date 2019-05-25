package com.xiandian.cloud.entity.core;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.xiandian.cloud.entity.BaseEntity;

@Entity
@Table(name="commodity")
public class Commodity extends BaseEntity{
	private String content;
	private int flag;
	private String img;
	private String imgs;
	private String name;
	private String price;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
}
