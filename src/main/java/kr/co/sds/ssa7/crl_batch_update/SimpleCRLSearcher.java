package kr.co.sds.ssa7.crl_batch_update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * Hello world!
 *
 */
public class SimpleCRLSearcher 
{
    public static void main( String[] args )
    {
    	long startTime = System.currentTimeMillis();

    	String[] crlFileNames = new File("D:/CRL/OldCRL").list(
				new FilenameFilter(){
			Pattern pattern = Pattern.compile(".*o=TradeSign.*");
			
			public boolean accept(File arg0, String filename) {
				return pattern.matcher(filename).matches();
			}
		});
    	
    	String path = String.format("LDAP://%s/", "ldap.tradesign.net");
    	Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, path);
		properties.put(Context.SECURITY_AUTHENTICATION, "none");	
		DirContext context = null;
		try {
			context = new InitialDirContext(properties);
		} catch (NamingException e) {
			e.printStackTrace();
		}

    	int TotalCount = 0;
    	for(String crlFileName : crlFileNames) {

    		String uri = crlFileName.substring(0, crlFileName.indexOf(".crl"));
    		System.out.println(uri);
    		
			NamingEnumeration<SearchResult> results = null;
			SearchControls searcher = new SearchControls();
			searcher.setSearchScope(SearchControls.OBJECT_SCOPE);
			
			try {
				results = context.search( uri, "(objectclass=*)", searcher);
			} catch (NamingException e1) {
				e1.printStackTrace();
			}
			byte[] crlbyte = null;

			try {
				if (results.hasMore()) {
					SearchResult result = results.next();
					
					Attributes attrs = result.getAttributes();
					try {
						crlbyte = (byte[]) attrs.get("certificateRevocationList").get();
					} catch(NullPointerException e) {
						crlbyte = (byte[]) attrs.get("certificateRevocationList;binary").get();
					}
					if ((crlbyte == null) || (crlbyte.length == 0)) {
						System.out.println("["+uri+"] CRL Object not exist");
						return;
					}
				} else {
					System.out.println("["+uri+"] CRL Object not exist");
					return;
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}	
			
			OutputStream os = null;
			try {
				os = new BufferedOutputStream(
						new FileOutputStream("D:/CRL/NewCRL/"+crlFileName));
				os.write(crlbyte);
				os.flush();		
				os.close();
				System.out.println("[Success]"+uri);
			} catch (Exception e) {
				e.printStackTrace();
			} 


    		
    		TotalCount++;
    	}
    	System.out.println(TotalCount);
    	long endTime = System.currentTimeMillis();
        System.out.println("##  시작시간 : " + formatTime(startTime));
        System.out.println("##  종료시간 : " + formatTime(endTime));
        System.out.println("##  소요시간(초.0f) : " + ( endTime - startTime )/1000.0f +"초"); 
    }
    
    public static String formatTime(long lTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lTime);
        return (c.get(Calendar.HOUR_OF_DAY) + "시 " + c.get(Calendar.MINUTE) + "분 " + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND) + "초");
    }    // end function formatTime()
}
