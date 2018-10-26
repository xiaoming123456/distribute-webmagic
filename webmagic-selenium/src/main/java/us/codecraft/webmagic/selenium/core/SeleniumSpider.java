package us.codecraft.webmagic.selenium.core;

import java.io.UnsupportedEncodingException;
import java.lang.Thread.State;
import java.net.URLDecoder;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.netty.client.QueueClient;
import us.codecraft.webmagic.netty.common.ConfigProperty;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.utils.FilePath;

public class SeleniumSpider extends Spider {

	protected Thread thread;
	protected TimerTask task;
	public static Log log = LogFactory.getLog(SeleniumSpider.class);

	public static SeleniumSpider create(PageProcessor pageProcessor) {
		return new SeleniumSpider(pageProcessor);
	}

	static {
		PropertyConfigurator.configure(FilePath.getPath("log4j.properties"));

		QueueClient.beginClient(); // 启动netty客户端
	}

	public SeleniumSpider(PageProcessor pageProcessor) {

		super(pageProcessor);
		// 定时接受数据
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getPathData();
			}
		};
		String delay = ConfigProperty.serviceConfig.getProperty("delay");
		String intevalPeriod = ConfigProperty.serviceConfig.getProperty("intevalPeriod");
		Timer timer = new Timer();
		if (!StringUtils.isNumeric(delay)) {
			delay = "0";
		}
		if (!StringUtils.isNumeric(intevalPeriod)) {
			intevalPeriod = "1000";
		}

		// schedules the task to be run in an interval
		timer.scheduleAtFixedRate(task, Long.valueOf(delay), Long.valueOf(intevalPeriod));
		// TODO Auto-generated constructor stub
	}

	public void getPathData() {
		if (scheduler instanceof QueueScheduler) {
			int waitCount = ((QueueScheduler) scheduler).getLeftRequestsCount(null);
			if (waitCount < threadNum) {
				QueueClient.sendData("");
				addQueueTask(QueueClient.taskQueue.getQueue(0));
			}
		} else {
			log.error("scheduler not instanceof QueueScheduler  ");
			task.cancel();

		}
	}

	public void addQueueTask(List<String> requests) {
		for (String url : requests) {
			try {
				url = URLDecoder.decode(url, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			scheduler.push(new Request(url), this);
		}
	}

	public void addQueueTask(String[] requests) {
		for (String url : requests) {
			scheduler.push(new Request(url), this);
		}
	}

	public void runAsync() {
		thread = new Thread(this);
		thread.setDaemon(false);
		thread.start();
		setExitWhenComplete(false);
		try {

			securityExit();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Spider thread(int threadNum) {
		if (threadNum == 0) {
			threadNum = Runtime.getRuntime().availableProcessors() * 2;// 计算机核数启动selenium个数
		}
		super.thread(threadNum);

		return this;
	}

	public void securityExit() throws InterruptedException {
		boolean loop = true;
		while (loop) {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String exit = scanner.next();
			if (exit.equals("exit")) {
				loop = false;
				System.out.println("--准备退出中--");
				task.cancel();
				setExitWhenComplete(true);

				while (thread.getState() != State.TERMINATED) {
					Thread.sleep(1000);
				}

				System.exit(0);
			}
		}
	}
}
