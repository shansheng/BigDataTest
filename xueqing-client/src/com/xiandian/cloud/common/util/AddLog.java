package com.xiandian.cloud.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.xiandian.cloud.web.BaseController;



public class AddLog extends BaseController{
	@Autowired
	private JmsTemplate jmsTemplate;
	
	/*public void Add(HttpServletRequest request,final String constant,final String objecttype,final int courseid) {
		final User user = getSessionUser(request);
		final String ip = UtilTools.getRemoteAddress(request);
		try
		{
			jmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					Log log = new Log();
					log.setUsername(user.getUsername());
					log.setOperation(constant);
					log.setIp(ip);
					log.setObjecttype(objecttype);
					log.setCourseid(courseid);
					log.setCreatedate(DateUtil.timeTostrHMS(new Date()));
					ObjectMessage message = session.createObjectMessage(log);
					return message;
				}
			});
		}
		catch(Exception e)
		{}
	}*/
	

}
