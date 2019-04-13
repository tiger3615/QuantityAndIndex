package trieyes.QnI.common;

import trieyes.qni.core.QnI;

public class UtilTest {
	
	public static void main(String []a) {
		try {
			QnI.init();
			while(true) {
				System.out.println(String.format("servers %d, index %d", QnI.getServerQuantity(),QnI.getSelfIndex()));
				Thread.sleep(1000);
			}
//			System.out.println(QnI.getServerSizeAndIndex());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
