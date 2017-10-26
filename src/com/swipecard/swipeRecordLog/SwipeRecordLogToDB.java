package com.swipecard.swipeRecordLog;

import java.awt.Color;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.swipecard.util.FormatDateUtil;
import com.swipecard.util.JsonFileUtil;
import com.swipecard.model.EmpShiftInfos;
import com.swipecard.model.Employee;
import com.swipecard.model.RawRecord;
import com.swipecard.model.User;
import com.swipecard.SwipeCardNoDB;

public class SwipeRecordLogToDB {
	private static Logger logger = Logger.getLogger(SwipeRecordLogToDB.class);

	static SqlSessionFactory sqlSessionFactory;
	private static Reader reader;
	static {
		try {
			reader = Resources.getResourceAsReader("Configuration.xml");
			/*
			 * String filePath = System.getProperty("user.dir") +
			 * "/Configuration.xml"; FileReader reader = new
			 * FileReader(filePath);
			 */
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SqlSessionFactory getSession() {
		return sqlSessionFactory;
	}

	public static void main(String args[]) {
		SwipeRecordLogToDB();
	}

	static JsonFileUtil jsonFileUtil = new JsonFileUtil();

	public static void SwipeRecordLogToDB() {
		JSONObject swipeCardRecordJson = jsonFileUtil.getSwipeCardRecordByJson();
		JSONArray swipeDataJsonArray;
		String workshopNo = "", cardID = "", swipeCardTime = "";
		try {
			if (swipeCardRecordJson != null) {
				workshopNo = swipeCardRecordJson.getString("WorkshopNo");
				swipeDataJsonArray = swipeCardRecordJson.getJSONArray("SwipeData");
				if (swipeDataJsonArray.length() > 0) {
					for (int i = 0; i < swipeDataJsonArray.length(); i++) {
						JSONObject swipeCardData = swipeDataJsonArray.getJSONObject(i);
						cardID = swipeCardData.getString("CardID");
						swipeCardTime = swipeCardData.getString("swipeCardTime");
						System.out.println(
								"WorkshopNo:" + workshopNo + ",CardID:" + cardID + ",swipeCardTime:" + swipeCardTime);
						swipeCardlogToDB(workshopNo, cardID, swipeCardTime);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("刷卡記錄回寫失敗！原因:" + e);
		}

	}

	public static void swipeCardlogToDB(String WorkshopNo, String CardID, String swipeCardTime) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			// 通過卡號查詢員工個人信息
			// 1、判斷是否今天第一次刷卡
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(swipeCardTime);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String swipeDate = df.format(date);
			Employee eif = (Employee) session.selectOne("selectUserByCardID", CardID);
			
			//回寫刷卡資料至raw_record table中
			addRawSwipeRecord(session, eif, CardID,swipeCardTime,WorkshopNo);
		
		} catch (Exception ex) {
			logger.info("刷卡記錄回寫失敗！原因:" + ex);
			SwipeCardNoDB d = new SwipeCardNoDB(WorkshopNo);
			throw ExceptionFactory.wrapException("Error opening session.  Cause: " + ex, ex);
		} finally {
			ErrorContext.instance().reset();
			if (session != null) {
				session.close();
			}
		}
	}

	/*回寫刷卡資料至raw_record table中
	 * 
	 * */
	private static void addRawSwipeRecord(SqlSession session, Employee eif, String cardID,String swipeCardTime,String workshopNo) {
		String Id=null;
		try {
			if(eif!=null)
				Id=eif.getId();
			DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH24:mi:ss");
			Date date = fmt.parse(swipeCardTime);
			
			RawRecord newRawSwipeRecord=new RawRecord();
			newRawSwipeRecord.setCardID(cardID);
			newRawSwipeRecord.setId(Id);
			newRawSwipeRecord.setSwipeCardTime(date);
			session.insert("addRawSwipeRecord", newRawSwipeRecord);
			session.commit();
		}
		catch(Exception ex) {
			SwipeCardNoDB d = new SwipeCardNoDB(workshopNo);
			ex.printStackTrace();
		}
	}
	
}
