package pkg;
import org.junit.jupiter.api.Disabled;

@Disabled( "@someprop@")
public class B {
	public static final String FILTER_A="${someprop}";
	public static final String FILTER_B="@someprop@";
}
