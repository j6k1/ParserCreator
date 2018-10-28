package ParserCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfFold;
import FunctionalMatcher.MatcherOfGreedyOneOrMore;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

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

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfFold.of((str, start, end, lst) -> {
			List<IParserCreator> parsers = new ArrayList<>();

			lst.stream().forEach(r -> {
				r.value.ifPresent(p -> parsers.add((IParserCreator)p));
			});

			return  Optional.of((IParserCreator)new SequenceParser(parsers));
		},
		MatcherOfGreedyOneOrMore.of((str, start, end, m) -> m, MatcherOfSelect.of(
			(s -> FowardParser.parse(s))
		).or(
			(s -> OneOrZeroParser.parse(s))
		).or(
			(s -> OneOrMoreParser.parse(s))
		).or(
			(s -> ZeroOrMoreParser.parse(s))
		).or(
			(s -> ParenParser.parse(s))
		).or(
			(s -> LiteralParser.parse(s))
		).or(
			(s -> UserParser.parse(s))
		).skip(MatcherOfGreedyZeroOrMore.of(
			MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		)).toContinuation())).match(state);
	}
}
