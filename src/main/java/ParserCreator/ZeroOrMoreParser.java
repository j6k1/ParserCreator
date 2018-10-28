package ParserCreator;

public class ZeroOrMoreParser implements IParserCreator {
	private final IParserCreator child;

	protected ZeroOrMoreParser(IParserCreator child) {
		this.child = child;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		write(sb,indent,"MatcherOfGreedyZeroOrMore.of(");
		writeDefaultCallback(sb, indent);
		sb.append(',');
		child.create(sb, indent);
		sb.append(".toContinuation()");
		sb.append(')');
	}
}
