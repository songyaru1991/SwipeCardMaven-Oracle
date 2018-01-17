package com.swipecard;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import com.swipecard.util.DESUtils;
import com.swipecard.util.FormatDateUtil;
import com.swipecard.util.FrameShowUtil;
import com.swipecard.util.GetLocalHostIpAndName;
import com.swipecard.util.JsonFileUtil;
import com.swipecard.util.SwipeCardJButton;
import com.swipecard.model.EmpShiftInfos;
import com.swipecard.model.Employee;
import com.swipecard.model.RCLine;
import com.swipecard.model.RawRecord;
import com.swipecard.model.SwingBase;
import com.swipecard.model.SwipeCardTimeInfos;
import com.swipecard.model.SwipeCardUserTableModel;
import com.swipecard.model.WorkedOneWeek;
import com.swipecard.services.SwipeCardService;

public class SwipeCard extends JFrame {

	private static final long serialVersionUID = 1216479862784043108L;
	private final static String CurrentVersion="V20171113(FD通訊不卡七休一)";
	private static Logger logger = Logger.getLogger(SwipeCard.class);
	private Vector<Vector<Object>> rowData = new Vector<Vector<Object>>();
	private JTable table;
	private String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";	
	private int ONE_SECOND = 1000;

	static JTabbedPane tabbedPane;
	static JLabel label1, label3, swipeTimeLable, curTimeLable;
	static JLabel labelS1, labelS2, labelS3;
	static JPanel panel1, panel2, panel3;
	static ImageIcon image;
	static JLabel labelT2_1, labelT2_2, labelT2_3, labelT1_1,workShopNoJlabel, labelT1_3, labelT1_5, labelT1_6, labelT1_4, linenoLabel, labelT1_7;
	static JComboBox comboBox, comboBox2;
	static SwipeCardJButton butT1_3, butT1_4, butT1_5, butT1_6, butT2_1, butT2_2, butT2_3, butT1_7, butT2_rcno;
	static JTextArea jtextT1_1, jtextT1_2;
	static TextField textT2_1, textT2_2, textT1_3, textT1_1, textT1_5, textT1_6;
	static JTextField jtf, jtf2;
	static JScrollPane jspT1_1, jspT2_2, JspTable, myScrollPane;
	// static Object[] str1 = getItems();
	static Object[] str1 = null;
	private SwipeCardUserTableModel myModel;
	private JTable mytable;

	 static JsonFileUtil jsonFileUtil = new JsonFileUtil();
	  static String defaultWorkshopNo = jsonFileUtil.getSaveWorkshopNo();
	  static  String defaultLineNo = jsonFileUtil.getSaveLineNo();
	
