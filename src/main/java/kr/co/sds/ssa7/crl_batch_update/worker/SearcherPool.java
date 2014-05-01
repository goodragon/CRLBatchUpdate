package kr.co.sds.ssa7.crl_batch_update.worker;

import java.util.Hashtable;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class SearcherPool {
	
	private LinkedList<Search> searchQueue;
	private Searcher[] searchers;
	private int clientCount;

	public SearcherPool(int searcherCount, String url) {
		this.searchQueue = new LinkedList<Search>();
		this.searchers = new Searcher[searcherCount];
		clientCount = 1;
		
		String path = String.format("LDAP://%s/", url);
		System.out.println(path);

		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, path);
		properties.put(Context.SECURITY_AUTHENTICATION, "none");	

		
		for(Searcher searcher : searchers) {
			
    		DirContext context = null;
			try {
				context = new InitialDirContext(properties);
			} catch (NamingException e) {
				e.printStackTrace();
			}

			Searcher crlSearcher = new Searcher(this, context);
			searcher = crlSearcher;
			searcher.start();
		}
	}
	
    public synchronized void putSearchCRL(Search search){
    	while(searchQueue.size() >= searchers.length * 3){
    		try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	notifyAll();
    	searchQueue.addLast(search);
    }


	public synchronized Search getSearch() {
		
		while(searchQueue.size() == 0) {
			if(clientCount == 0) {
				notifyAll();
				return null;
			}
			
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		Search search = searchQueue.removeFirst();
		notifyAll();
		return search;
	}
	
    public synchronized void completeClient(){
    	this.clientCount--;
    }

}
