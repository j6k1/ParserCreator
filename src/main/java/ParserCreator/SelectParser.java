package ParserCreator;

import java.util.List;

public class SelectParser implements IParserCreator {
	private final List<IParserCreator> children;

	protected SelectParser(List<IParserCreator> children) {
		this.children = children;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		sb.append("MatcherOfSelect.of(");
		writeDefaultCallback(sb, indent);
		sb.append(',');
		this.children.get(0).create(sb, indent);

		sb.append(repeatString('\t', indent));

		for(int i=1,l=this.children.size()-1; i < l; i++) {
			sb.append(").or(");
			this.children.get(i).create(sb, indent+1);
			sb.append(')');
		}
	}
}
