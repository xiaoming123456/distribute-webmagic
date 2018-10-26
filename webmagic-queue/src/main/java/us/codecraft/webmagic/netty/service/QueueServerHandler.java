/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package us.codecraft.webmagic.netty.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import us.codecraft.webmagic.netty.common.NettyUtils;
import us.codecraft.webmagic.utils.LogException;

public class QueueServerHandler extends ChannelInboundHandlerAdapter {
	private static final Log log = LogFactory.getLog(QueueServerHandler.class);
	// private AtomicInteger atomicInteger = new AtomicInteger();

	/*
	 * 最后处理业务方法
	 * 
	 * @see
	 * io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.
	 * ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		String[] urls = NettyUtils.byteBufToString(byteBuf);
		QueueServer.taskQueue.addServiceAll(urls);
		// 后续版本定义为协议head传输
		if (urls == null) {

			List<String> responseUrlList = QueueServer.taskQueue.getQueue(10); //

			if (responseUrlList.size() > 0) {
				StringBuffer stringBuffer = new StringBuffer();
				for (int i = 0; i < responseUrlList.size(); i++) {
					stringBuffer.append(responseUrlList.get(i) + "\n");
				}
				ByteBuf resp = NettyUtils.buildProtocol(stringBuffer.toString());
				ctx.write(resp);
			}

		}
		System.out.println("server wait consume data:" + QueueServer.taskQueue.size());

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LogException.appendExceptionLog(log, cause);
		ctx.close();
	}
}
