package kr.co.sds.ssa7.crl_batch_update.asis;

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


public class AsisCRLSearcher 
{
	final String OLD_CRL_DIR = "C:/CRL/SampleCRL/";
	final String NEW_CRL_DIR = "C:/CRL/NewCRL/";
	
	String url;
	String[] crlFileList;
	
	public void setURL(String url) {
		this.url = url;
	}
	
	public void setCRLFileList(final String regexPattern) {
		crlFileList = new File(OLD_CRL_DIR).list(
				new FilenameFilter(){
			Pattern pattern = Pattern.compile(regexPattern);
			
			public boolean accept(File arg0, String filename) {
				return pattern.matcher(filename).matches();
			}
		});
	}
	
	private DirContext setInitialDirContext(String url) {
		DirContext dirContext = null;
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
		System.out.println("["+url+"] Connection complete");
		return dirContext;
	}
	
    public void updateCRL() {

    	for(String crlFileName : crlFileList) {
    		saveNewCRL(seachCRL(crlFileName), crlFileName);
    	}
	}
	
    private void saveNewCRL(byte[] crlbyte, String newCRLFileName) {
    	if(crlbyte != null) {
			OutputStream os = null;
			try {
				os = new BufferedOutputStream(
						new FileOutputStream(NEW_CRL_DIR+newCRLFileName));
				os.write(crlbyte);
				os.flush();		
				os.close();
				System.out.println("[Success]"+newCRLFileName);
			} catch (Exception e) {
				e.printStackTrace();
			} 
    	}
	}

	private byte[] seachCRL(String crlFileName) {
		DirContext dirContext = setInitialDirContext(url);
		String uri = crlFileName.substring(0, crlFileName.indexOf(".crl"));
		
		NamingEnumeration<SearchResult> results = null;
    	SearchControls searchControls = new SearchControls();
    	searchControls.setSearchScope(SearchControls.OBJECT_SCOPE);
		try {
			results = dirContext.search( uri, "(objectclass=*)", searchControls);
		} catch (NamingException e1) {
			return null;
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
			return null;
		}	
		return crlbyte;
	}

}
