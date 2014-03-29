package net.batmat.maven;

import static org.junit.Assert.*;
import org.junit.Ignore;

public class AppTest 
{
    @org.junit.Test
    public void testApp()
    {
        assertEquals( "<1.0-specialversion>", pkg.SomeClass.VERSION);
        assertEquals("coquinou", pkg.SomeClass.class.getAnnotation(Ignore.class).value());
    }
}
