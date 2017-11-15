package com.swipecard.util;

import java.net.InetAddress;

public class getLocalHostIpAndName {	
	     /* 获取本机的IP 
	     * @return Ip地址 
	     */ 
	     public static String getLocalHostIP() { 
	          String ip; 
	          try { 
	               /**返回本地主机。*/ 
	               InetAddress addr = InetAddress.getLocalHost(); 
	               ip = addr.getHostAddress();  
	          } catch(Exception ex) { 
	              ip = ""; 
	          } 
	            
	          return ip; 
	     } 
	       
	     /** 
	      * 获取本机主机名 
	      * @return 主机名
	      */ 
	     public static String getLocalHostName() { 
	          String hostName; 
	          try { 
	               InetAddress addr = InetAddress.getLocalHost(); 
	               hostName = addr.getHostName(); 
	          }catch(Exception ex){ 
	              hostName = ""; 
	          } 
	            
	          return hostName; 
	     } 
	     
	     public static void main(String[] args) {
	 		// TODO Auto-generated method stub
	 		 System.out.println("IP：" + getLocalHostIP()); 
	          System.out.println("NAME：" + getLocalHostName());
	 	}
	 	

}
