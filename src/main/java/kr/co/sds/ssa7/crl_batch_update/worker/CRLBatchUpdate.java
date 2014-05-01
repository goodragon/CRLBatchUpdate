package kr.co.sds.ssa7.crl_batch_update.worker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class CRLBatchUpdate {

	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("LDAP.prop"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator<String> iterator = properties.stringPropertyNames().iterator();
		while(iterator.hasNext()) {
			String url = iterator.next();
			String filter = properties.getProperty(url);
			System.out.println(url + ":" + filter);
			new Client(new SearcherPool( 5, url), filter).start();
		}
	}

}
