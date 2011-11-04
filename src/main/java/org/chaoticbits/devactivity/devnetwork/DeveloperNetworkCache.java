package org.chaoticbits.devactivity.devnetwork;

import java.util.HashMap;

public class DeveloperNetworkCache {
	private static DeveloperNetworkCache instance = null;

	public static DeveloperNetworkCache getInstance() {
		if (instance == null)
			instance = new DeveloperNetworkCache();
		return instance;
	}

	private HashMap<String, DeveloperNetwork> map;

	private DeveloperNetworkCache() {
		this.map = new HashMap<String, DeveloperNetwork>(5);
	}

	public void put(String key, DeveloperNetwork dn) {
		map.put(key, dn);
	}

	public DeveloperNetwork get(String key) {
		return map.get(key);
	}

}
