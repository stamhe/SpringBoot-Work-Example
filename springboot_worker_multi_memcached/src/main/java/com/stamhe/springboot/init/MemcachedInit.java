package com.stamhe.springboot.init;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.spy.memcached.MemcachedClient;

@Component
public class MemcachedInit implements CommandLineRunner {
	@Value("${springboot.memcached1.host}")
	String mhost1;
	
	@Value("${springboot.memcached1.port}")
	int mport1;

	@Value("${springboot.memcached2.host}")
	String mhost2;

	@Value("${springboot.memcached2.port}")
	int mport2;
	
	private MemcachedClient client;

	@Override
	public void run(String... args) throws Exception {
		try {
			// 一致性hash
			List<InetSocketAddress> list = new ArrayList<>();
			list.add(new InetSocketAddress(mhost1, mport1));
			list.add(new InetSocketAddress(mhost2, mport2));
			client = new MemcachedClient(list);
        } catch (Exception e) {
            System.out.println("inint MemcachedClient failed. error = " + e.getMessage());
        }
	}
	
	public MemcachedClient getClient() {
		return client;
	}
}
