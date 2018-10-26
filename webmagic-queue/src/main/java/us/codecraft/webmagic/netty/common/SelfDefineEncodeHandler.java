package us.codecraft.webmagic.netty.common;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import us.codecraft.webmagic.utils.LogException;

public class SelfDefineEncodeHandler extends ByteToMessageDecoder {
	private static final Log log = LogFactory.getLog(SelfDefineEncodeHandler.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf bufferIn, List<Object> out) throws Exception {
		if (bufferIn.readableBytes() < 4) {
			return;
		}
		int beginIndex = bufferIn.readerIndex();
		int length = bufferIn.readInt();

		if (bufferIn.readableBytes() < length) {
			bufferIn.readerIndex(beginIndex);
			return;
		}
		bufferIn.readerIndex(beginIndex + 4 + length);
		ByteBuf otherByteBufRef = bufferIn.slice(beginIndex, 4 + length);
		otherByteBufRef.retain();
		out.add(otherByteBufRef);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogException.appendExceptionLog(log, cause);
		ctx.close();
	}

}
