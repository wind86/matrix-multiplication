package com.matrix.multiplication.hadoop;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class Map extends org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text> {
	
	private static final String OUTPUT_KEY_FORMAT = "%s,%s";
	private static final String OUTPUT_VALUE_FORMAT = "%s,%s,%s";
	
	private enum DataWriter {
		FIRST_MATRIX {
			@Override
			protected String generateKey(int index, String[] data) {
				return String.format(OUTPUT_KEY_FORMAT, data[1], index);
			}

			@Override
			protected String generateValue(String[] data) {
				return String.format(OUTPUT_VALUE_FORMAT, data[0], data[2], data[3]);
			}

			@Override
			protected String sizeCode() {
				return Config.N_COLUMNS_CODE;
			}
		},
		SECOND_MATRIX {
			@Override
			protected String generateKey(int index, String[] data) {
				return String.format(OUTPUT_KEY_FORMAT, index, data[2]);
			}

			@Override
			protected String generateValue(String[] data) {
				return String.format(OUTPUT_VALUE_FORMAT, data[0], data[1], data[3]);
			}

			@Override
			protected String sizeCode() {
				return Config.M_ROWS_CODE;
			}
		};
		
		public void write(Context context, String[] data) throws IOException, InterruptedException {
			Text outputKey = new Text();
			Text outputValue = new Text();

			String value = generateValue(data);
			int size = getSize(context.getConfiguration());
			
			for (int i = 0; i < size; i++) {
				outputKey.set(generateKey(i, data));
				outputValue.set(value);
				
				context.write(outputKey, outputValue);
			}
		}
		
		protected int getSize(Configuration conf) {
			return Integer.parseInt(conf.get(sizeCode()));
		}
		
		protected abstract String sizeCode();
		
		protected abstract String generateKey(int index, String[] data);
		protected abstract String generateValue(String[] data);
	}
	
	private static final java.util.Map<String, DataWriter> DATA_WRITERS = Collections.unmodifiableMap(Stream.of(
					new SimpleEntry<>(Config.M_CODE, DataWriter.FIRST_MATRIX),
					new SimpleEntry<>(Config.N_CODE, DataWriter.SECOND_MATRIX)
					).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] data = StringUtils.split(value.toString(), ",");
		DataWriter writer = DATA_WRITERS.get(data[0]);
		writer.write(context, data);
	}
}