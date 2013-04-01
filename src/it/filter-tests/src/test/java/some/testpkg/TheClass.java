package some.testpkg;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings( net.batmat.maven.SomeTestClass.VERSION )
public class TheClass
{
    @Test
    public void testFoo()
    {
        Assert.assertEquals( "%1.0-specialtestversion%", net.batmat.maven.SomeTestClass.VERSION );
    }
}
