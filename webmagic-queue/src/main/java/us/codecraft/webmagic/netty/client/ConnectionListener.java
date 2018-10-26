package us.codecraft.webmagic.netty.client;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import us.codecraft.webmagic.netty.common.ConfigProperty;
import us.codecraft.webmagic.netty.common.StandardServerHandler;
import us.codecraft.webmagic.utils.LogException;

public class ConnectionListener implements ChannelFutureListener {
	private static final Log log = LogFactory.getLog(StandardServerHandler.class);

	private QueueClient client;

	public ConnectionListener(QueueClient client) {
		this.client = client;
	}

	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		try {

			if (!channelFuture.isSuccess()) {
				String reConnect = ConfigProperty.serviceConfig.getProperty("reConnect");
				String reTime = ConfigProperty.serviceConfig.getProperty("reTime");
				if (reConnect != null && reConnect.equals("true")) {
					if (!StringUtils.isNumeric(reTime)) {
						reTime = "1000";
					}
					Thread.sleep(Integer.valueOf(reTime));
					log.error("reconnect");
					final EventLoop loop = channelFuture.channel().eventLoop();
					loop.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								client.connect(new Bootstrap(), loop);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								LogException.appendExceptionLog(log, e);
							}
						}
					}, 1L, TimeUnit.SECONDS);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogException.appendExceptionLog(log, e);
		}
	}

}