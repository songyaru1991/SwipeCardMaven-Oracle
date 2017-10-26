package com.swipecard.model;

import java.util.Date;

public class RawRecord {
	private String Id;
	private String CardID;
	private Date SwipeCardTime;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getCardID() {
		return CardID;
	}
	public void setCardID(String cardID) {
		CardID = cardID;
	}
	public Date getSwipeCardTime() {
		return SwipeCardTime;
	}
	public void setSwipeCardTime(Date swipeCardTime2) {
		SwipeCardTime = swipeCardTime2;
	}
}
