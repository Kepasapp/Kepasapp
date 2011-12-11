package com.kepasapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * StreamLoader: Loads a twitter filter data stream into a MySql
 * 
 * @author cdepablo
 *
 */
public class StreamLoader {

	private static final String PROPERTIES_FILE = "loader.properties";
	private static final String TWITTERDB_PASSWORD = "twitterdb.password";
	private static final String TWITTERDB_USER = "twitterdb.user";
	private static final String TWITTERDB_URL = "twitterdb.url";
	
	private static final String OAUTH_CONSUMERKEY = "oauth.consumerKey";
	private static final String OAUTH_CONSUMERSECRET = "oauth.consumerSecret";
	private static final String OAUTH_ACCESSTOKEN= "oauth.accessToken";
	private static final String OAUTH_ACCESSTOKENSECRET = "oauth.accessTokenSecret";
	
	
	String url;
	String user;
	String password;
	
	
	String[] track;
	TwitterStream stream;	
	Configuration conf;
	
    // TwitterDBSink listener;
	StatusListener listener;

    
	public StreamLoader(Configuration conf, StatusListener sink, String[] track, long[] follow) throws SQLException {
        					
			this.stream = new TwitterStreamFactory(conf).getInstance();
			
			this.listener = sink;
			// this.listener.open();
			
			this.stream.addListener(listener);
			
			this.track = track;
	}
	
	
	public void run() throws SQLException {
		FilterQuery query = new FilterQuery();
		query.track(track);
		System.out.println(query);
		stream.filter(query);
	}
	


	protected void close() throws SQLException {
		stream.cleanUp();
		// this.listener.close();
	}



	public static void main(String[] args) throws FileNotFoundException, IOException, TwitterException {
 
		Properties properties = new Properties();
		
		properties.load(new FileReader(new File(StreamLoader.PROPERTIES_FILE)));
		
		String oAuthConsumerKey = properties.getProperty(OAUTH_CONSUMERKEY);
		String oAuthConsumerSecret = properties.getProperty(OAUTH_CONSUMERSECRET);
		String oAuthAccessToken = properties.getProperty(OAUTH_ACCESSTOKEN);
		String oAuthAccessTokenSecret = properties.getProperty(OAUTH_ACCESSTOKENSECRET);
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(oAuthConsumerKey);
		cb.setOAuthConsumerSecret(oAuthConsumerSecret);
		cb.setOAuthAccessToken(oAuthAccessToken);
		cb.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);

		Configuration twitterConf = cb.build();
		
		ConfigurationFileReader trackFile = new ConfigurationFileReader(new File("./conf/elecciones.txt"));
		ConfigurationFileReader followFile = new ConfigurationFileReader(new File("./conf/cuentas.txt"));
		
		List<TrackingConcept> concepts = trackFile.read();
		List<TrackingConcept> follow = followFile.read();

		String[] track = TrackingConcept.getQueries(concepts);
		String[] usernames = TrackingConcept.getQueries(follow);
		
		
	
		Twitter twitter = new TwitterFactory(twitterConf).getInstance();
		// TODO: Work it out for more than 100 usernames
		ResponseList<User> users = twitter.lookupUsers(usernames);
		
		long[] userIds = new long[usernames.length];
		int i = 0;
		for (User twitterUsername : users) {
			userIds[i++] = twitterUsername.getId();
		}
		
		
		StreamLoader loader = null;
		
		try {
						
//		String url = properties.getProperty(StreamLoader.TWITTERDB_URL);
//		String user = properties.getProperty(StreamLoader.TWITTERDB_USER);
//		String password = properties.getProperty(StreamLoader.TWITTERDB_PASSWORD);
			
		// TwitterDBSink sink = new TwitterDBSink(url, user, password);
		
		StatusListener sink = new PrintSink();
		loader = new StreamLoader(twitterConf, sink, track, userIds);

		loader.run();
		} catch (SQLException e) {
			e.printStackTrace();
			try {	loader.close();	} catch (Exception e1) { }
		}	
	
	}
	
	
	
}
