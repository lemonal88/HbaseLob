package lob;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hyperbase.client.HyperbaseAdmin;

import java.io.IOException;

public class ConnectHbase {
    //加载配置文件
    private Configuration configuration;
    //定义配置文件根目录
    private final static String conf_dir = "./conf/";
    //定义管理员账户
    private HBaseAdmin admin;
    private HyperbaseAdmin hyperbaseAdmin;

    public ConnectHbase() {
        // TODO Auto-generated constructor stub
        // 读取hbase-site.xml配置文件
        configuration = new Configuration();
        configuration.addResource(conf_dir + "hbase-site.xml");

        try {
            admin = new HBaseAdmin(configuration);
            hyperbaseAdmin = new HyperbaseAdmin(configuration);
        } catch (MasterNotRunningException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void checkConnection() {
        try {
            if (admin.tableExists("xxxxxxxx") == false) {
                System.out.println("hbase connected succed");
                return;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("hbase connected failed");
    }

    public HBaseAdmin getHbaseAdmin() {
        return admin;
    }

    public HyperbaseAdmin getHyperbaseAdmin() {
        return hyperbaseAdmin;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void close() {
        try {
            admin.close();
            hyperbaseAdmin.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
