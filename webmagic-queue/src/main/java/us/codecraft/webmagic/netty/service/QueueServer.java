/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.codecraft.webmagic.netty.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import us.codecraft.webmagic.netty.common.ConfigProperty;
import us.codecraft.webmagic.netty.common.SelfDefineEncodeHandler;
import us.codecraft.webmagic.netty.common.StandardServerHandler;
import us.codecraft.webmagic.utils.FilePath;
import us.codecraft.webmagic.utils.LogException;
import us.codecraft.webmagic.utils.TaskQueue;

public class QueueServer {
	static {
		PropertyConfigurator.configure(FilePath.getPath("log4j.properties"));

	}
	public static final TaskQueue<String> taskQueue = new TaskQueue<>();
	private static final Log log = LogFactory.getLog(StandardServerHandler.class);

	public void beginService() throws Exception {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024 * 1024).childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChildChannelHandler());
			// 绑定端口，同步等待成功
			String servicePort = ConfigProperty.serviceConfig.getProperty("servicePort");
			if (StringUtils.isEmpty(servicePort)) {
				log.error(" clientPort is not found");
				return;
			} else if (!StringUtils.isNumeric(servicePort)) {
				log.error(" clientPort is not numeric");
				return;
			}

			ChannelFuture f = b.bind(Integer.valueOf(servicePort)).sync();

			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogException.appendExceptionLog(log, e);
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			arg0.pipeline().addLast(new SelfDefineEncodeHandler());
			arg0.pipeline().addLast(new StandardServerHandler());
			arg0.pipeline().addLast(new QueueServerHandler());

		}

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		new QueueServer().beginService();
	}
}
