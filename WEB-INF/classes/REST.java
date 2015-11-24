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
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.*;


public class REST {
	final String user="383-sql";
	final String pass="test123";
	Connection conn = null;
	
	
	public void deleteStory(HttpServletRequest request, HttpServletResponse response){
		try{
		createConnections();
		
		BufferedReader br = request.getReader();
		StringBuffer sb = new StringBuffer();
		String l;
		while ((l = br.readLine()) != null) {
			sb.append(l);
		}
		System.err.println(sb.toString());
		JSONObject j = new JSONObject(sb.toString());
		String title = j.getString("title");

		Statement st = conn.createStatement();
		//System.out.println("insert into stories values ('"+title+".sty','webapps/htmlApp')");
		st.executeUpdate("delete from stories where title='"+title+"'");
		if(!Files.deleteIfExists("webapps/htmlApp/"+title))
			throw new Exception("The file did not exist");
		response.setStatus(200);
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			response.setStatus(400);
		}
	}

	public void createStory(HttpServletRequest request, HttpServletResponse response){
		try{
		createConnections();
		
		BufferedReader br = request.getReader();
		StringBuffer sb = new StringBuffer();
		String l;
		while ((l = br.readLine()) != null) {
			sb.append(l);
		}
		System.err.println(sb.toString());
		JSONObject j = new JSONObject(sb.toString());
		String title = j.getString("title");
		String pa = j.getString("page");
		Statement st = conn.createStatement();
		//System.out.println("insert into stories values ('"+title+".sty','webapps/htmlApp')");
		st.executeUpdate("insert into stories values ('"+title+".sty','webapps/htmlApp')");
		BufferedWriter bw = new BufferedWriter(new FileWriter("webapps/htmlApp/"+title+".sty"));
		bw.write("<page>"+pa+"</page>");
		bw.close();
		response.setStatus(200);
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			response.setStatus(400);
		}
	}
	
	public void editStory(HttpServletRequest request, HttpServletResponse response){
		try{
		createConnections();
		
		BufferedReader br = request.getReader();
		StringBuffer sb = new StringBuffer();
		String l;
		while ((l = br.readLine()) != null) {
			sb.append(l);
		}
		System.err.println(sb.toString());
		JSONObject j = new JSONObject(sb.toString());
		String title = j.getString("title");
		String pa = j.getString("page");
		Statement st = conn.createStatement();
		//System.out.println("insert into stories values ('"+title+".sty','webapps/htmlApp')");
		st.executeUpdate("insert into stories values ('"+title+".sty','webapps/htmlApp')");
		BufferedWriter bw = new BufferedWriter(new FileWriter("webapps/htmlApp/"+title+".sty"));
		bw.write("<page>"+pa+"</page>");
		bw.close();
		response.setStatus(200);
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			response.setStatus(400);
		}
	}
	
	
	/**
	* 	createConnections
	*Create connection to the the database. Called before any method which uses the database
	*
	*/
	private void createConnections() throws Exception{
		String url = "jdbc:mysql://localhost/383-sql";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url,user,pass);
	}
	
	/**
	*	retrieves the user from the login as a rest call
	*
	**/
	public void getUser(PrintWriter out){
		try{
		createConnections();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		rs = st.executeQuery("select user from login");
		ArrayList<String> ar = new ArrayList<String>();
		while(rs.next()){
			ar.add(rs.getString(1));		
		}
		for(int i=0;i<ar.size();i++){
			out.print("{\"user\":\""+ar.get(i)+"\"}");
		}
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		
	}
	
	/**
	* storyList
	* Reterives the list of stories as a REST call
	*
	*/
	public void storyList(PrintWriter out) throws Exception{
		
		createConnections();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		rs = st.executeQuery("select name from stories");
		ArrayList<String> ar = new ArrayList<String>();
		while(rs.next()){
			ar.add(rs.getString(1));		
		}

		//StringBuffer bf = loadStory(req,fl);
		String line = null;
		
		out.print("{\"StoryList\":[");
		
		for(int i =0;i<ar.size();i++){
			out.print("{\"pk\":"+loadStoryPageNum(ar.get(i))+",\"title\":\""+ar.get(i)+"\"}");
			if(i+1<ar.size())
				out.print(",");
		}
		
		out.print("]}");
	}
	/**
	* storyLoad
	* loads the story as a REST call
	*/
	public void storyLoad(PrintWriter out, String page) throws Exception{
		createConnections();
		String name= page.substring(page.lastIndexOf('/')+1);
		Statement st = conn.createStatement();
		ArrayList<String> stories = new ArrayList<String>();
		ResultSet rs = null;
		//out.println("selecting a story named: "+name);
		rs = st.executeQuery("select text from stories where name='"+name+"'");
		rs.next();
		BufferedReader in = new BufferedReader(new FileReader(rs.getString(1)+"/"+name));
		String str;
		StringBuffer buf = new StringBuffer();
		while ((str = in.readLine()) != null)
			buf.append(str);
		in.close();
		
		ArrayList<String> ar = new ArrayList<String>();
		for(int i=0;i<buf.lastIndexOf("</page>");i=buf.indexOf("</page>",i)+6){
			ar.add(buf.substring(i+7,buf.indexOf("</page>",i)));	
		}
		int count= ar.size();
		
		JSONObject jo = new JSONObject();
		jo.put("pages",new JSONArray(Arrays.asList(ar)));
		jo.put("author","none");
		jo.put("title",name);
		jo.put("numPages", count);
		out.println(jo.toString());
	}
	/**
	* loadStoryName
	* Provides a list of the stories in the database
	*/
	private ArrayList<String> loadStoryName() throws Exception{
		
		Statement st = conn.createStatement();
		ArrayList<String> stories = new ArrayList<String>();
		ResultSet rs = null;
	
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
	public StringBuffer loadStory(String name) throws SQLException, Exception{
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
	}
	/**
	*loadStoryPageNum
	*retreives the number of pages in the story
	*/
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
	/**
	*error
	* for failing a REST request 
	*/
	public void error(PrintWriter out){
		try{
		out.println("{\"error\":\"invalid request - no API specified\"}");		
		}
		catch(Exception e){ e.printStackTrace();}
	}

	/**
	*checkKey
	*Checks to see if a provided key is correct
	*/
	public boolean checkKey(String page) throws Exception{
		String key = getKey();
		if(page.contains(key) || page.contains("freekey"))
			return true;
		return false;
	}
	/**
	* getKeyCode
	* returns today's keycode
	*/
	public void getKeyCode(PrintWriter out) throws Exception {
	String key = getKey();
	out.println("{\"key\":\""+key+"\"}");
	}
	/**
	* getKey
	* returns a key which is a hash of today's date. This was easier than creating a new table
	* in the database
	*/
	private String getKey() throws Exception{
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
	
	/**
	* formatTime
	* returns the formated time of the system time
	*/
	private String formatTime(long t) {
		DateFormat f = new SimpleDateFormat("MM/dd/yyyy");
		return f.format(t);
	}
}
