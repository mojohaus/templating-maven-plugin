package pkg;
import org.junit.Ignore;

@Ignore(
"${someprop}" +
"@someprop@" +
"${someprop}@someprop@ @someprop@${someprop}"
)

public class C {
}