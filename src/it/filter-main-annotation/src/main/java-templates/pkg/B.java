package pkg;
import org.junit.Ignore;

@Ignore( "@someprop@")
public class B {
	public static final String FILTER_A="${someprop}";        
	public static final String FILTER_B="@someprop@";
}