	static SqlSessionFactory sqlSessionFactory;
	private static Reader reader;
	static Properties pps = new Properties();
	static Reader pr = null;
	static {
		try {
			pr = Resources.getResourceAsReader("db.properties");
			pps.load(pr);
			pps.setProperty("username", DESUtils.getDecryptString(pps.getProperty("username")));
			pps.setProperty("password", DESUtils.getDecryptString(pps.getProperty("password")));
			reader = Resources.getResourceAsReader("Configuration.xml");
			/*
			 * String filePath = System.getProperty("user.dir") +
			 * "/Configuration.xml"; FileReader reader = new
			 * FileReader(filePath);
			 */
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,pps);
		} catch (Exception e) {
			logger.error("Error opening session:"+e);
			SwipeCardNoDB d = new SwipeCardNoDB(defaultWorkshopNo);
			e.printStackTrace();
		}
	}

	public static SqlSessionFactory getSession() {
		return sqlSessionFactory;
	}

	/**
	 * Timer task to update the time display area
	 *
	 */
	protected class JLabelTimerTask extends TimerTask {
		@Override
		public void run() {			
			//time = dateFormatter.format(Calendar.getInstance().getTime());
			Date date = new Date();
			SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
            String time = dateFormatter.format(date);
			curTimeLable.setText(time);
		}
	}

	public SwipeCard(final String WorkshopNo, String LineNo) {

		super("產線端刷卡程式-"+CurrentVersion);
		SwipeCardService service=new SwipeCardService();
		//setBounds(12, 84, 1000, 630);		
		setResizable(true);
		
		Container c = getContentPane();
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT); // 创建选项卡面板对象
		// 创建标签
		labelS1 = new JLabel("指示單號");
		labelS2 = new JLabel("料號");
		labelS3 = new JLabel("標準人數");

		panel1 = new JPanel();
		panel1.setLayout(null);
		panel2 = new JPanel();
		panel2.setLayout(null);
		panel3 = new JPanel();
		panel1.setBackground(Color.WHITE);
		panel2.setBackground(Color.WHITE);
		panel3.setBackground(Color.WHITE);

		labelT2_1 = new JLabel("班別：");// 指示單號

		str1 = getRcLine();
		if (str1 != null) {
			comboBox = new JComboBox(str1);
		} else {
			comboBox = new JComboBox();
		}

		comboBox.setEditable(true);

		comboBox.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		jtf = (JTextField) comboBox.getEditor().getEditorComponent();

		comboBox2 = new JComboBox();// getLineNoByWorkNo
		// comboBox2.addItem("");
		comboBox2.addItem("日班");
		comboBox2.addItem("夜班");
		comboBox2.setEditable(false);// 可編輯
		comboBox2.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		jtf2 = (JTextField) comboBox2.getEditor().getEditorComponent();

		textT1_1 = new TextField(15);// 車間
		textT1_1.setFont(new Font("微软雅黑", Font.PLAIN, 25));

		textT1_3 = new TextField(15);// 上班
		textT1_3.setFont(new Font("微软雅黑", Font.PLAIN, 25));

		jtextT1_1 = new JTextArea();// 刷卡人員信息,JTextArea(int rows, int columns)
		jtextT1_1.setBackground(Color.WHITE);
		jtextT1_2 = new JTextArea();// 備註
		textT2_1 = new TextField(15);// "料號"
		textT2_2 = new TextField(15);// "標準人數"

		// text3 = new JTextArea(2, 20);

		labelT1_1 = new JLabel("車間:");
		labelT1_1.setFont(new Font("微软雅黑", Font.BOLD, 25));
		
		workShopNoJlabel = new JLabel("車間:");
		workShopNoJlabel.setFont(new Font("微软雅黑", Font.BOLD, 25));
		
		linenoLabel = new JLabel("線號");
		linenoLabel.setFont(new Font("微软雅黑", Font.BOLD, 25));

		labelT1_3 = new JLabel("刷卡:");
		labelT1_3.setFont(new Font("微软雅黑", Font.BOLD, 25));
		
		labelT1_4 = new JLabel("線號：");
		labelT1_4.setFont(new Font("微软雅黑", Font.BOLD, 25));

		labelT1_5 = new JLabel("實際人數:");
		labelT1_6 = new JLabel("備註:");
		labelT2_2 = new JLabel("指示單號:");
		labelT2_3 = new JLabel("標準人數:");
		labelT1_7 = new JLabel("更換車間刷卡：");
		labelT1_7.setFont(new Font("微软雅黑", Font.BOLD, 25));
		labelT1_7.setVisible(false);
		
		curTimeLable = new JLabel();
		curTimeLable.setFont(new Font("微软雅黑", Font.BOLD, 35));

		swipeTimeLable = new JLabel();
		swipeTimeLable.setFont(new Font("微软雅黑", Font.BOLD, 35));

		// 未補充指示單號人員信息
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("姓名");
		columnNames.add("刷卡時間1");
		columnNames.add("刷卡時間2");
		table = new JTable(new DefaultTableModel(rowData, columnNames));
		JspTable = new JScrollPane(table);
		JspTable.setBounds(310, 40, 520, 400);

		Object ShiftName = comboBox2.getSelectedItem();
		String ShiftRcNo = "";
		if (ShiftName.equals("夜班")) {
			ShiftRcNo = "N";
		} else {
			ShiftRcNo = "D";
		}

		myModel = new SwipeCardUserTableModel(WorkshopNo, ShiftRcNo);
		mytable = new JTable(myModel);
		setTable();
		myScrollPane = new JScrollPane(mytable);
		myScrollPane.setBounds(310, 40, 520, 400);

		int x1 = 15, x2 = 100, x3 = 200, x4 = 400, x5 = 130, x6 = 460, x7 = 90;
		int y1 = 40, y4 = 180;

		labelT2_1.setBounds(x1, y1, x7, y1);
		labelT2_2.setBounds(x1, 2 * y1 + 10, x7, y1);
		comboBox2.setBounds(x1 + x7, y1, x3, y1); 
		comboBox.setBounds(x1 + x7, 2 * y1 + 10, x3, y1);

		labelT2_3.setBounds(x1, 2 * y1 + 10, x7, y1);

		labelT1_1.setBounds(x1 + 20, y1, x7, y1);
		labelT1_3.setBounds(x1 + 20, 2 * y1 + 20, x7, y1);

		labelT1_6.setBounds(x1, 8 * y1, x7, y1);
		linenoLabel.setBounds(x1 + x7, 4 * y1 + 80, y4 + 100, y1);
		workShopNoJlabel.setBounds(x1 + x7, 1 * y1, y4 + 100, y1);
		textT1_3.setBounds(x1 + x7, 2 * y1 + 20, y4 + 100, y1);
		labelT1_4.setBounds(x1 + 20, 4 * y1 + 80, x7, y1);
		jtextT1_2.setBounds(x1 + x7, 9 * y1, x4, y1);

		textT2_1.setBounds(x1 + x7, 1 * y1, y4, y1);
		textT2_2.setBounds(x1 + x7, 2 * y1 + 10, y4, y1);

		swipeTimeLable.setBounds(400, y1, x4, 50);
		curTimeLable.setBounds(x1 + 10, 3 * y1 + 40, 400, 50);

		jspT1_1 = new JScrollPane(jtextT1_1);
		jspT1_1.setBounds(400, 2 * y1 + 20, x4, 250);

		jspT2_2 = new JScrollPane(jtextT1_2);
		jspT2_2.setBounds(x1, 9 * y1, x3 + x7, 150);
		int cc = 240;
		Color d = new Color(cc, cc, cc);

		// 将标签面板加入到选项卡面板对象上
		tabbedPane.addTab("上下班刷卡界面", null, panel1, "First panel");
		tabbedPane.addTab("補充指示單號", null, panel2, "Second panel");
		tabbedPane.setSelectedIndex(0); // 设置默认选中的
		// tabbedPane.setEnabledAt(1,false);
		this.setVisible(true);

		textT1_1.setEditable(false);
		textT1_3.setEditable(true);

		jtextT1_1.setEditable(false);
		jtextT1_2.setEditable(false);

		textT2_1.setEditable(false);
		textT2_2.setEditable(false);

		jtextT1_1.setLineWrap(true);
		jtextT1_2.setLineWrap(true);

		jtextT1_2.setBackground(d);
		labelT1_7.setBounds(x1 + 20, 350 + y1 + 20, x3, y1);

		butT1_5 = new SwipeCardJButton("更換車間", 2);
		butT1_6 = new SwipeCardJButton("退出程式", 2);

		butT2_1 = new SwipeCardJButton("換料 ", 2);
		butT2_2 = new SwipeCardJButton("確認提交", 2);
		butT2_3 = new SwipeCardJButton("人員刷新", 2);
		butT2_rcno = new SwipeCardJButton("刷新指示單", 2);

		butT1_5.setBounds(x6, 350 + y1 + 20, x5, y1);
		butT1_6.setBounds(x6 + 160, 350 + y1 + 20, x5, y1);
		butT2_1.setBounds(x4, 400, x5, y1);
		butT2_3.setBounds(x6 + 60, 12 * y1, x5, y1);

		butT2_rcno.setBounds(x2, 3 * y1 + 30, 100, y1);
		butT2_2.setBounds(x2 + 110, 3 * y1 + 30, 90, y1);
		
		textT1_6 = new TextField(15);// 管理员刷卡
		textT1_6.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		textT1_6.setVisible(false);
		textT1_6.setBounds(x1 + x3, 350 + y1 + 20, y4 + 30, y1);
		textT1_6.setEditable(false);
		panel1.add(textT1_6);

		panel1.add(textT1_3);
		panel1.add(labelT1_7);
		panel1.add(labelT1_1);
		panel1.add(workShopNoJlabel);
		panel1.add(labelT1_4);
		panel1.add(linenoLabel);
		
		panel1.add(labelT1_3);
		panel1.add(swipeTimeLable);
		panel1.add(curTimeLable);

		panel1.add(jspT1_1);
		panel1.add(butT1_5);
		panel1.add(butT1_6);

		panel2.add(butT2_2);

		panel2.add(butT2_3);
		panel2.add(butT2_rcno);

		panel2.add(labelT2_1);
		panel2.add(labelT2_2);
		panel2.add(comboBox);
		panel2.add(comboBox2);

		panel2.add(myScrollPane);
		
		if(LineNo == null || LineNo.equals("")){
			linenoLabel.setText("");
			labelT1_4.setVisible(false);
		}else{
			linenoLabel.setText(LineNo);
		}
		
		FrameShowUtil frameShow=new FrameShowUtil();
		frameShow.sizeWindowOnScreen(this, 0.51, 0.6);
			
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		Timer tmr = new Timer();
		tmr.scheduleAtFixedRate(new JLabelTimerTask(), new Date(), ONE_SECOND);
		
		// ItemListene取得用户选取的项目,ActionListener在JComboBox上自行输入完毕后按下[Enter]键,运作相对应的工作
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO

				if (e.getStateChange() == ItemEvent.SELECTED) {
					// System.out.println("-----------e.getItem():"+e.getStateChange()+"-------------");
					String RC_NO = jtf.getText();
					if (RC_NO.length() == 0) {
						textT2_1.setText("");
						textT2_2.setText("");
					} else {
						SqlSession session = sqlSessionFactory.openSession();
						try {
							RCLine rcLine = (RCLine) session.selectOne("selectUserByRCNo", RC_NO);
							if (rcLine != null) {
								textT2_1.setText(rcLine.getPRIMARY_ITEM_NO());
								textT2_2.setText(rcLine.getSTD_MAN_POWER());
							}

						} catch (Exception e1) {
							logger.error(e1);
							System.out.println("Error opening session");
							dispose();
							SwipeCardNoDB d = new SwipeCardNoDB(WorkshopNo);
							throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e1, e1);
						} finally {
							ErrorContext.instance().reset();
							if (session != null) {
								session.close();
							}
						}
					}

				}
			}
		});

		// TODO addKeyListener用于接收键盘事件（击键）的侦听器接口
		jtf.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				String key = jtf.getText();
				comboBox.removeAllItems();
				// for (Object item : getItems()) {
				if (str1 != null) {
					for (Object item : str1) {
						// 可以把contains改成startsWith就是筛选以key开头的项目
						// contains(key)/startsWith(key)
						if (((String) item).startsWith(key)) {
							comboBox.addItem(item);
						}
					}
				}
				jtf.setText(key);
			}

			public void keyPressed(KeyEvent e) {
			}
		});

		butT1_5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String bt = butT1_5.getText();
				if (bt.equals("更換車間")) {
					butT1_5.setText("刷卡上下班");
					jtextT1_1.setText("請管理員刷卡");
					jtextT1_1.setBackground(Color.WHITE);
					textT1_3.setEditable(false);
					textT1_6.setEditable(true);
					textT1_6.setVisible(true);
					labelT1_7.setVisible(true);
					// 使用swing的线程做獲取焦點的界面绘制，避免获取不到的情况。
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							textT1_6.requestFocusInWindow();
						}
					});
				} else {
					butT1_5.setText("更換車間");
					jtextT1_1.setText("刷卡上下班");
					jtextT1_1.setBackground(Color.WHITE);
					textT1_3.setEditable(true);
					textT1_6.setEditable(false);
					textT1_6.setVisible(false);
					labelT1_7.setVisible(false);
					// 使用swing的线程做獲取焦點的界面绘制，避免获取不到的情况。
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							textT1_3.requestFocusInWindow();
						}
					});
				}
			}
		});
		
		/*
		 * l 刷管理员的卡选择车间
		 */
		textT1_6.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(TextEvent e) {
				SqlSession session = sqlSessionFactory.openSession();
				// TODO Auto-generated method stub
				String cardID = textT1_6.getText();
				if (cardID.length() > 10) {
					jtextT1_1.setBackground(Color.RED);
					jtextT1_1.setText("卡號輸入有誤，請再次刷卡\n");
					textT1_6.setText("");
				} else {
					String pattern = "^[0-9]\\d{9}$";
					Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
					Matcher m = r.matcher(cardID);
					if (m.matches() == true) {
						boolean admin = IsAdminByCardID(cardID, session);
						if (admin) {
							dispose();
							SwipeCardLogin swipeCardLogin = new SwipeCardLogin();
							textT1_6.setText("");
						} else {
							jtextT1_1.setBackground(Color.RED);
							jtextT1_1.setText("您的卡权限不够\n请刷管理员的卡");
							textT1_6.setText("");
						}
					} else {
						System.out.println("無輸入內容或輸入錯誤!");
					}
				}

			}
		});

		butT1_6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				System.exit(0);
			}
		});

		butT2_1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 中途刷卡原因
				jtf.setEditable(true);
			}
		});

		butT2_2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int countRow = mytable.getRowCount();
				Boolean State = null;			
				SwipeCardService service=new SwipeCardService();
				String WorkshopNo = workShopNoJlabel.getText();
				String RC_NO = jtf.getText();
				String PRIMARY_ITEM_NO = textT2_1.getText();
				String Name = "", empID = "";
				try {
					SqlSession session = sqlSessionFactory.openSession();
					StringBuilder strBuilder = new StringBuilder();
					for (int i = 0; i < RC_NO.length(); i++) {
						char charAt = RC_NO.charAt(i);
						if (charAt == ' ')
							continue;
						strBuilder.append(charAt);
					}
					RC_NO = strBuilder.toString();

					if (!RC_NO.equals("") && RC_NO != "" && RC_NO != null) {						
						RCLine rcLine = new RCLine();
						rcLine.setPROD_LINE_CODE(WorkshopNo);
						rcLine.setRC_NO(RC_NO);
						rcLine.setPRIMARY_ITEM_NO(PRIMARY_ITEM_NO);
						boolean isaddItem = false;
						str1 = getRcLine();
						if (str1 != null) {
							for (Object item : str1) {
								if (((String) item).equals(RC_NO)) {
									isaddItem = false;
									break;
								} else {
									isaddItem = true;
								}
							}
						}
						if (isaddItem) {
							session.insert("insertRCInfo", rcLine);
							session.commit();
						}
						for (int i = 0; i < countRow; i++) {
							State = (Boolean) mytable.getValueAt(i, 0);
							if (State == true) {
								empID = (String) mytable.getValueAt(i, 2);
								Name = (String) mytable.getValueAt(i, 3);
							    SwipeCardTimeInfos swipeInfo=new SwipeCardTimeInfos();
								swipeInfo.setEMP_ID(empID);
								swipeInfo.setRC_NO(RC_NO);
								swipeInfo.setPRIMARY_ITEM_NO(PRIMARY_ITEM_NO);
								session.update("Update_rcno_ByLineNOandCardID", swipeInfo);
								session.commit();
							}
						}
					} else {
						JOptionPane.showMessageDialog(null, "指示單號不得為空!", "提示", JOptionPane.WARNING_MESSAGE);
					}

					panel2.remove(myScrollPane);
					myModel = new SwipeCardUserTableModel(WorkshopNo, "D");
					mytable = new JTable(myModel);
					setTable();
					myScrollPane = new JScrollPane(mytable);
					myScrollPane.setBounds(310, 40, 520, 400);
					panel2.add(myScrollPane);
					panel2.updateUI(); // 重绘
					panel2.repaint(); // 重绘此组件。
					// System.out.println("State!"+ mytable.getColumnClass(0));
				} catch (Exception e1) {
					System.out.println("Error opening session");
					logger.error("綁定指示單號失敗,原因:"+e1);
					dispose();
					SwipeCardNoDB d = new SwipeCardNoDB(WorkshopNo);
					throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e1, e1);
				} finally {
					ErrorContext.instance().reset();
				}
			}
		});

		butT2_3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				update();
			}
		});

		butT2_rcno.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				str1 = getRcLine();
			}
		});

		// TODO 刷卡模式
		textT1_3.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
				SqlSession session = sqlSessionFactory.openSession();
				SwipeCardService swipeCardService=new SwipeCardService();
				String CardID = textT1_3.getText();
				SwingBase fieldSetting=null;
				Date swipeCardTime = FormatDateUtil.getDateTime();
			    String swipeCardTimeStr = FormatDateUtil.changeTimeToStr(swipeCardTime);
				String curDate=FormatDateUtil.getCurDate();				
				String yesterdayDate=FormatDateUtil.getYesterdayDate();
				
				String WorkshopNo = workShopNoJlabel.getText();
				// 驗證是否為10位整數，是則繼續執行，否則提示
				if (CardID.length() > 10) {
					jtextT1_1.setBackground(Color.red);
					jtextT1_1.setText("卡號輸入有誤，請再次刷卡\n");
					textT1_3.setText("");
				} else if(CardID.length()<10){
					jtextT1_1.setBackground(Color.RED);
					jtextT1_1.setText("卡號輸入有誤，請再次刷卡\n");
					textT1_3.setText("");
				}else {
					String pattern = "^[0-9]\\d{9}$";
					Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
					Matcher m = r.matcher(CardID);
					if (m.matches() == true) {
						try {
							// 通過卡號查詢員工個人信息
							// 1、判斷是否今天第一次刷卡
							// System.out.println("getRowsa: " +
							// rows.getRowsa());
							swipeTimeLable.setText(swipeCardTimeStr);

							Employee eif = (Employee) session.selectOne("selectUserByCardID", CardID);
							//只要刷卡都將記錄至raw_record table
							String Record_Status=null;
							swipeCardService.addRawSwipeRecord(session, eif, CardID, swipeCardTime, WorkshopNo,Record_Status);
							RawRecord swipeRecord = new RawRecord();
							swipeRecord.setCardID(CardID);
							swipeRecord.setSwipeCardTime(swipeCardTime);
							
							if (eif == null) {	
								swipeRecord.setRecord_Status("1");
								int lostRows = session.selectOne("selectLoseEmployee", swipeRecord);				
								if (lostRows > 1) {
									
									jtextT1_1.setText("已記錄當前異常刷卡人員，今天不用再次刷卡！\n");
									jtextT1_1.setBackground(Color.RED);
									textT1_3.setText("");
									session.update("updateRawRecordStatus",swipeRecord);
									session.commit();
									return;
								}
								/*
								 * JOptionPane.showMessageDialog(null,
								 * "當前刷卡人員不存在；可能是新進人員，或是舊卡丟失補辦，人員資料暫時未更新，請線長記錄，協助助理走原有簽核流程！"
								 * );
								 */
								jtextT1_1.setText("當前刷卡人員不存在；可能是新進人員，或是舊卡丟失補辦，人員資料暫時未更新，請線長記錄，協助助理走原有簽核流程！\n");
								jtextT1_1.setBackground(Color.RED);	
								session.update("updateRawRecordStatus",swipeRecord);
								session.commit();

							} else {
								String name = eif.getName();
								String RC_NO = jtf.getText();
								String PRIMARY_ITEM_NO = textT2_1.getText();
								String Id = eif.getId();						
								String PROD_LINE_CODE = linenoLabel.getText();
								//判斷該卡號是否已連續工作六天
								WorkedOneWeek workedOneWeek=swipeCardService.isUserContinuesWorkedOneWeek(session, eif, CardID, WorkshopNo, swipeCardTime);
								if(!workedOneWeek.isWorkedOneWeek()){					
									
									//該卡號是連續工作日小於六天
									EmpShiftInfos curShiftUser = new EmpShiftInfos();
								    curShiftUser.setId(Id);
								    curShiftUser.setShiftDay(0);
								     
								    EmpShiftInfos yesShiftUser = new EmpShiftInfos();
								    yesShiftUser.setId(Id);
								    yesShiftUser.setShiftDay(1);
								    
								    int empCurShiftCount =  session.selectOne("getShiftCount", curShiftUser);
									int empYesShiftCount =  session.selectOne("getShiftCount", yesShiftUser);
									EmpShiftInfos empYesShift = (EmpShiftInfos) session.selectOne("getShiftByEmpId", yesShiftUser);
								
									String yesterdayShift = "";
									if (empYesShiftCount > 0) {
										String yesterdayClassDesc = empYesShift.getClass_desc();
							
										yesterdayShift = empYesShift.getShift();
										if (yesterdayShift.equals("N")) {
											Timestamp yesClassEnd = empYesShift.getClass_end();
											Timestamp goWorkSwipeTime = new Timestamp(new Date().getTime());

											Calendar outWorkc = Calendar.getInstance();
											outWorkc.setTime(yesClassEnd);
											outWorkc.set(Calendar.HOUR_OF_DAY,
													outWorkc.get(Calendar.HOUR_OF_DAY) + 3);
											outWorkc.set(Calendar.MINUTE,
													outWorkc.get(Calendar.MINUTE) + 30);
											Date dt = outWorkc.getTime();
											Timestamp afterClassEnd = new Timestamp(dt.getTime());
											
											if (empCurShiftCount == 0) {
												if (goWorkSwipeTime.before(afterClassEnd)) {
													// 刷卡在夜班下班3.5小時之內,記為昨日夜班下刷
													fieldSetting=swipeCardService.offDutyNightShiftSwipeCard(session, RC_NO, PRIMARY_ITEM_NO, WorkshopNo, eif, swipeCardTime, empYesShift,PROD_LINE_CODE);
													showLabelContent(fieldSetting);
												}else{
													// 刷卡在夜班下班3.5小時之后,今日班別有誤
													jtextT1_1.setBackground(Color.red);
													jtextT1_1.append("ID: " + eif.getId() + " Name: " + eif.getName() + "\n班別有誤，請聯繫助理核對班別信息!\n");
													swipeRecord.setId(Id);
													swipeRecord.setRecord_Status("2");
													session.update("updateRawRecordStatus",swipeRecord);
													session.commit();
												}												
												
											} else {
												EmpShiftInfos empCurShift = (EmpShiftInfos) session.selectOne("getShiftByEmpId", curShiftUser);

												String curShift = empCurShift.getShift();
												String curClassDesc = empCurShift.getClass_desc();
												Timestamp curClassStart = empCurShift.getClass_start();
												Timestamp curClassEnd = empCurShift.getClass_end();						

												SwipeCardTimeInfos userNSwipe = new SwipeCardTimeInfos();
												Date SwipeCardTime2 = swipeCardTime;														
												userNSwipe.setEMP_ID(Id);
												userNSwipe.setSWIPE_DATE(yesterdayDate);
												userNSwipe.setSwipeCardTime2(SwipeCardTime2);
												userNSwipe.setRC_NO(RC_NO);
												userNSwipe.setPRIMARY_ITEM_NO(PRIMARY_ITEM_NO);
												userNSwipe.setShift(yesterdayShift);
												userNSwipe.setWorkshopNo(WorkshopNo);
												

												if (curShift.equals("N")) {													
													if (swipeCardTime.getHours() < 12) {
														fieldSetting=swipeCardService.offDutyNightShiftSwipeCard(session, RC_NO, PRIMARY_ITEM_NO, WorkshopNo, eif, SwipeCardTime2, empYesShift,PROD_LINE_CODE);
														showLabelContent(fieldSetting);
													} else {
														// 上班刷卡
														fieldSetting=swipeCardService.swipeCardRecord(session, eif, swipeCardTime, RC_NO, PRIMARY_ITEM_NO, WorkshopNo,PROD_LINE_CODE);
														showLabelContent(fieldSetting);
													}
												} else {													
													
													int goWorkNCardCount =  session
															.selectOne("selectGoWorkNByCardID", userNSwipe);
													if (goWorkNCardCount > 0) { 
														// 昨日夜班已存在上刷
														int yesterdaygoWorkCardCount =  session
																.selectOne("selectCountNByCardID", userNSwipe);
														if (yesterdaygoWorkCardCount == 0) {
															// 夜班下刷刷卡記錄不存在
															
															if (goWorkSwipeTime.before(afterClassEnd)) {
																// 刷卡在夜班下班3.5小時之內,記為昨日夜班下刷
																jtextT1_1.setBackground(Color.WHITE);
																jtextT1_1.setText(
																		"下班刷卡\n" + "ID: " + eif.getId() + "\nName: "
																				+ eif.getName() + "\n刷卡時間： " + swipeCardTimeStr
																				+"\n昨日班別為:"+yesterdayClassDesc
																				+ "\n" + "員工下班刷卡成功！\n------------\n");
																session.update("updateOutWorkNSwipeTime", userNSwipe);
																session.commit();
															} else {
																// 刷卡在夜班下班3.5小時之后,記為今日白班上刷
																
																fieldSetting=swipeCardService.swipeCardRecord(session, eif, swipeCardTime, RC_NO, PRIMARY_ITEM_NO, WorkshopNo,PROD_LINE_CODE);
																showLabelContent(fieldSetting);
															}
														} else {
															// 夜班下刷刷卡記錄已存在
															int isOutWoakSwipeDuplicate =  session
																	.selectOne("isOutWorkSwipeDuplicate", userNSwipe);
															if (isOutWoakSwipeDuplicate > 0) {
																fieldSetting=swipeCardService.offDutySwipeDuplicate(session, eif, swipeCardTime, curShift);
																showLabelContent(fieldSetting);
															} else {
																fieldSetting=swipeCardService.swipeCardRecord(session, eif, swipeCardTime, RC_NO, PRIMARY_ITEM_NO, WorkshopNo,PROD_LINE_CODE);
																showLabelContent(fieldSetting);
															}
														}
													} else {													
														// 昨天夜班，今天白班的，昨日夜班上刷不存在，直接記為今天白班刷卡
														fieldSetting=swipeCardService.swipeCardRecord(session, eif, swipeCardTime, RC_NO, PRIMARY_ITEM_NO, WorkshopNo,PROD_LINE_CODE);
														showLabelContent(fieldSetting);																								
													}													
												}

											}
											
										} else {
											fieldSetting=swipeCardService.swipeCardRecord(session, eif, swipeCardTime, RC_NO, PRIMARY_ITEM_NO, WorkshopNo,PROD_LINE_CODE);
											showLabelContent(fieldSetting);
										}
									} else {
										fieldSetting=swipeCardService.swipeCardRecord(session, eif, swipeCardTime, RC_NO, PRIMARY_ITEM_NO, WorkshopNo,PROD_LINE_CODE);
										showLabelContent(fieldSetting);
									}
								}
								else{
									//該卡號已連續工作六天，顯示錯誤訊息
									jtextT1_1.append("工號："+eif.getId()+" 姓名："+eif.getName()+" 已連續上班六天，此次刷卡不列入記錄！!\n");
									jtextT1_1.setBackground(Color.RED);
									
									swipeRecord.setId(Id);
									swipeRecord.setRecord_Status("4");
									session.update("updateRawRecordStatus",swipeRecord);
									session.commit();
								}
							}
						} catch (Exception e1) {
							logger.error("刷卡異常,原因:"+e1);
							dispose();
							SwipeCardNoDB d = new SwipeCardNoDB(WorkshopNo);
							throw ExceptionFactory.wrapException("刷卡異常,原因:" + e1, e1);
						} finally {
							ErrorContext.instance().reset();
							if (session != null) {
								session.close();
							}
							textT1_3.setText("");
						}
						textT1_3.setText("");
					} else {
						System.out.println("無輸入內容或輸入錯誤!");
					}
				}
				}
			}
		});

		c.add(tabbedPane);
		c.setBackground(Color.lightGray);
		
		// 使用swing的线程做獲取焦點的界面绘制，避免获取不到的情况。
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textT1_3.requestFocusInWindow();
			}
		});

		//textT1_1.setText(WorkshopNo);// 綁定車間
		workShopNoJlabel.setText(WorkshopNo);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void showLabelContent(SwingBase fieldSetting) {
		jtextT1_1.append(fieldSetting.getFieldContent());
		jtextT1_1.setBackground(fieldSetting.getFieldColor());
	}
	
	public String getShiftByClassDesc(String classDesc) {
		String shift = null;
		if (classDesc.indexOf("日") != -1 || classDesc.indexOf("中") != -1) {
			shift = "D";
		} else if (classDesc.indexOf("夜") != -1) {
			shift = "N";
		}
		return shift;
	}

	private void breakShow() {
		return;
	}

	private static void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}
	
	public static void main(String args[]) {
		boolean OneWindow = OpenOneWindow.checkLock();
		if (OneWindow) {
		InitGlobalFont(new Font("微软雅黑", Font.BOLD, 18));
		SwipeCardService swipeCardService = new SwipeCardService();
		String WorkShopNo = null;
		String LineNo =null;
		if(defaultLineNo != null){
			LineNo = defaultLineNo;
		}
		if (defaultWorkshopNo != null) {
			WorkShopNo = defaultWorkshopNo;
			SwipeCard d = new SwipeCard(WorkShopNo,LineNo);
		} else {
			SwipeCardLogin d = new SwipeCardLogin();
		}
		//檢測ip是否可用
		String ip = GetLocalHostIpAndName.getLocalIp();
		CheckIp checkIp = new CheckIp(ip);
		Thread executeCheckIp = new Thread(checkIp);
		executeCheckIp.start();
		//检测版本是否最新
		/*CheckCurrentVersion chkVersion = new CheckCurrentVersion(CurrentVersion);
		Thread executeCheckVersion = new Thread(chkVersion);
		executeCheckVersion.start();*/
		} else {
			JOptionPane.showConfirmDialog(null, "程序已經開啟，請不要重複開啟", "程序重複打開", JOptionPane.DEFAULT_OPTION);
			System.exit(0);
		}
	}

	public void update() {
		// String LineNo = textT1_2.getText();
		//String WorkshopNo = textT1_1.getText();
		String WorkshopNo = workShopNoJlabel.getText();
		Object ShiftName = comboBox2.getSelectedItem();
		System.out.println("comboBox2" + ShiftName);
		String ShiftRcNo = "";
		if (ShiftName.equals("夜班")) {
			ShiftRcNo = "N";
		} else {
			ShiftRcNo = "D";
		}

		panel2.remove(myScrollPane);
		myModel = new SwipeCardUserTableModel(WorkshopNo, ShiftRcNo);
		mytable = new JTable(myModel);

		myScrollPane = new JScrollPane(mytable);
		myScrollPane.setBounds(310, 40, 520, 400);
		setTable();
		panel2.add(myScrollPane);
		panel2.updateUI();
		panel2.repaint();
	}

	public void setTable() {
		mytable.getColumnModel().getColumn(0).setMaxWidth(40);
		mytable.getColumnModel().getColumn(1).setMaxWidth(40);
		mytable.getColumnModel().getColumn(2).setMaxWidth(60);
		mytable.setRowHeight(25);
		mytable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		JTableHeader header = mytable.getTableHeader();
		header.setFont(new Font("微软雅黑", Font.BOLD, 16));
		header.setPreferredSize(new Dimension(header.getWidth(), 30));
	}
	
	private Object[] getRcLine() {
		List<RCLine> rcLine;
		SqlSession session = sqlSessionFactory.openSession();
		try {
			rcLine = session.selectList("selectRCNo");
			int con = rcLine.size();
			System.out.println(rcLine.size());
			Object[] a = null;
			if (con > 0) {
				a = new Object[con + 1];
				a[0] = "";
				for (int i = 1; i < con + 1; i++) {
					a[i] = rcLine.get(i - 1).getRC_NO();
				}
			}
			else {
				a = new Object[1];
				a[0] = "";
			}
			final Object[] s = a;
			return a;
		} catch (Exception e1) {
			System.out.println("Error opening session");
			dispose();
			SwipeCardNoDB d = new SwipeCardNoDB(null);
			throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e1, e1);
		} finally {
			ErrorContext.instance().reset();
			if (session != null) {
				session.close();
			}
		}
	}
	
	protected boolean IsAdminByCardID(String cardID, SqlSession session) {
		// TODO Auto-generated method stub
		try {

			int isAdmin = session.selectOne("isAdminByCardID", cardID);
			if (isAdmin > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("判断是否管理员错误，原因：" + e);
			dispose();
			SwipeCardNoDB d = new SwipeCardNoDB(defaultWorkshopNo);
		}
		return false;
	}

}
