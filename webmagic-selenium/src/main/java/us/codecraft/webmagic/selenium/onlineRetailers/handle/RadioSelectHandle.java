package us.codecraft.webmagic.selenium.onlineRetailers.handle;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;

import us.codecraft.webmagic.selenium.onlineRetailers.param.ExecuteParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ResultParam;

public class RadioSelectHandle extends SelectHandle {
	public static String optionClickStr = "var selSelect = $($(\"input[type='checkbox']\")[%1$s]).parent();\r\n"
			+ "var selOption;\r\n" + "selSelect.find(\"option\").each(function (){\r\n"
			+ "if($(this).text()=='%2$s'){\r\n" + "selOption = $(this);\r\n" + "}\r\n" + "});\r\n" + "\r\n"
			+ "selSelect.find(\"option\").prop(\"selected\",true);" + "selOption.prop(\"selected\",\"selected\");\r\n"
			+ "selOption.parent().trigger(\"change\");\r\n";

	public static String getselectStr = "var selectArray = [];\r\n" + "var optionArray;\r\n"
			+ "$($(\"input[type='checkbox']\")[%1$s]).parent().find(\"select\").each(function (){\r\n"
			+ "optionArray = [];\r\n" + "selectArray.push(optionArray);\r\n"
			+ "$(this).find(\"option\").each(function(){\r\n" + "optionArray.push($(this).text());\r\n" + "});\r\n"
			+ "}); return JSON.stringify(selectArray);";

	public static String radioClickStr = "$($(\"input[type='checkbox']\")[%1$s]).click()";

	protected void clickRadio(JavascriptExecutor js, String index) {
		js.executeScript(String.format(radioClickStr, index));
	}

	@Override
	protected ResultParam executeElement(ExecuteParam executeParam) {
		// TODO Auto-generated method stub
		ResultParam resultParam = new ResultParam();
		boolean havaselect = false;
		// 单独的select 等同普通规格
		if (scaleVal.startsWith(ElementHandle.RADIO)) {
			// 此处页面布局 大于协议规则
			int radioLength = ElementHandle.RADIO.length();
			if (scaleVal.contains(ElementHandle.SELECT)) {

				radioLength += ElementHandle.SELECT.length();
				havaselect = true;
			}
			// 通过选项的父节点获得当前这行的元素
			// select元素有多个选项就脑裂成多个获得价格的事件
			scaleVal = scaleVal.substring(radioLength, scaleVal.length());
			clickRadio(js, scaleVal);

			String attr = executeParam.getValAttrMap().get(rowNum);
			if (StringUtils.isNotEmpty(scaleName)) {
				scale = attr + ":" + scaleName;
			}
			if (havaselect) {// 若为radioselect 将不进入普通体系
				getOptionArray(executeParam);

			} else {
				resultParam.setStorage(true);
				resultParam.setDeepLayer(true);

				scale += ";";
			}

			String attrValue = waitRequest(executeParam.getScaleTabThread().get(), executeParam.getWebParam(), js);
			executeParam.getScaleTabThread().set(attrValue);

			clickRadio(js, scaleVal);
			resultParam.setScale(scale);
		} else {
			resultParam = super.executeLabel(executeParam);
		}

		return resultParam;
	}

}
