package kr.co.sds.ssa7.crl_batch_update.worker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class Client extends Thread {
	private SearcherPool searcherPool;
	private String filter;
	
	public Client(SearcherPool seacherPool, String filter){
		this.searcherPool = seacherPool;
		this.filter = filter;	
	}
	
	public void run(){
	
		String[] fileNames = new File("D:/CRL/OldCRL/").list(
				new FilenameFilter(){
			Pattern pattern = Pattern.compile(filter);
			
			public boolean accept(File arg0, String filename) {
				return pattern.matcher(filename).matches();
			}
		});
		
		for(String fileName : fileNames){
			Search search = new Search(fileName);
			searcherPool.putSearchCRL(search);
		}
		
		searcherPool.completeClient();
	}

}
