package UnitTests;

import warehouse.*;


import org.junit.Assert;
import org.junit.Test;

public class OrderTest {

	@Test
	public void testGetKey() {
		String key = Order.getKey("S", "White");
		Assert.assertEquals(key, "White_S");
	}

	@Test
	public void testInitLookupMaps(){
		Order.initLookupMaps("translation.csv");

		Assert.assertEquals(Order.getSKU1LookupMap().size(), 48);
		Assert.assertEquals(Order.getSKU2LookupMap().size(), 48);
		
	}
	@Test
	public void testGetSku(){
		Order.initLookupMaps("translation.csv");
		Assert.assertEquals(Order.getSku1("S", "White"), "1a");
		Assert.assertEquals(Order.getSku2("S", "White"), "1b");
	}
	
	@Test
	public void testGetSkuNull(){
		Order.initLookupMaps("translation.csv");
		Assert.assertEquals(Order.getSku1("xxxx", "xxxx"), null);
		Assert.assertEquals(Order.getSku2("xxxxS", "White"), null);
	}
}
