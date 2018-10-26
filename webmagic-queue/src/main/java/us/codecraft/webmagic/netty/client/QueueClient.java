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
package us.codecraft.webmagic.netty.client;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import us.codecraft.webmagic.netty.common.ConfigProperty;
import us.codecraft.webmagic.netty.common.NettyUtils;
import us.codecraft.webmagic.netty.common.SelfDefineEncodeHandler;
import us.codecraft.webmagic.netty.common.StandardServerHandler;
import us.codecraft.webmagic.utils.LogException;
import us.codecraft.webmagic.utils.TaskQueue;

public class QueueClient {
	public static final QueueClient queueClient = new QueueClient();
	public static final TaskQueue<String> taskQueue = new TaskQueue<>();
	private SocketChannel socketChannel;
	private static final Log log = LogFactory.getLog(StandardServerHandler.class);

	public void connect(Bootstrap bootstrap, EventLoopGroup eventLoop) {
		// 配置客户端NIO线程组
		try {
			bootstrap.group(eventLoop).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new SelfDefineEncodeHandler()).addLast(new StandardServerHandler())
									.addLast(new QueueClientHandler());
						}
					});

			String serviceIp = ConfigProperty.serviceConfig.getProperty("serviceIp");
			String clientPort = ConfigProperty.serviceConfig.getProperty("clientPort");

			if (StringUtils.isEmpty(serviceIp)) {
				log.error(" serviceIp is not found");
				return;
			}
			if (StringUtils.isEmpty(clientPort)) {
				log.error(" clientPort is not found");
				return;
			} else if (!StringUtils.isNumeric(clientPort)) {
				log.error(" clientPort is not numeric");
				return;
			}

			// 发起异步连接操作
			ChannelFuture future = bootstrap.connect(serviceIp, Integer.valueOf(clientPort))
					.addListener(new ConnectionListener(this)).sync();

			if (future.isSuccess()) {
				socketChannel = (SocketChannel) future.channel();

				System.out.println("connect server  success!");
			} else {
				log.error(" connect server  fail!");
			}
			// 当代客户端链路关闭
			future.channel().closeFuture().sync();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogException.appendExceptionLog(log, e);
		} finally {
			// 优雅退出，释放NIO线程组
			// eventLoop.shutdownGracefully();
		}

	}

	public static void beginClient() {
		Thread clientThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				EventLoopGroup group = new NioEventLoopGroup();
				queueClient.connect(new Bootstrap(), group);

			}
		});
		clientThread.setDaemon(true);
		clientThread.start();

	}

	public static void sendData(String wirteStr) {
		ByteBuf buffer = NettyUtils.buildProtocol(wirteStr);
		queueClient.getSocketChannel().writeAndFlush(buffer);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		beginClient();
		Thread.sleep(1000);

		// for (int j = 0; j < 19; j++) {
		// Thread.sleep(1);
		//
		// StringBuffer stringBuffer = new StringBuffer();
		// for (int i = 0; i < 200; i++) {
		// stringBuffer.append("http://www.ininin.com/search.html?keyword=经典&type=6" + i
		// + "\n");
		// }
		//
		// String wirteStr = stringBuffer.toString();
		// sendData(wirteStr);
		// }
		// Thread.sleep(1000);
		sendData(URLEncoder.encode("http://www.ininin.com/search.html?keyword=经典&type=61", "utf-8"));
		sendData(URLEncoder.encode("http://www.ininin.com/search.html?keyword=经典&type=61", "utf-8"));
		sendData(URLEncoder.encode("http://www.ininin.com/search.html?keyword=经典&type=61", "utf-8"));
		sendData(URLEncoder.encode("http://www.ininin.com/search.html?keyword=经典&type=61", "utf-8"));

		// sendData(URLEncoder.encode("http://www.ininin.com/search.html?keyword=经典&type=62",
		// "utf-8"));
		// sendData(URLEncoder.encode("http://www.ininin.com/search.html?keyword=经典&type=62",
		// "utf-8"));
		System.out.println(11);
		sendData("");
		Thread.sleep(1000);
		System.out.println(taskQueue.size());
	}

	public SocketChannel getSocketChannel() {
		while (socketChannel == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogException.appendExceptionLog(log, e);
			}
		}
		return socketChannel;
	}
}
