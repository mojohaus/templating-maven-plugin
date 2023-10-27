package net.batmat.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class AppTest
{
	@Test
	public void testApp()
	{
		assertEquals("<1.0-specialversion>", pkg.SomeClass.VERSION);
		assertEquals("coquinou", pkg.SomeClass.class.getAnnotation(Disabled.class).value());
	}

	@Test
	public void testA() throws Exception
	{
		assertEquals("coquinou", pkg.A.class.getAnnotation(Disabled.class).value());
		assertEquals("coquinou", pkg.A.class.getDeclaredField("FILTER_A").get(null));
		assertEquals("coquinou", pkg.A.class.getDeclaredField("FILTER_B").get(null));
	}

	@Test
	public void testB() throws Exception
	{
		assertEquals("coquinou", pkg.B.class.getDeclaredField("FILTER_A").get(null));
		assertEquals("coquinou", pkg.B.class.getDeclaredField("FILTER_B").get(null));
	}

	@Disabled("See MOJO-2012, not sure it will ever work. To be continued...")
	@Test
	public void testB_TwoArobases() throws Exception
	{
	    assertEquals("coquinou", pkg.B.class.getAnnotation(Disabled.class).value());
	}

	@Test
	public void testC() throws Exception
	{
		assertEquals("coquinoucoquinoucoquinoucoquinou coquinoucoquinou", pkg.C.class.getAnnotation(Disabled.class).value());
	}
}
