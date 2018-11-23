package ParserCreator;

import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class ZeroOrMoreParser implements IParserCreator {
	private final IParserCreator child;

	protected ZeroOrMoreParser(IParserCreator child) {
		this.child = child;
	}

	@Override
	public String create() {
		return new Template("MatcherOfGreedyZeroOrMore.of(\r\n" +
			"	{{:0}},\r\n" +
			"	{{:1}}.toContinuation()\r\n" +
			")"
		).apply(defaultCallbackString(),this.child.create());
	}

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfJust.of("(").next(MatcherOfGreedyZeroOrMore.of(
			MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		)).next(MatcherOfSelect.of(
			(str, start, end, m) -> {
				return m.flatMap(r -> r.value.map(v -> (IParserCreator)new ZeroOrMoreParser(v)));
			}, (s -> FowardParser.parse(s))
		).or(
			(s -> OneOrZeroParser.parse(s))
		).or(
			(s -> OneOrMoreParser.parse(s))
		).or(
			(s -> ZeroOrMoreParser.parse(s))
		).or(
			(s -> ParenParser.parse(s))
		).or(
			(s -> SelectParser.parse(s))
		).or(
			(s -> SequenceParser.parse(s))
		).or(
			(s -> LiteralParser.parse(s))
		).or(
			(s -> UserParser.parse(s))
		)).skip(MatcherOfGreedyZeroOrMore.of(
			MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		)).skip(MatcherOfJust.of(")*")).match(state);
	}
}
