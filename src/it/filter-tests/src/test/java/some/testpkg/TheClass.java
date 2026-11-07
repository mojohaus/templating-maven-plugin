package some.testpkg;

// Compiles only if the filtered test template yields a compile-time constant.
@SuppressWarnings( net.batmat.maven.SomeTestClass.VERSION )
public class TheClass
{
    static final String VERSION = net.batmat.maven.SomeTestClass.VERSION;
}
