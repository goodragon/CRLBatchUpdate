package kr.co.sds.ssa7.crl_batch_update.worker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import javax.naming.directory.SearchControls;

public class Client extends Thread {
	
	private SearcherPool searcherPool;
	private String filter;
	private SearchControls searchControls;
	
	public Client(SearcherPool seacherPool, String filter){
		this.searcherPool = seacherPool;
		this.filter = filter;
		searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.OBJECT_SCOPE);
	}
	
	public void run(){
	
		String[] fileNames = new File("C:/CRL/SampleCRL/").list(
				new FilenameFilter(){
			Pattern pattern = Pattern.compile(filter);
			
			public boolean accept(File arg0, String filename) {
				return pattern.matcher(filename).matches();
			}
		});
		
		for(String fileName : fileNames){
			Search search = new Search(fileName, searchControls);
			searcherPool.putSearchCRL(search);
		}
		
		searcherPool.completeClient();
	}

}
