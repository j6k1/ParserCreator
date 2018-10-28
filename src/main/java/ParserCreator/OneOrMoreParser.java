package ParserCreator;

public class OneOrMoreParser implements IParserCreator {
	private final IParserCreator child;

	protected OneOrMoreParser(IParserCreator child) {
		this.child = child;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		write(sb,indent,"MatcherOfGreedyOneOrMore.of(");
		writeDefaultCallback(sb, indent);
		sb.append(',');
		child.create(sb, indent);
		sb.append(".toContinuation()");
		sb.append(')');
	}
}
