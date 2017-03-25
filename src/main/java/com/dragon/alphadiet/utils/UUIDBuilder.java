package com.dragon.alphadiet.utils;

import java.util.UUID;

public class UUIDBuilder {
	public static void main(String[] args) {
		for (int i = 0; i < 50; i++) {
			String id = UUID.randomUUID().toString().replace("-", "");
			System.out.println(id);
		}

	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
