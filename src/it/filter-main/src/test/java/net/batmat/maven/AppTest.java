package net.batmat.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AppTest
{
    @Test
    public void testApp()
    {
        assertEquals( "<1.0-specialversion>", pkg.SomeClass.VERSION);
    }
}
