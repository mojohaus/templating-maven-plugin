package net.batmat.maven;

import pkg.some.SomeOtherClass;

// That one is impossible to do using a .properties file
// which you load for example in a static code block.
// So this is the kind of use where it comes useful.
@SuppressWarnings("for some "+pkg.SomeClass.VERSION)
public class App
{
    public static void main(String[] args )
    {
		System.out.println("Hello World!" + pkg.some.SomeOtherClass.POUET);
		System.out.println("Version:" + pkg.SomeClass.VERSION);
    }
}
