package com.stamhe.springboot.algorithm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;


/*
 * 
 * 分库分表: hash、取模、按时间
 * https://blog.csdn.net/myshy025tiankong/article/details/83063887
 * 
 * https://www.cnblogs.com/mr-yang-localhost/p/8313360.html
 * StandardShardingStrategy 标准分片策略。提供对SQL语句中的=, IN和BETWEEN AND的分片操作支持。StandardShardingStrategy只支持单分片键，
 * 提供PreciseShardingAlgorithm和RangeShardingAlgorithm两个分片算法
 * PreciseShardingAlgorithm 用于处理=和IN的分片
 * RangeShardingAlgorithm 用于处理BETWEEN AND分片，如果不配置RangeShardingAlgorithm，SQL中的BETWEEN AND将按照全库路由处理
 * 
 * 
 * ComplexShardingStrategy 复合分片策略。提供对SQL语句中的=, IN和BETWEEN AND的分片操作支持。
 * ComplexShardingStrategy支持多分片键，由于多分片键之间的关系复杂，因此Sharding-JDBC并未做过多的封装，
 * 而是直接将分片键值组合以及分片操作符交于算法接口，完全由应用开发者实现，提供最大的灵活度。
 * 
 * 
 * InlineShardingStrategy Inline表达式分片策略。使用Groovy的Inline表达式，提供对SQL语句中的=和IN的分片操作支持。
 * InlineShardingStrategy只支持单分片键，对于简单的分片算法，可以通过简单的配置使用，从而避免繁琐的Java代码开发，
 * 如: tuser${user_id % 8} 表示t_user表按照user_id按8取模分成8个表，表名称为t_user_0到t_user_7。
 * 
 * 
 * HintShardingStrategy 通过Hint而非SQL解析的方式分片的策略。
 * 
 * 
 * NoneShardingStrategy 不分片的策略。
 */
public class TableMSShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
		String logic_table = shardingValue.getLogicTableName();
		
		String tb_name = "";
		
		switch(logic_table) {
			case "t_user_x":
				tb_name = "users_201908_009";
				return tb_name;
			case "t_article_x":
				tb_name = "t_article_201908_009";
				return tb_name;
			default:
				tb_name = "xxx_";
		}
		
		try {
			Date date = (Date) new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(shardingValue.getValue().toString());

			String year = String.format("%tY", date);
			String mon = String.format("%tm", date);
			String dat = String.format("%td", date);
			// 表名【gps_20181011】
			tb_name = tb_name + year + mon + dat;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		System.out.println("tb_name = " + tb_name);
		return tb_name;
	}

}
