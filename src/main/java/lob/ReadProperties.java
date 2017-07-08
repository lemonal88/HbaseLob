package lob;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

//读取conf目录下的conf.properties参数
public class ReadProperties {
	private Properties properties;
	//windows路径写法如下
	//private final static String conf_dir="D:\\hyperbase-demo\\src\\main\\resources\\conf\\";
	private final static String conf_dir="/IdeaProjects/XWHBASE/src/main/resources/conf/";

	private String tableName;
	private String threadNums;
	private String downloadDir;
	private String flushSize;
	public ReadProperties() {
		// TODO Auto-generated constructor stub
		properties = new Properties();
		try {
			////读取conf.properties目录中各key的value值
			properties.load(new FileInputStream(conf_dir+"conf.properties"));
			tableName = properties.getProperty("table.name");
			threadNums = properties.getProperty("thread.number");
			downloadDir = properties.getProperty("download.dir");
			flushSize = properties.getProperty("flush.size");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTableName(){
		return tableName;
	}

	public int getThreadNums() {
		return Integer.parseInt(threadNums);
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public int getFlushSize() {
		return Integer.parseInt(flushSize);
	}
}
