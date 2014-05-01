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
			System.out.println("LDAP.prop 파일이 존재하지 않습니다.");
			return;
		} catch (IOException e) {
			System.out.println("LDAP.prp 파일 read 중 IOException이 발생했습니다.");
			return;
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
