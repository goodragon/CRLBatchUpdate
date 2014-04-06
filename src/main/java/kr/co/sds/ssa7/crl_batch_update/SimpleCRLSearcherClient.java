package kr.co.sds.ssa7.crl_batch_update;

import java.util.Calendar;

public class SimpleCRLSearcherClient {

	public static void main( String[] args )
    {
    	long startTime = System.currentTimeMillis();

    	// TradeSign Test
    	SimpleCRLSearcher crlSearcher = new SimpleCRLSearcher(".*o=TradeSign.*", "ldap.tradesign.net");
    	crlSearcher.updateCRL();

    	long endTime = System.currentTimeMillis();
    	
    	reportPerformance(startTime, endTime);

    }

	private static void reportPerformance(long startTime, long endTime) {
        System.out.println("##  시작시간 : " + formatTime(startTime));
        System.out.println("##  종료시간 : " + formatTime(endTime));
        System.out.println("##  소요시간(초.0f) : " + ( endTime - startTime )/1000.0f +"초"); 
	}
	
	public static String formatTime(long lTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lTime);
        return (c.get(Calendar.HOUR_OF_DAY) + "시 " + c.get(Calendar.MINUTE) + "분 " + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND) + "초");
    }

}
