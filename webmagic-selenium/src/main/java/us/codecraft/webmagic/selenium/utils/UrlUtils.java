package us.codecraft.webmagic.selenium.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class UrlUtils {
	private static Set<String> urlSet = new ConcurrentSkipListSet<String>();

	public static List<String> getLegalList(List<String> urlList) {
		List<String> newurlList = new ArrayList<String>();
		for (int i = 0; i < urlList.size(); i++) {
			String url = urlList.get(i);
			if (legalUrl(url)) {
				newurlList.add(url);
				urlSet.add(url);
			}

		}
		return newurlList;
	}

	public static String getLegalUrls(List<String> urlList) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < urlList.size(); i++) {
			String url = urlList.get(i);
			if (legalUrl(url)) {
				stringBuffer.append(url + "\n");
				urlSet.add(url);

			}

		}
		return stringBuffer.toString();
	}

	private static boolean legalUrl(String url) {
		if (!url.endsWith("#") && !urlSet.contains(url)) {
			return true;
		}
		return false;

	}

}
