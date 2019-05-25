package com.xiandian.cloud.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.xiandian.cloud.common.bean.MessageBean;
import com.xiandian.cloud.service.CommodityService;

@Controller
@RequestMapping("wechat")
public class WeChatController {
	@Autowired
	private CommodityService commodityService;
	
	private static final String APPID = "wx73b63ccf190eb39f";
    private static final String SECRET = "dac4d5358626c7ef0f6773d17c619095";
	
	@ResponseBody
	@RequestMapping("commodity")
	public MessageBean getCommodityList() {
		return new MessageBean(true,"",commodityService.getAll());
	}
	
	@ResponseBody
	@RequestMapping("search")
	public MessageBean search(@RequestParam(value = "keyword",required = false) String keyword) {
		return new MessageBean(true, "", commodityService.Search(keyword));
	}
	
    @ResponseBody
	@RequestMapping("onLogin")
	public String onLogin(@RequestParam(value="code",required=false) String code) {
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + SECRET + "&js_code="
				+ code + "&grant_type=authorization_code";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
			String data = responseEntity.getBody();
			System.out.println(data);
		}
		return null;
	}
}
