package com.kepasapp;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class PrintSink implements StatusListener {

	public void onException(Exception ex) {
		// TODO Auto-generated method stub

	}

	public void onStatus(Status status) {
	
		System.out.println(status.getUser().getName() +  " : " + status.getText());

	}

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// TODO Auto-generated method stub

	}

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// TODO Auto-generated method stub

	}

	public void onScrubGeo(long userId, long upToStatusId) {
		// TODO Auto-generated method stub

	}

}
