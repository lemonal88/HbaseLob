package lob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//字节流数组的定义
public class FileUtils {
	//上传文件
	public static byte[] getFileBytes(File file) throws Exception {
		FileInputStream fis;
		fis = new FileInputStream(file);
		byte[] data = new byte[fis.available()];
		fis.read(data);
		fis.close();
		return data;
	}

	//下载文件
	public static File getFileFromByte(byte[] bytes, String path) {
		if (bytes == null) {
			return null;
		}
		File f = new File(path);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
		}
		return f;
	}
}
