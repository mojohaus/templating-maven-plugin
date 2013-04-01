package net.batmat.maven;

import org.junit.Assert;

public class AppTest 
{
    @org.junit.Test
    public void testApp()
    {
        Assert.assertEquals( "<1.0-specialversion>", pkg.SomeClass.VERSION);
    }
}
