package kr.co.sds.ssa7.crl_batch_update.worker;

import java.util.Calendar;

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
		long startTime = System.currentTimeMillis();
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
		long endTime = System.currentTimeMillis();
		reportPerformance(startTime, endTime);
	}
	
	private void reportPerformance(long startTime, long endTime) {
        System.out.println("##  시작시간 : " + formatTime(startTime));
        System.out.println("##  종료시간 : " + formatTime(endTime));
        System.out.println("##  소요시간(초.0f) : " + ( endTime - startTime )/1000.0f +"초"); 
	}
	
	private String formatTime(long lTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lTime);
        return (c.get(Calendar.HOUR_OF_DAY) + "시 " + c.get(Calendar.MINUTE) + "분 " + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND) + "초");
    }
	
}
