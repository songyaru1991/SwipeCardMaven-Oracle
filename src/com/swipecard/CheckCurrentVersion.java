package com.swipecard;

import java.io.Reader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class CheckCurrentVersion implements Runnable {
	private static Logger logger = Logger.getLogger(CheckCurrentVersion.class);
	private boolean active;
	private static SqlSessionFactory sqlSessionFactory;
	private static Reader reader;
	private String localSwipeCardVersion;
	private Timestamp CurrentDBTimeStamp;
	static {
		try {
			reader = Resources.getResourceAsReader("Configuration.xml");
			/*
			 * String filePath = System.getProperty("user.dir") +
			 * "/Configuration.xml"; FileReader reader=new FileReader(filePath);
			 */
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (Exception e) {
			logger.error("版本檢查時 Error building SqlSession，原因:"+e);
			e.printStackTrace();
		}
	}

	public CheckCurrentVersion(String currentVersion) {
		active = true;
		localSwipeCardVersion = currentVersion;
	}

	private boolean CheckCurrentVersionIsLatest(String localVersion) {
		SqlSession session = null;
		HashMap<String, Object> versionFromDB = null;
		boolean IsLatest = false;
		try {
			session = sqlSessionFactory.openSession();
			List<HashMap<String, Object>> VersionsFromDB = session.selectList("getCurrentVersionFromDB");
			Iterator<HashMap<String, Object>> iterator = VersionsFromDB.iterator();
			while (iterator.hasNext()) {
				versionFromDB = iterator.next();
			}
			String dbTime=versionFromDB.get("DB_TIME").toString();
			CurrentDBTimeStamp = Timestamp.valueOf(dbTime); 
			//CurrentDBTimeStamp = (Timestamp) versionFromDB.get("DB_TIME");
			
			String versionByDb=versionFromDB.get("VERSION").toString();			
			if (versionByDb.equals(localVersion))
				IsLatest = true;
			else
				IsLatest = false;
		} catch (Exception ex) {
			logger.error("版本檢查時Exception，原因:"+ex);
			/*SwipeCardNoDB d = new SwipeCardNoDB(defaultWorkshopNo);
			throw ExceptionFactory.wrapException("Exception, Cause: " + ex, ex);*/
			return true;
		} finally {
			ErrorContext.instance().reset();
			if (session != null) {
				session.close();
			}
		}

		return IsLatest;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isActive()) {
			try {
				boolean isLatest = this.CheckCurrentVersionIsLatest(localSwipeCardVersion);
				int currentHour = CurrentDBTimeStamp.getHours();

				if (isLatest) {
					System.out.println("程式為最新版本");
					Thread.currentThread().sleep(60*60*1000);
				} else {
					if ((currentHour >= 9 && currentHour < 12) || (currentHour >= 14 && currentHour < 17)
							|| (currentHour >= 21 && currentHour < 24) || (currentHour >= 1 && currentHour < 7)) {
						int dialogResult = JOptionPane.showConfirmDialog(null,
								"本地端程序為舊版本，點選「確認」後立即自動關閉\n關閉後請重新開啟刷卡端程式，程序會自動更新並重新啟動\n", "程序版本警告",
								JOptionPane.DEFAULT_OPTION);
						System.exit(0);
					}

				}

			} catch (InterruptedException ex) {
				logger.error("版本檢查時 Error building SqlSession，原因:"+ex);
				// logger.error(ex.toString());
				ex.printStackTrace();
			}
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private boolean isActive() {
		// TODO Auto-generated method stub
		return active;
	}

	public static SqlSessionFactory getSession() {
		return sqlSessionFactory;
	}

}
