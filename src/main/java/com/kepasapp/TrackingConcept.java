package com.kepasapp;

import java.util.LinkedList;
import java.util.List;

public class TrackingConcept {

	public String name;
	public List<String> queries;
	
	
	@Override
	public String toString() {
		
		return name.toString() + " " + queries.toString();
	}
	
	public static String[] getQueries(List<TrackingConcept> concepts) {
		List<String> queries = new LinkedList<String>();
		
		for (TrackingConcept concept : concepts) {
			queries.addAll(concept.queries);	
		}
		
		return queries.toArray(new String[queries.size()]);
	}
	
}
