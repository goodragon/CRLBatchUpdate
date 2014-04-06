package kr.co.sds.ssa7.crl_batch_update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
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


public class SimpleCRLSearcher 
{
	String regexPattern;
	String url;
	DirContext dirContext;
	String[] crlFileList;
	SearchControls searcher;
	
	public SimpleCRLSearcher(String regexPattern, String url) {
		this.regexPattern = regexPattern;
		this.url = url;
		setDirContext();
		setCRLFileList();
		searcher = new SearchControls();
		searcher.setSearchScope(SearchControls.OBJECT_SCOPE);
	}
	
	private void setDirContext() {
    	String path = String.format("LDAP://%s/", url);
    	Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, path);
		properties.put(Context.SECURITY_AUTHENTICATION, "none");	
		try {
			dirContext = new InitialDirContext(properties);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private void setCRLFileList() {
		crlFileList = new File("D:/CRL/OldCRL").list(
				new FilenameFilter(){
			Pattern pattern = Pattern.compile(regexPattern);
			
			public boolean accept(File arg0, String filename) {
				return pattern.matcher(filename).matches();
			}
		});
	}
	
    public void updateCRL() {

    	for(String crlFileName : crlFileList) {
    		saveNewCRL(seachCRL(crlFileName), crlFileName);
    	}
	}
	
    private void saveNewCRL(byte[] crlbyte, String newCRLFileName) {
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(
					new FileOutputStream("D:/CRL/NewCRL/"+newCRLFileName));
			os.write(crlbyte);
			os.flush();		
			os.close();
			System.out.println("[Success]"+newCRLFileName);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private byte[] seachCRL(String crlFileName) {
		String uri = crlFileName.substring(0, crlFileName.indexOf(".crl"));
		
		NamingEnumeration<SearchResult> results = null;
		try {
			results = dirContext.search( uri, "(objectclass=*)", searcher);
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
					return null;
				}
			} else {
				System.out.println("["+uri+"] CRL Object not exist");
				return null;
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}	
		return crlbyte;
	}
}
