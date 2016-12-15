package edu.stevens.cs549.hadoop.pagerank;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FindJoinMapper extends Mapper<LongWritable, Text, Text, Text>  {

	 @Override
	    protected void setup(Context context) throws IOException, InterruptedException {
	        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
	            URI mappingFileUri = context.getCacheFiles()[0];
	            
	            if (mappingFileUri != null) {
	              // Would probably be a good idea to inspect the URI to see what the bit after the # is, as that's the file name
	                System.out.println("Mapping File: " + FileUtils.readFileToString(new File("./cache")));
	            } else {
	                System.out.println(">>>>>> NO MAPPING FILE");
	            }
	        } else {
	            System.out.println(">>>>>> NO CACHE FILES AT ALL");
	        }
	    }
	
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException, IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		/*
		 * Join final output with linked name
		 * input: key: nodeId+rank, text: adjacent list
		 * input: key: nodeId, text: name
		 * 
		 * output: key: nodeId, text: rank
		 * ouput: key: nodeId, text:names
		 */
		
		String[] sections;
		if (line.contains(":")) {
			int index = line.indexOf(":");
			sections = new String[2];
			sections[0] = line.substring(0, index);
			sections[1] = line.substring(index + 1, line.length());
		} else {
			sections = line.split("\t"); // Splits it into two parts. Part 1: node+rank | Part 2: adj list
		}
		

		if (sections.length > 2) // Checks if the data is in the incorrect format
		{
			throw new IOException("Incorrect data format");
		}
		String[] noderank = sections[0].split("\\+");
		if(noderank.length == 1) {
			// it's nodeID with its name
			context.write(new Text(noderank[0]), new Text(PageRankDriver.MARKER_NAME + sections[1].trim()));
		}
		
		if(noderank.length == 2) {
			// it's nodeID with its rank
			context.write(new Text(noderank[0]), new Text(PageRankDriver.MARKER_RANK + noderank[1]));
		}
	}

	
}
