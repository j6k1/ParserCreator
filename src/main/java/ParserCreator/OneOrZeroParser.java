package ParserCreator;

public class OneOrZeroParser implements IParserCreator {
	private final IParserCreator child;

	public OneOrZeroParser(IParserCreator child) {
		this.child = child;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		write(sb,indent,"MatcherOfOneOrZero.of(");
		writeDefaultCallback(sb, indent);
		sb.append(',');
		child.create(sb, indent);
		sb.append(')');
	}
}
