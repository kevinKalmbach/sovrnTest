package com.example.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class HistoryCache extends LinkedHashMap<String, HistoryEntry> {

	private static final long serialVersionUID = 5240021647382889376L;
	private final int maxSize;

    public HistoryCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, HistoryEntry> eldest) {
        return size() > maxSize;
    }
}