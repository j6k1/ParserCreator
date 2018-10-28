package ParserCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfFold;
import FunctionalMatcher.MatcherOfGreedyOneOrMore;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

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

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfFold.of((str, start, end, lst) -> {
			List<IParserCreator> parsers = new ArrayList<>();

			lst.stream().forEach(r -> {
				r.value.ifPresent(p -> parsers.add((IParserCreator)p));
			});

			return  Optional.of((IParserCreator)new SelectParser(parsers));
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
		)).skip(MatcherOfJust.of("|").skip(MatcherOfGreedyZeroOrMore.of(
			MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		))).toContinuation())).match(state);
	}
}
