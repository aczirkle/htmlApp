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


public class MVC extends HttpServlet {
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
			if(page.contains("select") && (checkCookies(request) || checkUser(request, response))){
				doPost(request,response);
			}
			if(page.contains("makePage")){
				makePage(request, out);
				//doPost(request,response);
			}
			if(page.contains("makeUser")){
				makeUser(request, response);
				//doPost(request,response);
			}
			//String page = request.getServletPath();
			if(page.contains("login") || page.equals("/"))
				loginPage(request, out);
			/*else{
			if((checkCookies(request) || checkUser(request, response)) && page.contains("select")){
				createConnections();
				doPost(request, response);
			}*/

			//errorPage(request,response);
		} catch (Exception e) {
			e.printStackTrace(out);
			errorPage(request,response);
		}
	}
	
	/**
	*	doPost	
	*	Spliter code for generating the pages
	*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{ 
		PrintWriter out = response.getWriter();
		try {
			this.generateBook(request,out);
		}
		catch (Exception e){e.printStackTrace(out);}
	}
	
	/**
	*	errorPage
	*	Provides an error page for the user with the location they were trying to access
	*/
	private void errorPage(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		out.println("I ran into an error.");
		out.println("You requested "+request.getPathInfo());
		out.close();
		/* todo login data if avilable*/
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
	
	/**
	*	checkCookies
	*	checks to see if the user is already logged in to the server
	*/
	private boolean checkCookies(HttpServletRequest request){
		Cookie[] co = request.getCookies();
		String pass;
		if(co!=null){
			for(int i=0;i<co.length;i++)
				if(co[i].getName().equals("use"))
					return true;
		}
		return false;
	}
	
	/**
	*Check the hash of the users password
	*
	*
	*/
	private boolean checkUser(HttpServletRequest request, HttpServletResponse response){
		try{
			createConnections();
			String us = request.getParameter("user");
			String pas = request.getParameter("pass");
			if(us==null || pas==null)
				return false;
			PrintWriter out = new PrintWriter("java.log");
			out.println("Login attempt from:"+request.getRemoteAddr()+" with username:"+us);
			out.println("select pass,secret from login where user = '"+us+"\"'");
			out.close();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		rs = st.executeQuery("select pass,secret from login where user = '"+us+"'");
		if(rs.next()){
		String truePass = rs.getString(1);
		String secret = rs.getString(2);
		out.println("Pass: "+truePass +" secret: "+ secret);
		MessageDigest dig = MessageDigest.getInstance("SHA-256");
		dig.update((pas+secret).getBytes("UTF-8"));
		BigInteger temp = new BigInteger(1,dig.digest());
		String output = temp.toString(16);
		while ( output.length() < 32 ) {
         	   output = "0"+output;
    		}
		if(output.equals(truePass)){
			Cookie co = new Cookie("use","online");
			co.setMaxAge(1000);
			response.addCookie(co);
			return true;
		}
		}
		throw new RuntimeException("Database error");
		//return false;
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			return false;
		}
		
	}
	
	private boolean makeUser(HttpServletRequest request, HttpServletResponse response){
		try{
			String us = request.getParameter("user");
			String pas = request.getParameter("pass");
			if(us==null || pas==null)
				return false;
			PrintWriter out = new PrintWriter("java.log");
			out.println("User attempt create from:"+request.getRemoteAddr()+" with username:"+us);
			out.close();
		
		int secret = (int) (System.currentTimeMillis() / 1000L);
		
		MessageDigest dig = MessageDigest.getInstance("SHA-256");
		dig.update((pas+secret).getBytes("UTF-8"));
		BigInteger temp = new BigInteger(1,dig.digest());
		String output = temp.toString(16);
		while ( output.length() < 32 ) {
         	   output = "0"+output;
    	}
		
		createConnections();
		
		Statement st = conn.createStatement();
		st.executeUpdate("insert into login values('"+us+"','"+output+"','"+secret+"')");
		
	
		Cookie co = new Cookie("use","online");
		co.setMaxAge(1000);
		response.addCookie(co);
		return true;
		
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			return false;
		}
		
	}
	
	

	/**
	* Provides the simple login page for the app
	*
	*/
	private void loginPage(HttpServletRequest req, PrintWriter out) throws Exception{
		
		/* Create and adjust the configuration singleton */
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/htmlApp"));
		Map<String,String> root = new HashMap<String,String>();
		cfg.setDefaultEncoding("UTF-8");
		Template temp = cfg.getTemplate("login.ftl");
		temp.process(root,out);
	}
	
	
		private void makePage(HttpServletRequest req, PrintWriter out) throws Exception{
		
		/* Create and adjust the configuration singleton */
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/htmlApp"));
		Map<String,String> root = new HashMap<String,String>();
		cfg.setDefaultEncoding("UTF-8");
		Template temp = cfg.getTemplate("make.ftl");
		temp.process(root,out);
	}
	
	
	/**
	*Generates the pages for the book
	*
	*/
	private void generateBook(HttpServletRequest req, PrintWriter out) throws Exception{
		
		/* Create and adjust the configuration singleton */
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/htmlApp"));
		Map<String,String> root = new HashMap<String,String>();
		cfg.setDefaultEncoding("UTF-8");
		
		createConnections();
		
		//Statement st = conn.createStatement();
		//ResultSet rs = null;
		//String us = req.getParameter("user");
		//rs = st.executeQuery("select email from web where user = '"+us+"'");
		//String user = rs.getString(1);
		//String email = rs.getString(2);
		
		
		
		out.println("<html><header><div align=\"center\">");
		//out.println("User: "+user+"<br>");
		//out.println("Email: "+email+"<br>");
		out.println("</div><header>");
		Template temp= null;
		//BufferedReader fl= null;
		String fl = null;
		ArrayList<String> names = Collections.list(req.getParameterNames());
		for(int i=0;i<names.size();i++){
			if(names.get(i).contains(".sty")&& req.getParameter(names.get(i)).equalsIgnoreCase("on")){
				fl=names.get(i);
				//fl = BufferedReader(new FileReader("/opt/jetty/webapps/htmlApp/"+names.get(i)));
				//temp = cfg.getTemplate(names.get(i));
				break;
			}
		}
		
	
		
		ArrayList<String> ar = new ArrayList<String>();
		StringBuffer bf = loadStory(req,fl);
		String line = null;
		
		for(int i=0;i<bf.lastIndexOf("</page>");i=bf.indexOf("</page>",i)+7){
			ar.add(bf.substring(i,bf.indexOf("</page>",i)+7));	
		}
		/*while((line = fl.readLine())!= null){
			if(line.contains("<page>")){
				br.append(line);
				while((line = fl.readLine())!= null){
					br.append(line);
					if(line.contains("</page>")){
						break;
					}
				}
				ar.add(bf.toString(());
				bf = new StringBuffer();
			}
		}*/
		String page = req.getServletPath();
		int p = page.lastIndexOf('/');
		if(p<0)
			p=0;
		if(p>ar.size())
			p=ar.size();
		
		out.println(ar.get(p));
		
		
		//FormSection for buttons
	
		if(p>0)
			out.println("<div align=\"center\">	<form action=\"select/"+(p-1)+" ><input type=\"submit\" value=\"Submit\"></div>");
	
		if(p<ar.size())
			out.println("<div align=\"center\"> <form action=\"select/"+(p+1)+" ><input type=\"submit\" value=\"Submit\"></div>");
		//End form section
		

		temp.process(root,out);


	}
	/**
	*@Author Kevin
	*http://stackoverflow.com/questions/2102952/listing-files-in-a-directory-matching-a-pattern-in-java
	*
	*/
	/*private File[] loadStories(){
		File dir = new File("/opt/jetty/webapps/servlets");
		File[] files = dir.listFiles(new FilenameFilter(){
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".sty");
		}
		});
		return files;
		
	}*/
	
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
		BufferedReader in = new BufferedReader(new FileReader(rs.getString(1)));
		String str;
		StringBuffer buf = new StringBuffer();
		while ((str = in.readLine()) != null)
			buf.append(str);
		in.close();
		return buf;
	}
	throw new RuntimeException("Error getting story names");
	return null;
		//return stories;

	
	}
	
	/**
	* generatePage
	* creates the select a story page with a list of story names
	*/
	private void generatePage(HttpServletRequest req, PrintWriter out) throws Exception {
		/* ------------------------------------------------------------------------ */    
		/* You should do this ONLY ONCE in the whole application life-cycle:        */    

		/* Create and adjust the configuration singleton */
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/htmlApp"));
		cfg.setDefaultEncoding("UTF-8");
		//cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW);

		/* ------------------------------------------------------------------------ */    
		/* You usually do these for MULTIPLE TIMES in the application life-cycle:   */    

		/* Create a data-model */
		Map<String,Object> root = new HashMap<String,Object>();
		HttpSession session = req.getSession();

		//ArrayList<String> sty = new ArrayList<String>();
		//ArrayList<File> files = new ArrayList<File>(Arrays.asList(loadStories())); 
		//for(int i=0;i<files.size();i++)
		//	sty.add(files.get(i).getName());
		
		root.put("stories",loadStoryName(req));

		
		Template temp = cfg.getTemplate("select.ftl");
		temp.process(root, out);

	}

	private String formatTime(long t) {
		DateFormat f = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
		return f.format(t);
	}
}
