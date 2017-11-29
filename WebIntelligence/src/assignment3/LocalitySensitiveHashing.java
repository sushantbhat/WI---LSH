package assignment3;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class LocalitySensitiveHashing {
	public static void main(String [] args) throws Exception
	{
		Configuration c=new Configuration();
		String[] files=new GenericOptionsParser(c,args).getRemainingArgs();
		Path input=new Path(files[0]);
		Path output=new Path(files[1]);
		Job j=new Job(c,"LocalitySensitiveHashing");
		j.setJarByClass(LocalitySensitiveHashing.class);
		j.setMapperClass(MapForWordCount.class);
		j.setReducerClass(ReduceForWordCount.class);
		j.setOutputKeyClass(IntWritable.class);
		j.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(j, input);
		FileOutputFormat.setOutputPath(j, output);
		System.exit(j.waitForCompletion(true)?0:1);
	}
	
	public static class MapForWordCount extends Mapper<LongWritable, Text,
	IntWritable, Text>{
		public void map(LongWritable key, Text value, Context con) throws
		IOException, InterruptedException
		{
			String line = value.toString();
			String[] words=line.split(" ");
			int row = Integer.parseInt(words[0]);
			Random rand = new Random();
			
			int bands[][] = new int[words.length-1][4];
			for(int i=1; i<words.length ; i++){
				String[] curArr = words[i].split(",");
				for(int j=0 ; j<curArr.length ; j++){
					bands[i-1][j] = Integer.parseInt(curArr[j]);
				}
			}
			
			int signRow = rand.nextInt(4);
			
			
			con.write(new IntWritable(row*10 + bands[0][signRow]), new Text("Column1"));
			con.write(new IntWritable(row*10 + bands[1][signRow]), new Text("Column2"));
			con.write(new IntWritable(row*10 + bands[2][signRow]), new Text("Column3"));
			
		}
	}
	public static class ReduceForWordCount extends Reducer<IntWritable,
	Text, IntWritable, Text>
	{
		public void reduce(IntWritable bucket, Iterable<Text> values, Context
		con) throws IOException, InterruptedException
		{
			String collisionColumns = "";
			for(Text value : values)
			{
				collisionColumns += " " + value.toString();
			}
			con.write(bucket, new Text(collisionColumns));
		}
	}


}
