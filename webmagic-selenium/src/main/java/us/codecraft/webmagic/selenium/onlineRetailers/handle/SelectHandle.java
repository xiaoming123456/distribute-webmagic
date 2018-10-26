package us.codecraft.webmagic.selenium.onlineRetailers.handle;

import com.alibaba.fastjson.JSONArray;

import us.codecraft.webmagic.selenium.data.DataManage;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ExecuteParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ResultParam;
import us.codecraft.webmagic.selenium.utils.ScaleArrayList;
import us.codecraft.webmagic.selenium.utils.ScaleLinkedList;

public class SelectHandle extends LabelHandle {
	public static String optionClickStr = "var selSelect = $($(\"select\")[%1$s]).parent();\r\n" + "var selOption;\r\n"
			+ "selSelect.find(\"option\").each(function (){\r\n" + "if($(this).text()=='%2$s'){\r\n"
			+ "selOption = $(this);\r\n" + "}\r\n" + "});\r\n"
			+ "selSelect.find(\"option\").prop(\"selected\",false);\r\n selOption.prop(\"selected\",true);\r\n"
			+ "selOption.focus();\r\n selOption.parent().trigger(\"change\");\r\n ";

	public static String getselectStr = "var selectArray = [];\r\n" + "var optionArray;\r\n"
			+ "$($(\"select\")[%1$s]).parent().find(\"select\").each(function (){\r\n" + "optionArray = [];\r\n"
			+ "selectArray.push(optionArray);\r\n" + "$(this).find(\"option\").each(function(){\r\n"
			+ "optionArray.push($(this).text());\r\n" + "});\r\n" + "}); return JSON.stringify(selectArray);";

	protected void getOptionArray(ExecuteParam executeParam) {

		String selectIndex = scaleVal.substring(ElementHandle.SELECT.length(), scaleVal.length());
		ScaleArrayList<ScaleLinkedList<String>> selectList = new ScaleArrayList<>();
		// option点击事件
		ScaleLinkedList.Execute<String> optionExecute = (e, n, i) -> {
			if (i != 0) {
				js.executeScript(String.format(optionClickStr, selectIndex, e.item));
				String scale = getChooseScale(executeParam.getSelectDocsThread().get(), valAttrMap, webParam, js);
				String price = getJSStr(webParam.getPriceStr(), js);
				DataManage.insertVertor.add("('" + executeParam.getUrl() + "','" + executeParam.getTitle() + "','"
						+ scale + "','" + price + "','" + webParam.getTypeName()
						+ "',from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss')),");
			}
			if (n == selectList.size() - 1) {
				executeParam.getLayerList().deepLayer(executeParam.getLayerLevel());
			}

			selectList.deepLayer(n);
		};

		String selectJson = js.executeScript(String.format(getselectStr, selectIndex)).toString();
		JSONArray selectArray = JSONArray.parseArray(selectJson);

		for (int i = 0; i < selectArray.size(); i++) {
			JSONArray opitonArray = selectArray.getJSONArray(i);
			ScaleLinkedList<String> opitonList = new ScaleLinkedList<String>(optionExecute);
			for (int j = 0; j < opitonArray.size(); j++) {
				opitonList.add(opitonArray.getString(j));
			}
			if (opitonList.getSize() > 0) {
				selectList.add(opitonList);
			}
		}

		selectList.start();
	}

	@Override
	protected ResultParam executeElement(ExecuteParam executeParam) {
		// TODO Auto-generated method stub
		ResultParam resultParam = new ResultParam();
		// 单独的select 等同普通规格
		if (scaleVal.startsWith(ElementHandle.RADIO)) {
			// scaleVal = scaleVal.substring(0, scaleVal.length());
			scale = getChooseScale(executeParam.getSelectDocsThread().get(), valAttrMap, executeParam.getWebParam(),
					js);
			getOptionArray(executeParam);
			resultParam.setDeepLayer(true);
			resultParam.setScale(scale);
		} else {
			resultParam = super.executeLabel(executeParam);
		}

		return resultParam;
	}
}
