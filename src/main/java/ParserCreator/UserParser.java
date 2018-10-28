package ParserCreator;

public class UserParser implements IParserCreator {
	private final String name;

	protected UserParser(String name) {
		this.name = name;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		sb.append(name + ".of(");
		writeDefaultCallback(sb, indent);
		sb.append(')');
	}
}
