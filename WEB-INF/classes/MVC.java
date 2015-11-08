/*
   MVC for book app
   zirkleac
   12/6/2015
   cse383
 */


import freemarker.template.*;
import java.util.*;
import java.io.*;
import java.sql.*;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class MVC extends HttpServlet {
	final String user="383-sql";
	final String pass="test123";
	Connection conn = null;
	
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		//PrintWriter out = response.getWriter();
		try {
			doPost(request,response);
			//this.generatePage(request,out);
		} catch (Exception e) {
			//e.printStackTrace(out);
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{ 
		//String user = request.getParameter("user");
		//String email = request.getParameter("email");
		//String ses = request.getSession().toString();
		PrintWriter out = response.getWriter();
		try {
			this.generateBook(request,out);
		}
		catch (Exception e){e.printStackTrace(out);}
	}


	/**
	*Create connection to the the database
	*
	*/
	private void createConnections(){
	try{
		String url = "jdbc:mysql://localhost/383-sql";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn.Driver.getConnection(url,user,pass);
	catch(Exception e){
		System.out.println("Trouble connecting to the database");
	}	}	}
	/**
	*Check the hash of the users password
	*
	*
	*/
	private void checkUser(String us, String pas){
		try{
		Statement st = conn.createStatement();
		ResultSet rs = null;
		rs = st.executeQuery("select pass,secret from web where user = '"+us+"'");
		String truePass = rs.getString(1);
		String secret = rs.getString(2);
		
		MessageDigest dig = MessageDigest.getInstance("SHA-256");
		dig.update((pas+secret).getBytes("UTF-8"));
		BigInteger temp = new BigInteger(1,dig.digest());
		String output = temp.toString(16);
		while ( output.length() < 32 ) {
         	   output = "0"+output;
    		}
		if(output.equals(truePass){
			return true;
		}
		
		return false;
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			return false;
		}

	}

	private void generateBook(HttpServletRequest req, PrintWriter out) throws Exception{
		
		/* Create and adjust the configuration singleton */
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/servlets"));
		Map<String,String> root = new HashMap<String,String>();
		cfg.setDefaultEncoding("UTF-8");
		
		out.println("<html><header>");
		out.println("User: "+req.getParameter("user")+"<br>");
		out.println("Email: "+req.getParameter("email")+"<br>");
		out.println("<header>");
		Template temp= null;
		ArrayList<String> names = Collections.list(req.getParameterNames());//new ArrayList<String>(Arrays.asList(req.getParameterNames()));
		for(int i=0;i<names.size();i++){
			if(names.get(i).contains(".sty")&& req.getParameter(names.get(i)).equalsIgnoreCase("on")){
				temp = cfg.getTemplate(names.get(i));
				break;
			}
		}	
		//Template temp = cfg.getTemplate(req.getParameter("bookName").concat(".sty"));
		temp.process(root,out);


	}
	/**
	*@Author Kevin
	*http://stackoverflow.com/questions/2102952/listing-files-in-a-directory-matching-a-pattern-in-java
	*
	*/
	private File[] loadStories(){
		File dir = new File("/opt/jetty/webapps/servlets");
		File[] files = dir.listFiles(new FilenameFilter(){
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".sty");
		}
		});
		return files;
		
	}
	private void generatePage(HttpServletRequest req, PrintWriter out) throws Exception {
		/* ------------------------------------------------------------------------ */    
		/* You should do this ONLY ONCE in the whole application life-cycle:        */    

		/* Create and adjust the configuration singleton */
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/servlets"));
		cfg.setDefaultEncoding("UTF-8");
		//cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW);

		/* ------------------------------------------------------------------------ */    
		/* You usually do these for MULTIPLE TIMES in the application life-cycle:   */    

		/* Create a data-model */
		Map<String,Object> root = new HashMap<String,Object>();
		HttpSession session = req.getSession();

		ArrayList<String> sty = new ArrayList<String>();
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(loadStories())); 
		for(int i=0;i<files.size();i++)
			sty.add(files.get(i).getName());
		
		root.put("stories",sty);
		/* Get the template (uses cache internally) */
		
		Template temp = cfg.getTemplate("select.ftl");

		/* Merge data-model with template */
		temp.process(root, out);
		// Note: Depending on what `out` is, you may need to call `out.close()`.
		// This is usually the case for file output, but not for servlet output.
	}

	private String formatTime(long t) {
		DateFormat f = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
		return f.format(t);
	}
}
