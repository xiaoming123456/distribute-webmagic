package us.codecraft.webmagic.selenium.data;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import us.codecraft.webmagic.selenium.utils.DataVector;

public class DataManage {
	public static DataVector<String> insertVertor = new DataVector<>();

	public static String sql;

	private static AtomicInteger comsumeNum = new AtomicInteger();

	// 因火狐有err控制台输出 所以屏蔽所要err输出
	private static ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
	// private static PrintStream printStream = new PrintStream(baoStream);
	static {
		// System.setErr(printStream);// 清空err信息
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DataManage.consumeData();
			}
		}, 1000, 2000);
		// 优雅退出时清空资源
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				while (insertVertor.size() > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				timer.cancel();
			}
		});

	}

	/**
	 * 消费数据
	 */
	public static void consumeData() {
		// 并发控制
		System.out.println("wait consume data:" + insertVertor.size());
		if (insertVertor.size() == 0)
			return;
		// baoStream.reset();// 清空err信息

		DataVector<String> tempVertor = new DataVector<>(insertVertor);

		StringBuffer stringBuffer = new StringBuffer(sql);
		for (int i = 0; i < tempVertor.size(); i++) {
			// 1000条sql 拼接为1条
			if (i != 0 && i % 1000 == 0) {

				stringBuffer.setLength(stringBuffer.length() - 1);
				JDBCModel.insert(stringBuffer.toString());
				stringBuffer = new StringBuffer(sql);
			}
			stringBuffer.append(tempVertor.get(i));
			comsumeNum.incrementAndGet();
		}
		stringBuffer.setLength(stringBuffer.length() - 1);
		System.out.println("sum consume data" + comsumeNum.get());
		JDBCModel.insert(stringBuffer.toString());
	}

}
