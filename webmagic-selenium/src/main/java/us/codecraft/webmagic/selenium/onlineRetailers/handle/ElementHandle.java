package us.codecraft.webmagic.selenium.onlineRetailers.handle;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;

import us.codecraft.webmagic.selenium.onlineRetailers.param.ExecuteParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ResultParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam.Strategy;
import us.codecraft.webmagic.selenium.utils.StringTool;

public abstract class ElementHandle {
	// 特殊标识分割符v
	// 协议多选框radio 单选框 select
	public final static String RADIO = "radio-";
	public final static String SELECT = "select-";
	public final static String LABEL = "label-";
	// 行分隔符
	public final static String ROW = "row";
	// 有效的select元素的属性名
	public final static String SELECT_DOCS = "selectDocs";

	protected String scale = "";
	protected String val;
	protected JavascriptExecutor js;
	protected HashMap<Integer, String> valAttrMap;
	protected Integer rowNum;
	protected String scaleVal;
	protected String scaleName;
	protected WEBParam webParam;

	protected boolean initialParam(ExecuteParam executeParam) {
		val = executeParam.getVal();
		if (StringUtils.isEmpty(val))
			return false;
		js = executeParam.getJs();
		valAttrMap = executeParam.getValAttrMap();
		webParam = executeParam.getWebParam();
		String[] attrVal = val.split(ElementHandle.ROW);
		rowNum = Integer.valueOf(attrVal[1]);
		val = attrVal[0];
		attrVal = val.split(";");
		if (attrVal.length <= 1)
			return false;
		scaleVal = attrVal[1];
		scaleName = attrVal[0];
		return true;
	}

	protected void destoryParam() {
		scale = "";
		val = null;
		js = null;
		valAttrMap = null;
		rowNum = null;
		scaleVal = null;
		scaleName = null;
		webParam = null;
	}

	/**
	 * 获得文档文本
	 * 
	 * @param doc
	 * @param js
	 * @return
	 */
	public static String getDocStr(String doc, JavascriptExecutor js) {
		return StringTool.extract(js.executeScript("return $('" + doc + "').text();").toString());
	}

	public static String getJSStr(String str, JavascriptExecutor js) {
		return StringTool.extract(js.executeScript(str).toString());
	}

	/**
	 * 获得所有已选规格
	 * 
	 * @param selectDocs
	 * @param valAttrMap
	 * @param webParam
	 * @param js
	 * @return
	 */
	public static String getChooseScale(String selectDocs, HashMap<Integer, String> valAttrMap, WEBParam webParam,
			JavascriptExecutor js) {

		String chooseScale = js.executeScript(webParam.getChooseStr()).toString();
		String selectChooseStr = webParam.getSelectChooseStr();
		// select元素
		if (StringUtils.isNotEmpty(selectDocs) && StringUtils.isNotEmpty(selectChooseStr)) {
			chooseScale += js.executeScript(String.format(selectChooseStr, selectDocs));
		}
		chooseScale = StringTool.extract(chooseScale);
		String[] chooseScales = chooseScale.split(";");
		StringBuffer sb = new StringBuffer();
		for (String cScale : chooseScales) {
			String[] scales = cScale.split(ElementHandle.ROW);
			if (scales.length > 1) {
				String atrr = valAttrMap.get(Integer.valueOf(scales[0]));
				if (StringUtils.isNotEmpty(atrr))
					sb.append(atrr + ":" + scales[1] + ";");
			}
		}
		return sb.toString();
	}

	/**
	 * @param attrTable
	 *            比较的文本
	 * @param compareStr
	 *            html比较元素
	 * @param js
	 * @return
	 */
	protected static String waitRequest(String attrTable, WEBParam webParam, JavascriptExecutor js) {
		if (webParam.getStrategy() == Strategy.wait) {
			sleep(webParam.getWaitTime());

		} else if (webParam.getStrategy() == Strategy.compare) {
			for (int i = 0; i < 200; i++) {
				sleep(webParam.getWaitTime());

				String newAttrTable = getDocStr(webParam.getCompareDoc(), js);
				newAttrTable = StringTool.extract(newAttrTable);
				if (!attrTable.equals(newAttrTable)) {
					// System.out.println("sleep:" + (i + 1) * 5);
					attrTable = newAttrTable;
					break;
				}

			}
		}
		return attrTable;
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultParam execute(ExecuteParam executeParam) {
		if (!initialParam(executeParam))
			return null;
		ResultParam resultParam = executeElement(executeParam);
		destoryParam();
		return resultParam;
	}

	protected abstract ResultParam executeElement(ExecuteParam executeParam);

}
