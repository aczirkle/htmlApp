/*
   MVC for book app
   zirkleac
   11/6/2015
   cse383
 */


import freemarker.template.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.math.*;
import javax.xml.*;

import javax.servlet.http.*;
import javax.servlet.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class REST extends HttpServlet {
	final String user="383-sql";
	final String pass="test123";
	Connection conn = null;
	
	
	/**
	* Organizer code, it checks to see where the user is attepmting to access and directs them.
	*
	*/
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		try {
			String page = request.getPathInfo();
			System.out.println("User attempted to access: "+page);
			if(page.contains("getKey")
				UnirestKeycode(request,out);
			
			
			
			if(checkKey(page)){
				if(page.contains("story/"))
					storyLoad();
			if(page.contains("storylist"))
				storyList(request, out);
			else{
			if(page.contains("user"))
				getUser();
			else{
				throw new Exception("No key specified");
			}
			}}
		} catch (Exception e) {
			e.printStackTrace(out);
			errorPage(request,response);
		}
		
	}
	


	/**
	*Create connection to the the database
	*
	*/
	private void createConnections() throws Exception{
		String url = "jdbc:mysql://localhost/383-sql";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url,user,pass);
	}
	

	private void getUser(HttpServletRequest request, PrintWriter out){
		try{
		createConnections();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		rs = st.executeQuery("select user from login");
		ArrayList<String> ar = ArrayList<String>();
		while(rs.next()){
			ar.add(rs.getString(1));		
		}
		for(int i=0;i<ar.size();i++){
			out.print("{\"user\":\""+ar.at(i)+"\"}");
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		
	}
	}

	private void storyList(HttpServletRequest req, PrintWriter out) throws Exception{
		
		createConnections();
		
		rs = st.executeQuery("select title from stories");
		ArrayList<String> ar = ArrayList<String>();
		while(rs.next()){
			ar.add(rs.getString(1));		
		}

		StringBuffer bf = loadStory(req,fl);
		String line = null;
		
		out.print("{\"StoryList\":[");
		
		for(int i =0;i<ar.size();i++){
			out.print("{\"pk\":"+loadStoryPageNum(ar.at(i))+",\"title\":\""+ar.at(i)+"\"}");
			if(i+1<ar.size())
				out.print(",");
		}
		
		out.print("]}");
	}
	
	private void storyLoad(){
		
		for(int i=0;i<names.size();i++){
			loadStory(req,name);
		}
	}
	
	/**
	* loadStoryName
	* Provides a list of the stories in the database
	*/
	private ArrayList<String> loadStoryName(HttpServletRequest request) throws Exception{
		
		Statement st = conn.createStatement();
		ArrayList<String> stories = new ArrayList<String>();
		ResultSet rs = null;
		String us = request.getParameter("user");
		rs = st.executeQuery("select name from stories");
		ResultSetMetaData meta = rs.getMetaData();
		int colCount = meta.getColumnCount();
		for (int col=1; col <= colCount; col++) {
			if(rs.next())
			stories.add(rs.getString(col));
		}
		return stories;
	}
	
	/**
	* loadStory
	* Loads the text and page of the story
	*/
	private StringBuffer loadStory(HttpServletRequest request,String name) throws SQLException, Exception{
		Statement st = conn.createStatement();
		ArrayList<String> stories = new ArrayList<String>();
		ResultSet rs = null;
		rs = st.executeQuery("select text from stories where name='"+name+"'");
		if(rs.next()){
		BufferedReader in = new BufferedReader(new FileReader(rs.getString(1)+"/"+name));
		String str;
		StringBuffer buf = new StringBuffer();
		while ((str = in.readLine()) != null)
			buf.append(str);
		in.close();
		return buf;
	}
	throw new RuntimeException("Error getting story names");
		//return stories;

	
	}
	
	private int loadStoryPageNum(String name) throws SQLException, Exception{
	Statement st = conn.createStatement();
	ArrayList<String> stories = new ArrayList<String>();
	ResultSet rs = null;
	rs = st.executeQuery("select text from stories where name='"+name+"'");
	if(rs.next()){
	BufferedReader in = new BufferedReader(new FileReader(rs.getString(1)+"/"+name));
	String str;
	StringBuffer buf = new StringBuffer();
	while ((str = in.readLine()) != null)
		buf.append(str);
	in.close();
	
	int lastIndex = 0;
	int count = 0;
	String p="<page>";
	while(lastIndex != -1){
		lastIndex = buf.indexOf(p,lastIndex);
		if(lastIndex != -1){
			count ++;
			lastIndex += p.length();
		}
	}
	return count;
		

	}
	throw new RuntimeException("Error getting story names");	
	}
	
	private void error(PrintWriter out) throws Exception{
		out.println("{\"error\":\"invalid request - no API specified\"}");		
	}

	private boolean checkKey(String page){
		String key = getKey();
		if(page.contains(key)
			return true;
		return false;
	}
	
	private void UnirestKeycode(HttpServletRequest req, PrintWriter out) throws Exception {
	String key = getKey();
	out.println("{\"key\":\""+key+"\"}");
	}
	private String getKey(){
		String ti = formatTime(System.currentTimeMillis());
		MessageDigest dig = MessageDigest.getInstance("SHA-256");
		dig.update(ti.getBytes("UTF-8"));
		BigInteger temp = new BigInteger(1,dig.digest());
		String output = temp.toString(16);
		while ( output.length() < 32 ) {
         	   output = "0"+output;
    	}
		return output;
	}
	private String formatTime(long t) {
		DateFormat f = new SimpleDateFormat("MM/dd/yyyy");
		return f.format(t);
	}
}
