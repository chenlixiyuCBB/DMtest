import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class KnnClassifier {
    ArrayList<Data> trainSet;
    ArrayList<Data> testSet;
    int k;

    public static void main(String args[])
    {
        KnnClassifier knn = new KnnClassifier("/Users/xiyuchenli/Downloads/数据挖掘/数据挖掘/数据/forKNN/iris.2D.train.arff",1);
        knn.predict("/Users/xiyuchenli/Downloads/数据挖掘/数据挖掘/数据/forKNN/iris.2D.test.arff");
    }

    public KnnClassifier(String filePath, int k)
    {
        trainSet = readARFF(new File(filePath));
        this.k = k;
    }

    //读取arff文件，给attribute、attributevalue、data赋值
    public ArrayList<Data> readARFF(File file)
    {
        ArrayList<Data> data = new ArrayList<Data>();
        try {
            FileReader fr = new FileReader(file);
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
                        Data d = new Data(value, label);
                        data.add(d);
                    }
                } else
                {
                    continue;
                }
            }
            br.close();
            return data;
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            return null;
        }
    }

    public void predict(String testPath){
        testSet = readARFF(new File(testPath));
        int correct = 0;
        for(int n=0;n<testSet.size();n++){
            Data data = testSet.get(n);
            String predict = classify(data);
            boolean flag;
            if( flag = predict.equals(data.label)){
                correct++;
            }
            System.out.println(flag + ":\t***Predicted label:"+predict+"\tdata label:" + data.label+"***");
        }
        System.out.println("Accuracy: "+correct*100.0/testSet.size() +"%");
    }

    //对数据进行预测，返回预测的类标签
    public String classify(Data test)
    {
        Map<Data,Double> distanceMap = new HashMap<Data,Double>();
        Map<String,Integer> resultMap = new HashMap<String, Integer>();

        for (Data data : trainSet) {
            distanceMap.put(data,getDist(test,data));
        }

        List<Map.Entry<Data,Double>> list = new ArrayList<Map.Entry<Data, Double>>(distanceMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Data, Double>>() {
            public int compare(Map.Entry<Data, Double> o1, Map.Entry<Data, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        for(Map.Entry<Data,Double> entry : list.subList(0,k)){
            Integer count = resultMap.get(entry.getKey().label);
            if(count != null)
                count ++;
            else
                resultMap.put(entry.getKey().label,1);

        }
        List<Map.Entry<String,Integer>> resultlist = new ArrayList<Map.Entry<String, Integer>>(resultMap.entrySet());
        Collections.sort(resultlist, new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                }

        );
        return resultlist.get(0).getKey();


    }

    //计算两个数据点之间的欧氏距离
    private double getDist(Data test, Data data)
    {
        double x = test.data[0] - data.data[0];
        double y = test.data[1] - data.data[1];

        double d = Math.sqrt(x*x + y*y);
        return d;

    }

    private class Data{
        double[] data;
        String label;

        Data(double[] data, String label)
        {
            this.data = data;
            this.label = label;
        }

    }
}
