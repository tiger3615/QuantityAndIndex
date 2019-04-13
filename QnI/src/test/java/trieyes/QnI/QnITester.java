package trieyes.QnI;

import trieyes.qni.core.QnI;

public class QnITester {
	public static void main(String []a) throws Exception{
		QnI.init();
		while(true) {
			System.out.println(String.format("servers %d, index %d", QnI.getServerQuantity(),QnI.getSelfIndex()));
			Thread.sleep(1000);
		}
	}
}
