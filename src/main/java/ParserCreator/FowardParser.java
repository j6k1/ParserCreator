package ParserCreator;

public class FowardParser implements IParserCreator {
	private final IParserCreator child;
	private final boolean positive;

	protected FowardParser(IParserCreator child, boolean positive) {
		this.child = child;
		this.positive = positive;
	}

	protected FowardParser(IParserCreator child) {
		this(child, true);
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		if(positive) {
			write(sb,indent,"FowardMatcher.of(");
			writeDefaultCallback(sb, indent);
			sb.append(',');
			child.create(sb, indent);
			sb.append(')');
		} else {
			write(sb,indent,"FowardMatcher.of(");
			writeDefaultCallback(sb, indent);
			sb.append(',');
			sb.append("NegativeMatcher.of(\n");
			sb.append(repeatString('\t', indent+1));
			child.create(sb, indent+1);
			sb.append('\n');
			sb.append(repeatString('\t', indent));
			sb.append("))");
		}
	}
}
