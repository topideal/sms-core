package com.zx.sms.connect.manager.sgip;

import com.zx.sms.connect.manager.EndpointEntity.ChannelType;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 *经测试，35个连接，每个连接每200/s条消息
 *lenovoX250能承担7000/s消息编码解析无压力。
 *10000/s的消息服务不稳定，开个网页，或者打开其它程序导致系统抖动，会有大量消息延迟 (超过500ms)
 *
 *低负载时消息编码解码可控制在10ms以内。
 *
 */


public class TestSgipEndPoint {
	private static final Logger logger = LoggerFactory.getLogger(TestSgipEndPoint.class);

	@Test
	public void testsgipEndpoint() throws Exception {
		ResourceLeakDetector.setLevel(Level.ADVANCED);
		final EndpointManager manager = EndpointManager.INS;

	/*	SgipServerEndpointEntity server = new SgipServerEndpointEntity();
		server.setId("sgipserver");
		server.setHost("220.196.52.117");
		server.setPort(8801);
		server.setValid(true);
		//使用ssl加密数据流
		server.setUseSSL(false);
		
		SgipServerChildEndpointEntity child = new SgipServerChildEndpointEntity();
		child.setId("sgipchild");
		child.setLoginName("79798085912");
		child.setLoginPassowrd("min59han");
		child.setNodeId(3000091549L);
		child.setValid(true);
		child.setChannelType(ChannelType.UP);
		child.setMaxChannels((short)3);
		child.setRetryWaitTimeSec((short)30);
		child.setMaxRetryCnt((short)3);
		child.setReSendFailMsg(false);
		child.setIdleTimeSec((short)30);
//		child.setWriteLimit(200);
//		child.setReadLimit(200);
		child.setSupportLongmsg(SupportLongMessage.BOTH);  
		List<BusinessHandlerInterface> serverhandlers = new ArrayList<BusinessHandlerInterface>();
		
		serverhandlers.add(new SgipReportRequestMessageHandler());
		serverhandlers.add(new SGIPSessionConnectedHandler(10000));   
		child.setBusinessHandlerSet(serverhandlers);
		server.addchild(child);
		
		manager.addEndpointEntity(server);*/
		
		SgipClientEndpointEntity client = new SgipClientEndpointEntity();
		client.setId("sgipclient");
		client.setHost("220.196.52.117");
		client.setPort(8801);
		client.setLoginName("79798085912");
		client.setLoginPassowrd("min59han");
		client.setChannelType(ChannelType.DOWN);
		client.setNodeId(3000091549L);
		client.setMaxChannels((short)10);
		client.setRetryWaitTimeSec((short)100);
		client.setUseSSL(false);
		client.setReSendFailMsg(true);
//		client.setWriteLimit(200);
//		client.setReadLimit(200);
		List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
		clienthandlers.add(new SGIPMessageReceiveHandler());
		client.setBusinessHandlerSet(clienthandlers);
		manager.addEndpointEntity(client);
		manager.openAll();
		Thread.sleep(1000);
	//	for(int i=0;i<child.getMaxChannels();i++)
			manager.openEndpoint(client);
			manager.startConnectionCheckTask();
		System.out.println("start.....");

	/*	SgipSubmitRequestMessage submitmessage=new SgipSubmitRequestMessage();
		submitmessage.setUsernumber("18602195439");
		submitmessage.setCorpid("91549");
		submitmessage.setSpnumber("1065502180859");
		submitmessage.setServicetype("SHGRP");
		submitmessage.setMsgContent("测试联通网关...");

		List<Promise<SgipSubmitResponseMessage>> futures = ChannelUtil.syncWriteLongMsgToEntity("sgipclient",submitmessage);
		for(Promise  future: futures){
			//调用sync()方法，阻塞线程。等待接收response
			future.sync();
			//接收成功，如果失败可以获取失败原因，比如遇到连接突然中断错误等等
			if(future.isSuccess()){
				//打印收到的response消息
				logger.info("response:{}",future.get());
			}else{

				logger.error("response:{}",future.cause());
			}
		}*/

	//	manager.openEndpoint();
     //   LockSupport.park();

	//	EndpointManager.INS.close();
	}
}
