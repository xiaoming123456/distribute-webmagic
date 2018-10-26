package us.codecraft.webmagic.selenium.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.netty.client.QueueClient;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public abstract class SeleniumProcessor implements PageProcessor {
	protected static SeleniumSpider seleniumSpider;
	private Site site;
	protected static List<String> regexPathList = new ArrayList<>();

	public static Log log = LogFactory.getLog(SeleniumProcessor.class);

	/*
	 * 获取发送有效路径
	 * 
	 * @see
	 * us.codecraft.webmagic.processor.PageProcessor#process(us.codecraft.webmagic.
	 * Page)
	 */
	@Override
	public void process(Page page) {
		if (CollectionUtils.isEmpty(regexPathList)) {
			System.out.println("not found regexPathList");
			log.error("not found regexPathList");
			return;
		}
		List<String> pathList = new ArrayList<>();
		Selectable selectable = page.getHtml().links();
		for (String regexPath : regexPathList) {
			pathList.addAll(selectable.regex(regexPath).all());
		}
		if (page instanceof SeleniumPage) {
			List<String> pagePathList = ((SeleniumPage) page).getPathList();
			if (pagePathList != null)
				pathList.addAll(pagePathList);
		}

		String paths = getLegalPaths(pathList);
		QueueClient.sendData(paths);
		seleniumSpider.addQueueTask(QueueClient.taskQueue.getQueue(0));
	}

	@Override
	public Site getSite() {
		if (null == site) {
			site = Site.me().setDomain("business").setSleepTime(1000);
		}
		return site;
	}

	// 避免重复发送
	private static Set<String> pathSet = new ConcurrentSkipListSet<String>();

	private String getLegalPaths(List<String> pathList) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < pathList.size(); i++) {
			String path = pathList.get(i);
			path = replacePath(path);
			if (legalPath(path)) {
				try {
					path = URLEncoder.encode(path, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stringBuffer.append(path + "\n");
				// pathSet.add(path);
			}
		}
		return stringBuffer.toString();
	}

	public String replacePath(String path) {
		return path;
	}

	public boolean legalPath(String path) {
		if (!path.endsWith("#") && !pathSet.contains(path)) {//
			return true;
		}
		return false;

	}

}
