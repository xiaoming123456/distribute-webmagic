package us.codecraft.webmagic.samples.yunyin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selenium.core.SeleniumPage;
import us.codecraft.webmagic.selenium.data.DataManage;
import us.codecraft.webmagic.selenium.downloader.SeleniumDownloader;
import us.codecraft.webmagic.selenium.downloader.WebEvent;
import us.codecraft.webmagic.selenium.onlineRetailers.StandardWebEvent;
import us.codecraft.webmagic.selenium.onlineRetailers.handle.ElementKind;
import us.codecraft.webmagic.selenium.onlineRetailers.handle.ProductListHandle;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam.Strategy;

/**
 *
 * 云印网
 * 
 *
 * 
 */
public class YunyinProcessor implements PageProcessor {

	private Site site;

	private Set<String> pathset = new ConcurrentSkipListSet<String>();

	public static AtomicInteger comsumeNum = new AtomicInteger();

	@Override
	public void process(Page page) {
		// List<String> pathList =
		page.getHtml().links().regex("http://www\\.ininin\\.com/product/\\d+\\.html").all();

		List<String> pathList = page.getHtml().links().regex("http://www\\.ininin\\.com/search\\.html#keyword.*").all();
		System.out.println(pathList.size());

		if (page instanceof SeleniumPage) {
			List<String> pagePathList = ((SeleniumPage) page).getPathList();
			if (pagePathList != null)
				pathList.addAll(pagePathList);
		}
		// System.out.println(page.getHtml());
		List<String> newPathList = new ArrayList<String>();
		for (int i = 0; i < pathList.size(); i++) {
			String path = pathList.get(i);
			if (path.contains("html#keyword")) {
				path = path.replace("html#keyword", "html?keyword");
			}

			if (!path.endsWith("#") && !pathset.contains(path)) {
				newPathList.add(path);
				pathset.add(path);
			}

		}
		// newPathList.add("https://www.intuan.com/product/makeup/115");
		// newPathList.add("https://www.intuan.com/product/makeup/119");
		// newPathList.add("https://www.intuan.com/product/makeup/135");
		// newPathList.add("https://www.intuan.com/product/makeup/95");
		// newPathList.add("https://www.intuan.com/product/makeup/52");
		// newPathList.add("https://www.intuan.com/product/makeup/92");

		page.addTargetRequests(newPathList);

		// System.out.println(page.getHtml().xpath("//span[@id='hbPrice']").toString());

	}

	@Override
	public Site getSite() {
		if (null == site) {
			site = Site.me().setDomain("play.google.com").setSleepTime(300);
		}
		return site;
	}

	public static void main(String[] args) {
		DataManage.sql = "insert into goodsInfoTest1(url,title,scale,descPrice,typeName,time) values";

		String scaleStr = "var attrMap = {};" + "var attrValArray = null;"
				+ "var  productParam = $('div[id^=\"product_params-\"]');" + "" + "if(productParam.length>0){"
				+ "	productParam.find('div.vals.clearfix').each( function() {"
				+ "			var attr = $(this).attr('data-name').replace(/\\n/g, '').replace(/ /g, '').replace(/\\//g, '丨').replace(/：/g, '');"
				+ "		" + "			attrValArray = new Array();"
				+ "			eval(\"attrMap.\"+attr+\"=attrValArray\");"
				+ "			$(this).find('a.val').each( function() {" + "			"
				+ "			var attrVal = $(this).text();" + "			if(attrVal.search(\"自定义\") == -1)"
				+ "				attrValArray.push(attrVal+\";\"+$(this).attr(\"data-value\"));" + "			});"
				+ "	}); " + "}else{" + "" + "$('div[id^=\"template-product_params\"] span.vals').each( function() {"
				+ "			var attr = $(this).attr('data-name').replace(/\\n/g, '').replace(/ /g, '').replace(/\\//g, '丨').replace(/：/g, '');"
				+ "		" + "			attrValArray = new Array();"
				+ "			eval(\"attrMap.\"+attr+\"=attrValArray\");"
				+ "			$(this).find('a.val').each( function() {" + "			"
				+ "			var attrVal = $(this).text();" + "			if(attrVal.search(\"自定义\") == -1)"
				+ "				attrValArray.push(attrVal+\";\"+$(this).attr(\"data-value\"));" + "			});"
				+ "	}); " + "} " + "return  JSON.stringify(attrMap);";

		String priceStr = "var price = $(\"#data-price\").text();" + "if(!price)"
				+ " price = $('[id^=\"data-discountAmount\"]').text();" + "return price;";
		String chooseStr = "var attrVal = \"\";" + "$(\"a.sel\").each(function (){" + "attrVal+=$(this).text()+\";\";"
				+ "}); return attrVal";
		WEBParam webParam = new WEBParam("云印", priceStr, scaleStr, chooseStr,
				" return $('div.attr.clearfix a.val[data-value=\"%1$s\"]').click().length;", "h1.headline a", "", 200,
				Strategy.wait);
		ProductListHandle productListHandle = new ProductListHandle();
		productListHandle
				.setGetProductStr("var paths= \"\";\n" + "$(\"#template_product_list_view a\").each(function(){\n"
						+ "\n" + "paths +=$(this).attr(\"href\")+\";\";\n" + "}); return paths;");
		productListHandle.setPauseStr(
				"if($(\"#data_curPage\").text()!=\"\"&&$(\"#data_curPage\").text()!=$(\"#data_totalPage\").text()){\n"
						+ "$(\".pagin .after\").click();return  \"true\";\n" + "}else {return\"false\" }");

		webParam.setProductListHandle(productListHandle);
		WebEvent webEvent = new StandardWebEvent().setWEBParam(webParam, ElementKind.select);

		Spider.create(new YunyinProcessor()).thread(1).addPipeline(new ConsolePipeline())
				.setDownloader(new SeleniumDownloader(webEvent)).addUrl("http://www.ininin.com/").runAsync();

		// http://www.ininin.com/
		// http://www.ininin.com/product/200553.html
		// http://www.ininin.com/product/200550.html
	}
}
