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
	public String create() {
		StringBuilder sb = new StringBuilder();

		if(this.children.size() == 1) {
			sb.append(this.children.get(0).create());
		} else {
			sb.append(this.children.get(0).create());

			for(int i=1,l=this.children.size()-1; i < l; i++) {
				sb.append(".seq(\r\n");
				sb.append(new Template("	{{:0}}").apply(this.children.get(i).create()));
				sb.append(')');
				sb.append("\r\n");
			}

			sb.delete(sb.length()-2, sb.length());
		}
		sb.append(new Template(
			".map(\r\n" +
			"	{{:0}}\r\n" +
			")"
		).apply(defaultCallbackString()));

		return sb.toString();
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
