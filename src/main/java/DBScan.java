

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DBScan {

    double Eps=0.8;   //区域半径
    int MinPts=4;   //密度
    ArrayList<DataObject> datas;

    int clusterNum;

    //由于自己到自己的距离是0,所以自己也是自己的neighbor
    public Vector<DataObject> getNeighbors(DataObject p,ArrayList<DataObject> objects){
        Vector<DataObject> neighbors=new Vector<DataObject>();
        Iterator<DataObject> iter=objects.iterator();
        while(iter.hasNext()){
            DataObject q=iter.next();

            if(calDis(p.getValue(),q.getValue())<=Eps){      //使用编辑距离
                neighbors.add(q);
            }
        }
        return neighbors;
    }

    public void dbscan(){
        int clusterID=0;
        boolean AllVisited=false;
        while(!AllVisited){
            Iterator<DataObject> iter=datas.iterator();
            while(iter.hasNext()){
                DataObject p=iter.next();
                if(p.isVisited())
                    continue;
                AllVisited=false;
                p.setVisited(true);     //设为visited后就已经确定了它是核心点还是边界点
                Vector<DataObject> neighbors=getNeighbors(p,datas);
                if(neighbors.size()<MinPts){
                    if(p.getCid()<=0)
                        p.setCid(-1);       //cid初始为0,表示未分类；分类后设置为一个正数；设置为-1表示噪声。
                }else{
                    if(p.getCid()<=0){
                        clusterID++;
                        expandCluster(p,neighbors,clusterID,datas);
                    }else{
                        int iid=p.getCid();
                        expandCluster(p,neighbors,iid,datas);
                    }
                }
                AllVisited=true;
            }
        }
        clusterNum = clusterID;
    }

    private void expandCluster(DataObject p, Vector<DataObject> neighbors,
                               int clusterID,ArrayList<DataObject> objects) {
        p.setCid(clusterID);
        Iterator<DataObject> iter=neighbors.iterator();
        while(iter.hasNext()){
            DataObject q=iter.next();
            if(!q.isVisited()){
                q.setVisited(true);
                Vector<DataObject> qneighbors=getNeighbors(q,objects);
                if(qneighbors.size()>=MinPts){
                    Iterator<DataObject> it=qneighbors.iterator();
                    while(it.hasNext()){
                        DataObject no=it.next();
                        if(no.getCid()<=0)
                            no.setCid(clusterID);
                    }
                }
            }
            if(q.getCid()<=0){       //q不是任何簇的成员
                q.setCid(clusterID);
            }
        }
    }
    public void readData(String filePath){
        datas = new ArrayList<DataObject>();
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("@data"))
                {
                    while ((line = br.readLine()) != null)
                    {
                        if(line=="")
                            continue;
                        String[] row = line.split(",");
                        double[] value = new double[row.length - 1];
                        String label = row[row.length-1];
                        for (int i = 0; i < row.length - 1; i++)
                        {
                            value[i] = Double.valueOf(row[i]);
                        }
                        DataObject d = new DataObject(value, label);
                        datas.add(d);
                    }
                } else
                {
                    continue;
                }
            }
            br.close();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    private double calDis(double[] aVector,double[] bVector)  {
        double dis = 0;
        int i = 0;
        /*最后一个数据在训练集中为结果，所以不考虑  */
        for(;i < aVector.length;i++)
            dis += Math.pow(bVector[i] - aVector[i],2);
        dis = Math.pow(dis, 0.5);
        return dis;
    }
    public void printResult(){
        ArrayList<DataObject> noises = new ArrayList<DataObject>();
        ArrayList<ArrayList<DataObject>> result = new ArrayList<ArrayList<DataObject>>();
        for (int i = 0; i < clusterNum; i++) {
            result.add(new ArrayList<DataObject>());
        }
        for(DataObject data : datas){
            if(data.getCid() < 0){
                noises.add(data);
            }else {
                ArrayList<DataObject> list = result.get(data.getCid()-1);
                list.add(data);
            }
        }
        System.out.printf("共有%d个簇\n",clusterNum);
        System.out.print("噪声点为：");
        System.out.print(noises);
        for (int i = 1; i <= clusterNum; i++) {
            System.out.printf("簇%d有%d个点：\n",i,result.get(i-1).size());
            System.out.print(result.get(i-1)+"\n");
        }
    }

    class DataObject{
        private Integer cid = 0;
        private double[] value;
        private Boolean visited = false;
        private String lable;

        public DataObject(double[] value, String lable) {
            this.value = value;
            this.lable = lable;
        }

        public Integer getCid() {
            return cid;
        }

        public void setCid(Integer cid) {
            this.cid = cid;
        }

        public double[] getValue() {
            return value;
        }

        public void setValue(double[] value) {
            this.value = value;
        }

        public Boolean isVisited() {
            return visited;
        }

        public void setVisited(Boolean visited) {
            this.visited = visited;
        }

        @Override
        public String toString() {
            return "DataObject{" +
                    "cid=" + cid +
                    ", value=" + Arrays.toString(value) +
                    ", visited=" + visited +
                    ", lable='" + lable + '\'' +
                    '}';
        }
    }

    public static void main(String[] args){

        DBScan ds=new DBScan();
        ds.readData("/Users/xiyuchenli/Downloads/数据挖掘/数据挖掘/数据/forKNN/iris.2D.test.arff");
        ds.dbscan();
        ds.printResult();

    }
}