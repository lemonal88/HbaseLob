package lob;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class GetData {
    private ConnectHbase connectHbase;
    private Configuration configuration;
    private String tableName;
    private String downloadDir;
    private ReadProperties readProperties;
    private MD5Util md5Util;

    public GetData() {
        // TODO Auto-generated constructor stub
        connectHbase = new ConnectHbase();
        readProperties = new ReadProperties();
        md5Util = new MD5Util();

        configuration = connectHbase.getConfiguration();
        tableName = readProperties.getTableName();
        downloadDir = readProperties.getDownloadDir();
    }

    public void getFile(String fileName) {
        try {
            HTable hTable = new HTable(configuration, tableName);
            String fileNameMD5 = md5Util.md5crypt(fileName);

            String result_filename;
            byte[] result_filedata;

            int result_fileCount = 0;

            byte[] startRowKey = Bytes.toBytes(fileNameMD5);
            byte[] stopRowKey = Bytes.toBytes(fileNameMD5 + "9");

            Scan scan = new Scan();
            scan.setStartRow(startRowKey);
            scan.setStopRow(stopRowKey);
            ResultScanner scanner = hTable.getScanner(scan);
            for (Result r : scanner) {
                result_filename = Bytes.toString(r.getValue(Bytes.toBytes("info"), Bytes.toBytes("id")));
                if (fileName.compareTo(result_filename) == 0) {
                    result_fileCount++;
                    result_filedata = r.getValue(Bytes.toBytes("pictures"), Bytes.toBytes("bytes1"));
                    FileUtils.getFileFromByte(result_filedata, downloadDir + fileName);
                }
            }
            if (result_fileCount != 0)
                System.out.println("Download " + result_fileCount + " files");
            else
                System.out.println("Not found:" + fileName);

            hTable.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void disconnectHbase() {
        connectHbase.close();
    }

    public static void main(String[] args) {
        GetData get = new GetData();
        //get.getFile(args[0]);
        get.getFile("1.png");
        get.getFile("resume.doc");
        get.disconnectHbase();
    }

}
