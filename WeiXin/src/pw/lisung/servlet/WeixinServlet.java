package pw.lisung.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;

import pw.lisung.po.TextMessage;
import pw.lisung.util.CheckUtil;
import pw.lisung.util.MessageUtil;

public class WeixinServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");

		PrintWriter out = resp.getWriter();
		if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			Map<String, String> map;
			map = MessageUtil.xmlToMap(req);
			String toUserName = map.get("ToUserName");
			String fromUserName = map.get("FromUserName");
			String msgType = map.get("MsgType");
			String content = map.get("Content");

			String message = null;
			if (MessageUtil.MESSAGE_TEXT.equals(msgType)) {
				if ("1".equals(content)) {
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.firstMenu());
				} else if ("2".equals(content)) {
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.secondMenu());
				} else if ("?".equals(content) || "？".equals(content)) {
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				}
//				TextMessage text = new TextMessage();
//				text.setFromUserName(toUserName);
//				text.setToUserName(fromUserName);
//				text.setMsgType("text");
//				text.setCreateTime(new Date().getTime());
//				text.setContent("您发送的消息是:" + content);
//				message = MessageUtil.textMessageToXml(text);
			} else if (MessageUtil.MESSAGE_EVENT.equals(msgType)) {
				String eventType = map.get("Event");
				if (MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)) {
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				}
			}

			out.println(message);
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
