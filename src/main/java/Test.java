import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class Test {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        System.out.println("++++++++++Example 1: Normalize Data via weka.++++++++");

        System.out.println("Step 1. 读入数据集...");
        DataSource source = new DataSource("/Users/xiyuchenli/Desktop/weka-3-8-1/data/iris.arff");
        Instances instances = source.getDataSet();


        System.out.println("Step 2. 归一化数据...");
        Normalize norm = new Normalize();
        norm.setInputFormat(instances);
        Instances newInstances = Filter.useFilter(instances, norm);

        System.out.println("Step 3. 归一化后的数据...");

        System.out.println("--------------------------------------");
        try {
            //��ӡ������
            int numOfAttributes = newInstances.numAttributes();
            for(int i = 0; i < numOfAttributes ;++i)
            {
                Attribute attribute = newInstances.attribute(i);
                System.out.print(attribute.name() + "     ");
            }
            System.out.println();
            //��ӡʵ��
            int numOfInstance = newInstances.numInstances();
            for(int i = 0; i < numOfInstance; ++i)
            {
                Instance instance = newInstances.instance(i);
                System.out.print(instance.toString() + "     ");
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Step 4. 保存归一化的数据到新文件...");
        System.out.println("--------------------------------");
        DataSink.write("/Users/xiyuchenli/Desktop/weka-3-8-1/datairis_norm.arff", newInstances);
        System.out.println("Congratulations.");
    }

}
