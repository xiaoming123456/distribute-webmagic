package us.codecraft.webmagic.selenium.core;

import java.util.List;

import us.codecraft.webmagic.Page;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月16日 下午2:55:52
 */
public class SeleniumPage extends Page {
	private List<String> pathList;

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}

}
