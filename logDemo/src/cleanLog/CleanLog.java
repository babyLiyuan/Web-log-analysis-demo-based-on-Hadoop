package cleanLog;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
public class CleanLog { 

    public static class CleanLogMapper extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {
        private Text word = new Text();
        @Override
        public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            Logline log = Logline.parser(value.toString());
            if (log.isValid()) {
                word.set(log.toString());
                output.collect(word, null);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String input = "hdfs://ubuntu/aboutflume/log/events.1434366054409";
        String output = "hdfs://ubuntu/out";

        JobConf conf = new JobConf(CleanLog.class);
        conf.setJobName("CleanLog");
        conf.addResource("classpath:/hadoop/core-site.xml");
        conf.addResource("classpath:/hadoop/hdfs-site.xml");
        conf.addResource("classpath:/hadoop/mapred-site.xml");

        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(IntWritable.class);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(CleanLogMapper.class);
        //conf.setCombinerClass(CleanLogReducer.class);
        //conf.setReducerClass(CleanLogReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));
        
        conf.setNumReduceTasks(0);//reduce的数量设为0
        
        JobClient.runJob(conf);
        System.exit(0);
    }

}
