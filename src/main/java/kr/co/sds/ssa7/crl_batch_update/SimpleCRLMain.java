package kr.co.sds.ssa7.crl_batch_update;

public class SimpleCRLMain {

	public static void main(String[] args) {
		// KICA
		new SimpleCRLSearcherClient(".*o=KICA.*", "ldap.signgate.com").start();
		// CrossCert
		new SimpleCRLSearcherClient(".*o=CrossCert.*", "dir.crosscert.com").start();
	}
}
