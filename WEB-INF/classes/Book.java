/*
   Servlet for book
   zirkleac
   10/15/2015
   cse383
 */


import freemarker.template.*;
import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Book extends HttpServlet {

	
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			this.generatePage(request,out);
		} catch (Exception e) {
			e.printStackTrace(out);
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
		
		
		
		Template temp = cfg.getTemplate(req.getParameter("bookName").concat(".sty"));
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
		//Map<String, String> user = new HaspMap<String, String>();
		//Map<String, String> email = new HaspMap<String, String>();
		//root.put("TIME", Long.toString(java.lang.System.currentTimeMillis()));
		HttpSession session = req.getSession();
	//	Integer n = (Integer) session.getAttribute("visits");
	//	if (n==null)
	//		n = new Integer(0);
	//	int nn = n.intValue()+1;
		//n=new Integer(nn);
		//session.setAttribute("visits",new Integer(nn));

		//root.put("VISITS",n.toString());
		ArrayList<String> sty = new ArrayList<String>();
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(loadStories())); 
		//files.addAll(loadStories());
		for(int i=0;i<files.size();i++)
			sty.add(files.get(i).getName());
		
		//for(File sty : files)
		root.put("stories",sty);
		/* Get the template (uses cache internally) */
		//Template temp = cfg.getTemplate("quiz.ftl");
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
