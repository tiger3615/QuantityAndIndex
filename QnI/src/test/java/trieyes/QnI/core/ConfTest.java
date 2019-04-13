package sf.ibu.eric.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONException;

import junit.framework.Assert;
import sf.ibu.qni.core.Conf;
import sf.ibu.qni.core.QnI;

public class ConfTest {
	public static void main(String[] args) {
//		System.out.println(QnI.getServerSizeAndIndex("100.120.5.31"));

		List<Integer> ll = new ArrayList<>();
		List<Integer> lp = new ArrayList<>(1000000);
		long b =System.currentTimeMillis();
		for(int i=0;i<1000000;i++) {
			lp.add(i);
		}
		System.out.println(lp.size());
		System.out.println(System.currentTimeMillis()-b);
	}
}
