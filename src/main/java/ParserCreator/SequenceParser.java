package ParserCreator;

import java.util.List;

public class SequenceParser implements IParserCreator {
	private final List<IParserCreator> children;

	protected SequenceParser(List<IParserCreator> children) {
		this.children = children;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		if(this.children.size() == 1) {
			this.children.get(0).create(sb, indent);
		} else {
			this.children.get(0).create(sb, indent);

			for(int i=1,l=this.children.size()-1; i < l; i++) {
				sb.append(".seq(");
				this.children.get(i).create(sb, indent+1);
				sb.append(')');
				sb.append('\n');
			}

			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(".map(");
		writeDefaultCallback(sb, indent);
		sb.append('\n');
		sb.append(repeatString('\t', indent));
		sb.append(')');

	}
}
