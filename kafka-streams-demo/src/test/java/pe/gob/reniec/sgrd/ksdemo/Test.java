package pe.gob.reniec.sgrd.ksdemo;

import static org.junit.jupiter.api.Assertions.*;

class Test {

	@org.junit.jupiter.api.Test
	void test() {
		String k = "1111_George Washington";
		System.out.println(k.substring(0, k.indexOf("_")));
		System.out.println(k.substring(k.indexOf("_") + 1));
	}

}
