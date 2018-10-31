package com.swipecard.model;

import java.sql.Date;

public class RCLine {
	private String RC_NO;
	private String PRIMARY_ITEM_NO;
	private String STD_MAN_POWER;
	private String PROD_LINE_CODE;
	private Date CREATION_DATE;
	
	public String getRC_NO() {
		return RC_NO;
	}
	public void setRC_NO(String rC_NO) {
		RC_NO = rC_NO;
	}
	public String getPRIMARY_ITEM_NO() {
		return PRIMARY_ITEM_NO;
	}
	public void setPRIMARY_ITEM_NO(String pRIMARY_ITEM_NO) {
		PRIMARY_ITEM_NO = pRIMARY_ITEM_NO;
	}
	public String getSTD_MAN_POWER() {
		return STD_MAN_POWER;
	}
	public void setSTD_MAN_POWER(String sTD_MAN_POWER) {
		STD_MAN_POWER = sTD_MAN_POWER;
	}
	public String getPROD_LINE_CODE() {
		return PROD_LINE_CODE;
	}
	public void setPROD_LINE_CODE(String pROD_LINE_CODE) {
		PROD_LINE_CODE = pROD_LINE_CODE;
	}
	public Date getCREATION_DATE() {
		return CREATION_DATE;
	}
	public void setCREATION_DATE(Date cREATION_DATE) {
		CREATION_DATE = cREATION_DATE;
	}
	

}
