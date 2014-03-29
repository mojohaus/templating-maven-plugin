package pkg;
import org.junit.Ignore;

/* some stupid doc
 * basedir=${basedir}
 * 
 * some new code
 * et bim
 */
@Ignore("${someprop}")
public class SomeClass
{
	public static final String VERSION = "<${project.version}>";
}
