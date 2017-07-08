package com.transwarp.base;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemo on 17/3/30.
 */
public class OperateTable {
    // 声明静态配置
    private static Configuration conf = null;

    static {
        //创建config
        Configuration config = new Configuration();
        //添加集群中的zookeeper,可根据hbase-site.xml进行配置
        config.set("hbase.zookeeper.quorum", "aubdbte01,aubdbte02,aubdbte03");
        //添加客户端连接端口,默认为2181
        config.set("hbase.zookeeper.property.clientPort", "2181");
        //添加znode节点hyperbase1,根据manager界面中查看hyperbase名称
        config.set("zookeeper.znode.parent", "/hyperbase1");

        //创建并添加配置
        conf = HBaseConfiguration.create(config);
    }

    // 创建数据库表
    public static void createTable(String tableName, String[] columnFamilys)
            throws Exception {
        // 新建一个数据库管理员
        HBaseAdmin admin = new HBaseAdmin(conf);

        if (admin.tableExists(tableName)) {
            System.out.println("表已经存在");
            System.exit(0);
        } else {
            // 对新建一个表的描述
            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
            // 在描述里添加列族
            for (String columnFamily : columnFamilys) {
                tableDesc.addFamily(new HColumnDescriptor(columnFamily));
            }
            // 根据配置好的描述建表
            admin.createTable(tableDesc);
            System.out.println("创建表成功");
        }
    }

    // 删除数据库表
    public static void deleteTable(String tableName) throws Exception {
        // 新建一个数据库管理员
        HBaseAdmin admin = new HBaseAdmin(conf);

        if (admin.tableExists(tableName)) {
            // 关闭一个表
            admin.disableTable(tableName);
            // 删除一个表
            admin.deleteTable(tableName);
            System.out.println("删除表成功");

        } else {
            System.out.println("删除的表不存在");
            System.exit(0);
        }
    }

    // 添加一条数据
    public static void addRow(String tableName, String row,
                              String columnFamily, String column, String value) throws Exception {
        HTable table = new HTable(conf, tableName);
        Put put = new Put(Bytes.toBytes(row));
        // 参数出分别：列族、列、值
        put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column),
                Bytes.toBytes(value));
        table.put(put);
    }

    // 删除一条数据
    public static void delRow(String tableName, String row) throws Exception {
        HTable table = new HTable(conf, tableName);
        Delete del = new Delete(Bytes.toBytes(row));
        table.delete(del);
    }

    // 删除多条数据
    public static void delMultiRows(String tableName, String[] rows)
            throws Exception {
        HTable table = new HTable(conf, tableName);
        List<Delete> list = new ArrayList<Delete>();

        for (String row : rows) {
            Delete del = new Delete(Bytes.toBytes(row));
            list.add(del);
        }

        table.delete(list);
    }

    // get row
    public static void getRow(String tableName, String row) throws Exception {
        HTable table = new HTable(conf, tableName);
        Get get = new Get(Bytes.toBytes(row));
        Result result = table.get(get);
        // 输出结果
        for (KeyValue rowKV : result.raw()) {
            System.out.print("Row Name: " + new String(rowKV.getRow()) + " ");
            System.out.print("Timestamp: " + rowKV.getTimestamp() + " ");
            System.out.print("column Family: " + new String(rowKV.getFamily()) + " ");
            System.out.print("Row Name:  " + new String(rowKV.getQualifier()) + " ");
            System.out.println("Value: " + new String(rowKV.getValue()) + " ");
        }
    }

    // get all records
    public static void getAllRows(String tableName) throws Exception {
        HTable table = new HTable(conf, tableName);
        Scan scan = new Scan();
        ResultScanner results = table.getScanner(scan);
        // 输出结果
        for (Result result : results) {
            for (KeyValue rowKV : result.raw()) {
                System.out.print("Row Name: " + new String(rowKV.getRow()) + " ");
                System.out.print("Timestamp: " + rowKV.getTimestamp() + " ");
                System.out.print("column Family: " + new String(rowKV.getFamily()) + " ");
                System.out
                        .print("Row Name:  " + new String(rowKV.getQualifier()) + " ");
                System.out.println("Value: " + new String(rowKV.getValue()) + " ");
            }
        }
    }

    // main
    public static void main(String[] args) {
        try {
            String tableName = "transwarp";

            // 第一步：创建数据库表：“transwarp”,定义info和picture两个列簇
            String[] columnFamilys = {"info", "picture"};
            OperateTable.createTable(tableName, columnFamilys);

            // 第二步：向数据表的添加数据
            // 添加第一行数据
            OperateTable.addRow(tableName, "nemo", "info", "age", "20");
            OperateTable.addRow(tableName, "nemo", "info", "sex", "boy");
            OperateTable.addRow(tableName, "nemo", "picture", "bytes", "128");
            // 添加第二行数据
            OperateTable.addRow(tableName, "hang", "info", "age", "19");
            OperateTable.addRow(tableName, "hang", "info", "sex", "boy");
            OperateTable.addRow(tableName, "hang", "picture", "bytes", "90");

            OperateTable
                    .addRow(tableName, "tang", "picture", "bytes", "90");
            // 添加第三行数据
            OperateTable.addRow(tableName, "tao", "info", "age", "18");
            OperateTable.addRow(tableName, "tao", "info", "sex", "girl");
            OperateTable
                    .addRow(tableName, "tao", "picture", "bytes", "100");

            // 第三步：获取一条数据
            System.out.println("获取一条数据");
            OperateTable.getRow(tableName, "nemo");
            // 第四步：获取所有数据
            System.out.println("获取所有数据");
            OperateTable.getAllRows(tableName);
            // 第五步：删除一条数据
            System.out.println("删除一条数据");
            OperateTable.delRow(tableName, "nemo");
            OperateTable.getAllRows(tableName);
            // 第六步：删除多条数据
            System.out.println("删除多条数据");
            String[] rows = {"hang", "tao"};
            OperateTable.delMultiRows(tableName, rows);
            OperateTable.getAllRows(tableName);
            // 第八步：删除表
            System.out.println("删除表");
            OperateTable.deleteTable(tableName);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
