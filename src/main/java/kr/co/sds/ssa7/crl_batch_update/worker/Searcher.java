package kr.co.sds.ssa7.crl_batch_update.worker;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public class Searcher extends Thread {
	
	private SearcherPool searcherPool;
	private DirContext context;
	
	public Searcher(SearcherPool searcherPool, DirContext context) {
		this.searcherPool = searcherPool;
		this.context = context;
	}

	public void run() {
		while(true){
			Search search = searcherPool.getSearch();
			if(search != null)
			    search.execute(context);
			else {
				try {
					context.close();
					System.out.println("["+Thread.currentThread().getName()+"] LDAP Connection closed");
				} catch (NamingException e) {
					System.out.println("["+Thread.currentThread().getName()+"] LDAP Connection closing failed");
				}
				break;
			}
		}
	}

	
}
