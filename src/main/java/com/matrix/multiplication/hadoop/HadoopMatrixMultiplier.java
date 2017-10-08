package com.matrix.multiplication.hadoop;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class HadoopMatrixMultiplier {

	private String inputFolderPath;
	private String outputFolderPath;
	
	private int firstMatrixRows;
	private int firstMatrixColumns;
	
	@SuppressWarnings("unused")
	private int secondMatrixRows;
	private int secondMatrixColumns;
	
	public HadoopMatrixMultiplier(String inputFolder, String outputFolder, int mrows, int mcols, int nrows, int ncols) {
		this.inputFolderPath = inputFolder;
		this.outputFolderPath = outputFolder;
		this.firstMatrixRows = mrows;
		this.firstMatrixColumns = mcols;
		this.secondMatrixRows = nrows;
		this.secondMatrixColumns = ncols;
	}
	
	public void calculate() throws IOException, ClassNotFoundException, InterruptedException {
		// clear previous result (if exists)
		FileUtils.deleteDirectory(new File(outputFolderPath));

		Job job = createJob();
		
		FileInputFormat.addInputPath(job,   new Path(inputFolderPath));
		FileOutputFormat.setOutputPath(job, new Path(outputFolderPath));

		job.waitForCompletion(true);
	}
	
	private Job createJob() throws IOException {
		Job job = new Job(createConfiguration(), "MatrixMultiply");
		
		job.setJarByClass(HadoopMatrixMultiplier.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		return job;
	}
	
	private Configuration createConfiguration() {
		Configuration conf = new Configuration();
		// M is an m-by-n matrix; N is an n-by-p matrix.
		conf.set(Config.M_ROWS_CODE,    String.valueOf(firstMatrixRows));
		conf.set(Config.M_COLUMNS_CODE, String.valueOf(firstMatrixColumns));
		conf.set(Config.N_COLUMNS_CODE, String.valueOf(secondMatrixColumns));

		return conf;
	}
}