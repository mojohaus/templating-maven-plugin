package pkg;
import org.junit.jupiter.api.Disabled;


/* some stupid doc
 * basedir=${basedir}
 *
 * some new code
 * et bim
 */
@Disabled("${someprop}")
public class SomeClass
{
	public static final String VERSION = "<${project.version}>";
}
