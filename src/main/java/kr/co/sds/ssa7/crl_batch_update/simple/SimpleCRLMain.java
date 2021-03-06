package kr.co.sds.ssa7.crl_batch_update.simple;


public class SimpleCRLMain {

	public static void main(String[] args) {
		// 1.GPKI1 50개
//		new SimpleCRLSearcherClient(".*cn=CA1310[0-9]{5},ou=GPKI.*", "ldap.gcc.go.kr").start();
		// 2.GPKI2 50개
		new SimpleCRLSearcherClient(".*cn=CA1311[0-9]{5},ou=GPKI.*", "cen.dir.go.kr").start();
		// 3.EPKI 잠실에서는 막혀있음
//		new SimpleCRLSearcherClient(".*ou=CRL,ou=GPKI.*", "ldap.epki.go.kr").start();
		// 4.GPKI(대검찰청)
//		new SimpleCRLSearcherClient(".*ou=crldp[0-9],ou=GPKI.*", "ldap.spo.go.kr").start();
		// 5.GPKI(대법원) 50개 잠실에서 막혀있음
//		new SimpleCRLSearcherClient(".*CN=CA9740[0-9]{5},OU=GPKI.*", "ldap.scourt.go.kr").start();
		// 6.금융결제원 200개
		new SimpleCRLSearcherClient(".*o=yessign.*", "ds.yessign.or.kr").start();
		// 7.증권전산 100개
		new SimpleCRLSearcherClient(".*o=SignKorea.*", "dir.signkorea.com").start();
		// 8.무역정보 100개
		new SimpleCRLSearcherClient(".*o=TradeSign.*", "ldap.tradesign.net").start();
		// 9.정보인증 100개
		new SimpleCRLSearcherClient(".*o=KICA.*", "ldap.signgate.com").start();
		// 10.전자인증 50개
		new SimpleCRLSearcherClient(".*o=CrossCert.*", "dir.crosscert.com").start();
		
	}
}
