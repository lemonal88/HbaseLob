package lob;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.protobuf.generated.HyperbaseProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hyperbase.client.HyperbaseAdmin;
import org.apache.hadoop.hyperbase.datatype.TypeException;
import org.apache.hadoop.hyperbase.secondaryindex.LOBIndex;
import org.apache.hadoop.hyperbase.secondaryindex.IndexedColumn;

import java.io.IOException;

public class LobUtil {
    //定义管理员账户
    private HyperbaseAdmin admin;
    //定义列簇1
    private String family1 = "info";
    //定义列簇1中的列1
    private String f1_q1 = "id";
    //定义列簇2
    private String family2 = "pictures";
    //定义列簇2中的列1(存放照片)
    private String f2_q1 = "bytes1";
    //定义列簇2中的列2(存放简历)
    private String f2_q2 = "bytes2";


    //定义索引名
    private String indexName = "index";
    private int flushSize;
    private Configuration conf;
    private final static int REGIONS = 16;

    public LobUtil(HyperbaseAdmin admin, Configuration conf) {
        // TODO Auto-generated constructor stub
        this.admin = admin;
        this.conf = conf;
    }

    public void createLobTable(String tableName, int size) {
        byte[][] splitKeys = new byte[REGIONS - 1][];
        flushSize = size;
        try {
            if (admin.tableExists(TableName.valueOf(tableName))) {
                System.out.println("Table already exists!!---" + tableName);
                return;
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            tableDescriptor.addFamily(new HColumnDescriptor(family1));

            HColumnDescriptor hColumnDescriptor1 = new HColumnDescriptor(family2);
            hColumnDescriptor1.setValue(HConstants.HREGION_MEMSTORE_COLUMNFAMILY_FLUSH_SIZE, String.valueOf(flushSize));
            tableDescriptor.addFamily(hColumnDescriptor1);


            //int regionNumsLen = String.valueOf(regionNums-1).length();
            for (int i = 0; i < REGIONS - 1; i++) {
                if (i < 9) {
                    splitKeys[i] = Bytes.toBytes(String.valueOf(i + 1));
                } else {
                    splitKeys[i] = Bytes.toBytes(String.valueOf((char) (i + 1 + 55)));
                }
            }
            //添加索引名
            admin.createTable(tableDescriptor, null, splitKeys);
            addLOB(TableName.valueOf(tableName), family1, f1_q1, indexName);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addLOB(TableName table, String indexdFamily, String indexdQualifer, String LOBfamily) {

        try {
            HyperbaseProtos.SecondaryIndex.Builder LOBbuilder =
                    HyperbaseProtos.SecondaryIndex.newBuilder();
            LOBbuilder.setClassName(LOBIndex.class.getName());
            LOBbuilder.setUpdate(true);
            LOBbuilder.setDcop(true);
            IndexedColumn column = new IndexedColumn(Bytes.toBytes(indexdFamily), Bytes.toBytes(indexdQualifer));
            LOBbuilder.addColumns(column.toPb());
            admin.addLob(table, new LOBIndex(LOBbuilder.build()), Bytes.toBytes(LOBfamily), false, 1);
        } catch (TypeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void putLob(String tableName, String row, String filename, byte[] fileData1, byte[] fileData2) {
        byte[] rowkey = Bytes.toBytes(row);
        try {
            HTable htable = new HTable(conf, tableName);
            Put put = new Put(rowkey);
            //添加列簇1的列名,下载时会根据filename进行匹配(filename也就是本地文件名)
            put.add(Bytes.toBytes(family1), Bytes.toBytes(f1_q1), Bytes.toBytes(filename));
            //添加列簇2中的q1图片和q2简历数据
            put.add(Bytes.toBytes(family2), Bytes.toBytes(f2_q1), fileData1);
            put.add(Bytes.toBytes(family2), Bytes.toBytes(f2_q2), fileData2);

            htable.put(put);
            htable.flushCommits();
            htable.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
