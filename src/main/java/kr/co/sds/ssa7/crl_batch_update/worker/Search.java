package kr.co.sds.ssa7.crl_batch_update.worker;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Search {
	
	private String uri;
	private SearchControls searchControls;


	public Search(String fileName, SearchControls searchControls) {
		this.uri = fileName.substring(0, fileName.indexOf(".crl"));
		this.searchControls = searchControls;
	}
	
	public void execute(DirContext context) {
		
        NamingEnumeration<SearchResult> results;
        byte[] crlbyte = null;
		try {
			results = context.search( uri, "(objectclass=*)", searchControls);
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
		} catch (NamingException e1) {
			System.out.println("[NamingException]"+uri);
			return;
		} catch (Exception e) {
			System.out.println("[Exception]"+uri);
			e.printStackTrace();
			return;
		}

		OutputStream os = null;
		try {
			os = new BufferedOutputStream(
					new FileOutputStream("D:/CRL/NewCRL/"+uri+".crl"));
			os.write(crlbyte);
			os.flush();		
			os.close();
			System.out.println("[Success]"+uri);
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
}
