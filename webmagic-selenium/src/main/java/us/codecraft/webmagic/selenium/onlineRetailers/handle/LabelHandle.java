package us.codecraft.webmagic.selenium.onlineRetailers.handle;

import org.openqa.selenium.JavascriptExecutor;

import us.codecraft.webmagic.selenium.onlineRetailers.param.ExecuteParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ResultParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam;

public class LabelHandle extends ElementHandle {
	protected static String getClick(String val, String row, WEBParam webParam, JavascriptExecutor js) {
		String num = "";
		try {
			String clickStr = String.format(webParam.getClickStr(), val, row);
			num = js.executeScript(clickStr).toString();
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
		return num;

	}

	@Override
	protected ResultParam executeElement(ExecuteParam executeParam) {
		// TODO Auto-generated method stub
		return executeLabel(executeParam);
	}

	protected ResultParam executeLabel(ExecuteParam executeParam) {
		ResultParam resultParam = new ResultParam();
		String num = getClick(scaleVal, String.valueOf(rowNum), webParam, js);

		if (num.equals("1")) {
			String attrValue = waitRequest(executeParam.getScaleTabThread().get(), webParam, js);
			executeParam.getScaleTabThread().set(attrValue);
			resultParam.setDeepLayer(true);
		}
		if (executeParam.getIndex() != 0) {
			resultParam.setStorage(true);
		}

		return resultParam;

	}
}
