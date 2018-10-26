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
package us.codecraft.webmagic.netty.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import us.codecraft.webmagic.netty.common.NettyUtils;
import us.codecraft.webmagic.utils.LogException;

public class QueueClientHandler extends ChannelInboundHandlerAdapter {

	private static final Log log = LogFactory.getLog(QueueClientHandler.class);

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
		String[] tasks = NettyUtils.byteBufToString(byteBuf);
		QueueClient.taskQueue.addClientAll(tasks);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// 释放资源
		LogException.appendExceptionLog(log, cause);
		ctx.close();
	}

}
