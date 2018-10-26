package us.codecraft.webmagic.netty.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import us.codecraft.webmagic.utils.LogException;

public class StandardServerHandler extends ChannelInboundHandlerAdapter {
	private static final Log log = LogFactory.getLog(StandardServerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		buf.readInt();// 不执行 会有头乱码

		byte[] head = new byte[4];
		buf.readBytes(head);

		String headString = new String(head);

		if (!"head".equals(headString)) {
			log.error("request not found head");
			return;
		}
		byte[] body = new byte[4];
		buf.readBytes(body);
		String bodyString = new String(body);

		if (!"body".equals(bodyString)) {
			log.error("request not found body");
			return;
		}
		ctx.fireChannelRead(buf);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();// 刷新后才将数据发出到SocketChannel
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogException.appendExceptionLog(log, cause);
		ctx.close();
	}

}
