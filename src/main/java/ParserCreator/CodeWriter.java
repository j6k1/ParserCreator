package ParserCreator;

public class CodeWriter {
	public static void write(StringBuilder sb, int indent, String str) {
		String[] lines = str.split("\\r\\n|\\r|\\n");

		if(lines.length == 1) {
			sb.append(lines[0]);
		} else {
			sb.append(lines[0]);

			for(int i=1,l=lines.length; i < l; i++) {
				for(int j=0; j < indent; j++) {
					sb.append("\t");
				}
				sb.append(lines[i]);
				sb.append('\n');
			}

			sb.deleteCharAt(sb.length()-1);
		}
	}

	public static void writeDefaultCallback(StringBuilder sb, int indent) {
		write(sb,indent,"(str, start, end, m) -> {\n" +
			"	return Optional.of(\n" +
			"		str.substring(start, end));\n" +
			"}");
	}

	public static String repeatString(char c, int indent) {
		StringBuilder sb = new StringBuilder();

		for(int i=0; i < indent; i++) sb.append(c);

		return sb.toString();
	}
}
