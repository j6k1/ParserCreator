package ParserCreator;

public class CodeUtil {
	public static String defaultCallbackString() {
		return "(str, start, end, m) -> {\r\n" +
			"	return Optional.of(\r\n" +
			"		str.substring(start, end));\r\n" +
			"}";
	}
}
