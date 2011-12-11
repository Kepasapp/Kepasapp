package com.kepasapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationFileReader {

	LineNumberReader reader;
	

	public ConfigurationFileReader(LineNumberReader reader) {
		super();
		this.reader = reader;
	}

	public ConfigurationFileReader(File f) throws FileNotFoundException {
		super();
		this.reader = new LineNumberReader(new FileReader(f));
	}
	
	

	public List<TrackingConcept> read() {
		List<TrackingConcept> concepts = new ArrayList<TrackingConcept>();
		
		String line;
		try {
			line = reader.readLine();
			
			while (line!=null) {
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("#") )  {
					TrackingConcept concept = new TrackingConcept();
					String[] splits = line.split("="); 
					
					if (splits.length != 2) {
						System.err.println("Skipping incorrect line in configuration file: " +  line);
					} else {
						concept.name = splits[0];
						concept.queries = parseQueries(splits[1]);
						
						concepts.add(concept);
					}	
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { reader.close(); } catch (IOException e) { e.printStackTrace(); }
		}
			
	return concepts;		
	}

	
	

	public void close() throws IOException {
		reader.close();
	}

	private List<String> parseQueries(String queryLine) {
		String[] queries = queryLine.split(",");
		// TODO: Sanitize queries
		// TODO: automatically produce queries with special characters 
		List<String> list = new ArrayList<String>(queries.length);
		for (String q : queries) {
			list.add(q.trim());
		}
		
		return list;
	}
		
		
	public static void main(String[] args) {
		
		try {
			ConfigurationFileReader file = new ConfigurationFileReader(new File("./conf/elecciones.txt"));
			
			List<TrackingConcept> concepts = file.read();
			
			for (TrackingConcept concept : concepts) {
				System.out.println(concept.toString());
			}
			
			file.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
