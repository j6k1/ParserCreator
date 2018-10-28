package ParserCreator;

public interface IParserCreator {
	public void create(StringBuilder sb, int indent);

	default void write(StringBuilder sb, int indent, String str) {
		CodeWriter.write(sb, indent, str);
	}

	default void writeDefaultCallback(StringBuilder sb, int indent) {
		CodeWriter.writeDefaultCallback(sb, indent);
	}

	default String repeatString(char c, int indent) {
		return CodeWriter.repeatString(c, indent);
	}
}
