package us.codecraft.webmagic.netty.common;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class NettyUtils {
	public static ByteBuf buildProtocol(String content) {
		UnpooledByteBufAllocator allocator = new UnpooledByteBufAllocator(false);
		ByteBuf buffer = allocator.buffer(64);
		content = "body" + content;
		buffer.writeInt(content.length() + 4);
		buffer.writeBytes("head".getBytes());
		buffer.writeBytes(content.getBytes());

		return buffer;
	}

	public static String[] byteBufToString(ByteBuf byteBuf) throws UnsupportedEncodingException {

		byte[] req = new byte[byteBuf.readableBytes()];
		if (req.length == 0)
			return null;
		byteBuf.readBytes(req);
		String str = new String(req, "UTF-8");
		String[] strs = str.split("\n");
		return strs;
	}
}
