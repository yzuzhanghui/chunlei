package com.wuxi.service;

import java.util.ArrayList;
import java.util.List;

public class OutofmemoryTest {

	/**
	 * -Xms20M -Xmx20M -Xmn10M  -XX:+PrintGC -XX:+PrintGCDetails
		-XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError
		-Xloggc:gc.log
	 * @param args
	 */
	public static void main(String[] args) {
		List<OutofmemoryTest> list = new ArrayList<OutofmemoryTest>();
		while(true){
			list.add(new OutofmemoryTest());
		}
	}
}