package pkg;

import org.junit.jupiter.api.Disabled;

@Disabled(
"${someprop}" +
"@someprop@" +
"${someprop}@someprop@ @someprop@${someprop}"
)

public class C {
}
