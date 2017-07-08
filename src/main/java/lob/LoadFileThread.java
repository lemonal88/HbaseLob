package lob;

import java.io.File;
import java.io.IOException;

public class LoadFileThread implements Runnable {
    private String fileName;
    private String filePath1;
    private String filePath2;
    private String table;
    private LobUtil lobUtil;
    private String fileNameMD5;

    public LoadFileThread(String filePath1, String filePath2, String fileName, String tableName, LobUtil lobUtil, String md5String) {
        // TODO Auto-generated constructor stub
        this.fileName = fileName;
        this.filePath1 = filePath1;
        this.filePath2 = filePath2;
        this.table = tableName;
        this.lobUtil = lobUtil;
        this.fileNameMD5 = md5String;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            byte[] fileData1 = FileUtils.getFileBytes(new File(filePath1));
            byte[] fileData2 = FileUtils.getFileBytes(new File(filePath2));
            String rowkey = fileNameMD5 + String.valueOf(System.currentTimeMillis());
            System.out.println("Thread " + Thread.currentThread().getId() + " UPLOADING:" + filePath1 + "----- row key:" + rowkey);
            System.out.println("Thread " + Thread.currentThread().getId() + " UPLOADING:" + filePath2 + "----- row key:" + rowkey);
            lobUtil.putLob(table, rowkey, fileName, fileData1, fileData2);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("File not found.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}