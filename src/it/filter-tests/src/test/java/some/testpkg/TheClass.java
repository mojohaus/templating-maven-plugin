package some.testpkg;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings( net.batmat.maven.SomeTestClass.VERSION )
public class TheClass
{
    @Test
    public void testFoo()
    {
        assertEquals( "%1.0-specialtestversion%", net.batmat.maven.SomeTestClass.VERSION );
    }
}
