package net.batmat.maven;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class AppTest
{
	@Test
	public void testApp()
	{
		assertEquals("<1.0-specialversion>", pkg.SomeClass.VERSION);
		assertEquals("coquinou", pkg.SomeClass.class.getAnnotation(Ignore.class).value());
	}

	@Test
	public void testA() throws Exception
	{
		assertEquals("coquinou", pkg.A.class.getAnnotation(Ignore.class).value());
		assertEquals("coquinou", pkg.A.class.getDeclaredField("FILTER_A").get(null));
		assertEquals("coquinou", pkg.A.class.getDeclaredField("FILTER_B").get(null));
	}

	@Test
	public void testB() throws Exception
	{
		assertEquals("coquinou", pkg.B.class.getDeclaredField("FILTER_A").get(null));
		assertEquals("coquinou", pkg.B.class.getDeclaredField("FILTER_B").get(null));
	}

	@Ignore("See MOJO-2012, not sure it will ever work. To be continued...")
	@Test
	public void testB_TwoArobases() throws Exception
	{
	    assertEquals("coquinou", pkg.B.class.getAnnotation(Ignore.class).value());
	}
	
	@Test
	public void testC() throws Exception
	{
		assertEquals("coquinoucoquinoucoquinoucoquinou coquinoucoquinou", pkg.C.class.getAnnotation(Ignore.class).value());
	}
}
