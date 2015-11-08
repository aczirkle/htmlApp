/*
   Servlet example using freemarker template
   campbest
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


public class Exam extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws 
		ServletException, IOException {
		try{
		doGet(req,res);
		}
		catch(Exception e){
		}
		
		
	}
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			this.generatePage(request,out);
		} catch (Exception e) {
			e.printStackTrace(out);
		}
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
		Map<String,String> root = new HashMap<String,String>();
		//root.put("TIME", Long.toString(java.lang.System.currentTimeMillis()));
		//HttpSession session = req.getSession();
		//Integer n = (Integer) session.getAttribute("visits");

		root.put("grade",req.getParameter("grade"));
		//if (n==null)
		//	n = new Integer(0);
		//int nn = n.intValue()+1;
		//n=new Integer(nn);
		//session.setAttribute("visits",new Integer(nn));

		//root.put("VISITS",n.toString());

		/* Get the template (uses cache internally) */
		Template temp = cfg.getTemplate("exam.ftl");

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
