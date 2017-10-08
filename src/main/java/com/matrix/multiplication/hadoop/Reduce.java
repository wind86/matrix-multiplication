package com.matrix.multiplication.hadoop;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;


public class Reduce extends org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text> {
	
	private static final String OUTPUT_FORMAT = "%s,%s";
	private static final double DEFAULT_VALUE = 0.0;
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		java.util.Map<Integer, Double> mCache = new HashMap<>();
		java.util.Map<Integer, Double> nCache = new HashMap<>();

		cacheData(mCache, nCache, values);

		double result = calculate(context.getConfiguration(), mCache, nCache);
		if (result != DEFAULT_VALUE) {
			context.write(null, new Text(String.format(OUTPUT_FORMAT, key.toString(), Double.toString(result))));
		}
	}
	
	private static double calculate(Configuration configuration, java.util.Map<Integer, Double> mCache, java.util.Map<Integer, Double> nCache) {
		int size = Integer.parseInt(configuration.get(Config.M_COLUMNS_CODE));

		double result = IntStream.range(0, size)
				.mapToDouble(j -> getData(mCache, j) * getData(nCache, j))
				.sum();
		
		if (Double.isNaN(result)) {
			result = DEFAULT_VALUE;
		}
		
		return result;
	}
	
	private static double getData(java.util.Map<Integer, Double> cache, int index) {
		return defaultIfNull(cache.get(index), DEFAULT_VALUE);
	}
	
	private static void cacheData(java.util.Map<Integer, Double> mCache, java.util.Map<Integer, Double> nCache, Iterable<Text> values) {
		values.forEach(value -> {
			String[] data = StringUtils.split(value.toString(), ",");
			java.util.Map<Integer, Double> cache = Config.M_CODE.equals(data[0]) ? mCache : nCache;
			cache.put(Integer.parseInt(data[1]), Double.parseDouble(data[2]));
		});
	}
}
