package lob;

import com.transwarp.base.OperateTable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hyperbase.client.HyperbaseAdmin;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.File;

public class LoadData {
    private ConnectHbase connectHbase;
    private HyperbaseAdmin hyperbaseAdmin;
    private Configuration configuration;

    private ReadProperties readProperties;

    private int threadNums;
    private ExecutorService threadPool;

    private LobUtil lobUtil;
    private String tableName;
    private int flushSize;

    private MD5Util md5Util;
    private String fileNameMD5;

    public void init() {
        // TODO Auto-generated constructor stub
        connectHbase = new ConnectHbase();
        readProperties = new ReadProperties();

        hyperbaseAdmin = connectHbase.getHyperbaseAdmin();
        configuration = connectHbase.getConfiguration();

        tableName = readProperties.getTableName();
        threadNums = readProperties.getThreadNums();
        flushSize = readProperties.getFlushSize();

        this.threadPool = Executors.newFixedThreadPool(threadNums);

        lobUtil = new LobUtil(hyperbaseAdmin, configuration);
        lobUtil.createLobTable(tableName, flushSize);
        md5Util = new MD5Util();
    }

    //Load directory
    private int loadFilesRecursive(File dir) {
        String fileName;
        int count;

        if (dir.isDirectory()) {
            File[] allFiles = dir.listFiles();
            count = allFiles.length;
            if (allFiles.length == 0) {
                System.out.println("Empty directory!!");
                return 0;
            }
            for (File f : allFiles) {
                if (!f.isDirectory()) {
                    fileName = f.getName();
                    fileNameMD5 = md5Util.md5crypt(fileName);
                    //线程池执行载入文件,包含绝对路径,文件名表名,文件md5值
                    threadPool.execute(new LoadFileThread(f.getAbsolutePath(), f.getAbsolutePath(), fileName, tableName, lobUtil, fileNameMD5));
                } else {
                    count = count - 1 + loadFilesRecursive(f);
                }
            }
        } else {
            System.out.println("not a dir!");
            return 0;
        }
        return count;
    }

    public void disconnectHbase() {
        connectHbase.close();
        threadPool.shutdown();
    }

    public static void main(String[] args) {
        //String path = args[0];
        //定义图片路径
        String path1 = "/IdeaProjects/pictures/";
        //定义简历路径
        String path2 = "/IdeaProjects/resume/";
        int fileNum1;
        int fileNum2;
        LoadData loadData = new LoadData();
        loadData.init();
        loadData.connectHbase.checkConnection();
        fileNum1 = loadData.loadFilesRecursive(new File(path1));
        fileNum2 = loadData.loadFilesRecursive(new File(path2));
        loadData.disconnectHbase();
        System.out.println("******\n\nUpload files:" + fileNum1);
        System.out.println("******\n\nUpload files:" + fileNum2);
        ReadProperties readProperties = new ReadProperties();
        try {
            OperateTable.getAllRows(readProperties.getTableName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}