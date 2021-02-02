package com.zx.sms.handler.api.gate;

import com.zx.sms.BaseMessage;
import com.zx.sms.common.util.ChannelUtil;
import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import com.zx.sms.session.cmpp.SessionState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Lihuanghe(18852780@qq.com)
 *
 */
public abstract class SessionConnectedHandler extends AbstractBusinessHandler {
	private static final Logger logger = LoggerFactory.getLogger(SessionConnectedHandler.class);

	protected AtomicInteger totleCnt = new AtomicInteger(10);

	public AtomicInteger getTotleCnt() {
		return totleCnt;
	}

	public void setTotleCnt(AtomicInteger totleCnt) {
		this.totleCnt = totleCnt;
	}

	public SessionConnectedHandler() {
	}

	public SessionConnectedHandler(AtomicInteger t) {
		totleCnt = t;
	}

	protected abstract BaseMessage createTestReq(String content);

	@Override
	public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
		final AtomicInteger tmptotal = new AtomicInteger(totleCnt.get());
		if (evt == SessionState.Connect) {

			final Channel ch = ctx.channel();
			EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					int cnt = RandomUtils.nextInt() & 0x4ff;
					while (cnt > 0 && tmptotal.get() > 0) {
						List<Promise<BaseMessage>> futures = null;
						ChannelFuture chfuture = null;
						BaseMessage msg = createTestReq(UUID.randomUUID().toString());
//						chfuture = ChannelUtil.asyncWriteToEntity(getEndpointEntity().getId(), msg);
						futures = ChannelUtil.syncWriteLongMsgToEntity(getEndpointEntity().getId(), msg);
						
//						chfuture = ctx.writeAndFlush(msg);
						
						if (chfuture != null)
							chfuture.sync();

						if (futures != null) {
							try {
								for (Promise<BaseMessage> future : futures) {
									future.addListener(new GenericFutureListener<Future<BaseMessage>>() {
										@Override
										public void operationComplete(Future<BaseMessage> future) throws Exception {
											if (future.isSuccess()) {
//												 logger.debug("response:{}",future.get());
											} else {
//												 logger.error("response:{}",future.cause());
											}
										}
									});
									try {
										future.sync();
									}catch(Exception ex) {}
								}

							} catch (Exception e) {
								e.printStackTrace();
								cnt--;
								tmptotal.decrementAndGet();
								break;
							}
						}
					
						cnt--;
						tmptotal.decrementAndGet();
					}
					return true;
				}
			}, new ExitUnlimitCirclePolicy<Boolean>() {
				@Override
				public boolean notOver(Future<Boolean> future) {
					if (future.cause() != null)
						future.cause().printStackTrace();

					boolean over = ch.isActive() && tmptotal.get() > 0;
					if (!over) {
						logger.debug("========send over.============");

						// ch.writeAndFlush(new CmppTerminateRequestMessage());
					}
					return over;
				}
			}, 1);

		}

		ctx.fireUserEventTriggered(evt);
	}

	@Override
	public String name() {
		return "SessionConnectedHandler-Gate";
	}

	public SessionConnectedHandler clone() throws CloneNotSupportedException {
		SessionConnectedHandler ret = (SessionConnectedHandler) super.clone();
		return ret;
	}

}
