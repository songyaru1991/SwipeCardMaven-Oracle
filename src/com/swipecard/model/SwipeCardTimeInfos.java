package com.swipecard.model;

import java.util.Date;

public class SwipeCardTimeInfos {
	
	private String EMP_ID;
	private String SwipeDate;
	private Date SwipeCardTime;
	private Date SwipeCardTime2;
	private String CheckState;
	private String ProdLineCode;
	private String WorkshopNo;
	private String PRIMARY_ITEM_NO;
	private String RC_NO;
	private String Shift;
	private String ClassNo;
	
	public Date getSwipeCardTime() {
		return SwipeCardTime;
	}
	public void setSwipeCardTime(Date swipeCardTime3) {
		SwipeCardTime = swipeCardTime3;
	}
	
	public Date getSwipeCardTime2() {
		return SwipeCardTime2;
	}
	public void setSwipeCardTime2(Date swipeCardTime22) {
		SwipeCardTime2 = swipeCardTime22;
	}
	
	public String getCheckState() {
		return CheckState;
	}
	public void setCheckState(String checkState) {
		CheckState = checkState;
	}
	public String getProdLineCode() {
		return ProdLineCode;
	}
	public void setProdLineCode(String prodLineCode) {
		ProdLineCode = prodLineCode;
	}
	public String getWorkshopNo() {
		return WorkshopNo;
	}
	public void setWorkshopNo(String workshopNo) {
		WorkshopNo = workshopNo;
	}

	public String getShift() {
		return Shift;
	}
	public void setShift(String shift) {
		Shift = shift;
	}
	public String getSwipeDate() {
		return SwipeDate;
	}
	public void setSwipeDate(String swipeDate) {
		SwipeDate = swipeDate;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public String getClassNo() {
		return ClassNo;
	}
	public void setClassNo(String classNo) {
		ClassNo = classNo;
	}
	public String getPRIMARY_ITEM_NO() {
		return PRIMARY_ITEM_NO;
	}
	public void setPRIMARY_ITEM_NO(String pRIMARY_ITEM_NO) {
		PRIMARY_ITEM_NO = pRIMARY_ITEM_NO;
	}
	public String getRC_NO() {
		return RC_NO;
	}
	public void setRC_NO(String rC_NO) {
		RC_NO = rC_NO;
	}
	

}
