package com.zx.sms.connect.manager.smgp;

import com.zx.sms.BaseMessage;
import com.zx.sms.codec.smgp.msg.SMGPDeliverMessage;
import com.zx.sms.codec.smgp.msg.SMGPSubmitMessage;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.ServerEndpoint;
import com.zx.sms.handler.api.gate.SessionConnectedHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class SMGPSessionConnectedHandler extends SessionConnectedHandler {
	public SMGPSessionConnectedHandler(int t) {
		totleCnt = new AtomicInteger(t);
	}

	@Override
	protected BaseMessage createTestReq(String content) {
		final EndpointEntity finalentity = getEndpointEntity();

		if (finalentity instanceof ServerEndpoint) {
			SMGPDeliverMessage pdu = new SMGPDeliverMessage();
			pdu.setDestTermId("10086");
			pdu.setMsgContent(content);
			pdu.setSrcTermId("13800138000");
			return pdu;
		} else {
			SMGPSubmitMessage pdu = new SMGPSubmitMessage();
			pdu.setSrcTermId("1065902100990");
	        pdu.setDestTermIdArray("18939955801");
	        pdu.setMsgContent(content);
			return pdu;
		}
	}

}
