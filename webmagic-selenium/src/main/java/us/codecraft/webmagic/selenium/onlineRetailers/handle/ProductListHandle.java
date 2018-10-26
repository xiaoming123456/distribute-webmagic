package us.codecraft.webmagic.selenium.onlineRetailers.handle;

import java.util.Arrays;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selenium.core.SeleniumPage;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ExecuteParam;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月16日 下午1:48:13
 */
public class ProductListHandle {
	private String getProductStr;

	private String pauseStr;

	public Page execute(ExecuteParam executeParam) {
		SeleniumPage seleniumPage = new SeleniumPage();
		StringBuffer productSB = new StringBuffer();
		String pause = "false";
		do {
			String products = executeParam.getJs().executeScript(getProductStr).toString();
			productSB.append(products);
			pause = executeParam.getJs().executeScript(pauseStr).toString();
		} while (pause.equals("true"));
		String[] products = productSB.toString().split(";");
		seleniumPage.setPathList(Arrays.asList(products));
		return seleniumPage;
	}

	public String getGetProductStr() {
		return getProductStr;
	}

	public void setGetProductStr(String getProductStr) {
		this.getProductStr = getProductStr;
	}

	public String getPauseStr() {
		return pauseStr;
	}

	public void setPauseStr(String pauseStr) {
		this.pauseStr = pauseStr;
	}

}
