package us.codecraft.webmagic.selenium.downloader;

import org.openqa.selenium.WebDriver;

import us.codecraft.webmagic.Page;

public abstract class WebEvent {

	public abstract Page onEvent(WebDriver webDriver);

}
