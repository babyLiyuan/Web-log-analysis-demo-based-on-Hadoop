package rule;
import cleanLog.CleanLog;
import cleanLog.Logline;
import cleanLog.CleanLog.CleanLogMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

public class Rule1 {
	public static class Rule1Mapper extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {
        private Text word = new Text();
        private IntWritable one = new IntWritable(1);


        @Override
        public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        	Logline log = new Logline(value.toString());
        	if(log.isValid() && log.getMethod()=="POST" && log.getUri_stem()=="http://www.aboutyun.com/home.php"){
                word.set(log.getC_ip()+" "+log.getDate()+" "+log.getTime());
                output.collect(word, one);
            }
        }
    }
	public static void main(String[] args) throws Exception {
		String input = "hdfs://ubuntu/out/part-00000";
		String output = "hdfs://ubuntu/outRule1";

		JobConf conf = new JobConf(Rule1.class);
		conf.setJobName("Rule1");
		conf.addResource("classpath:/hadoop/core-site.xml");
		conf.addResource("classpath:/hadoop/hdfs-site.xml");
		conf.addResource("classpath:/hadoop/mapred-site.xml");

		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(IntWritable.class);

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(Rule1Mapper.class);
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
