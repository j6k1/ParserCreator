package ParserCreator;

import java.util.Optional;

import FunctionalMatcher.IMatcher;
import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfCharacterClassMultiple;
import FunctionalMatcher.MatcherOfCharacterRange;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class ExpressionParser implements IParserCreator {
	public final String name;
	private final IParserCreator expr;

	protected ExpressionParser(String name, IParserCreator expr) {
		this.name = name;
		this.expr = expr;
	}

	@Override
	public String create() {
		return new Template("class {{:0}}Parser implements IMatcher<String> {\r\n" +
			"	@Override\r\n" +
			"	public Optional<MatchResult<String>> match(State state) {\r\n" +
			"		{{:1}}.match(state);\r\n" +
			"	}\r\n" +
			"}"
		).apply(this.name,this.expr.create());
	}

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfSelect.of(MatcherOfCharacterClassMultiple.of(
			MatcherOfCharacterRange.of('a','z')
		).add(MatcherOfCharacterRange.of('A','Z'))
		).next(MatcherOfGreedyZeroOrMore.of(
			(str, start, end, m) -> {
				return Optional.of(str.substring(start,end));
			},
			MatcherOfCharacterClassMultiple.of(
				MatcherOfCharacterRange.of('a','z')
			).add(MatcherOfCharacterRange.of('A','Z')
			).add(MatcherOfCharacterRange.of('0','9')
			).add(MatcherOfAsciiCharacterClass.of("_")).toContinuation()
		)).match(state).flatMap(n -> {
			return n.next(State.of(state.str, n.range.start, false),
				MatcherOfGreedyZeroOrMore.of(
					MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
				).next(MatcherOfJust.of("::=")
				).next(MatcherOfGreedyZeroOrMore.of(
					MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
				))
				.next(
					MatcherOfSelect.of(
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
						(s -> SelectParser.parse(s))
					).or(
						(s -> SequenceParser.parse(s))
					).or(
						(s -> LiteralParser.parse(s))
					).or(
						(s -> UserParser.parse(s))
					)
				).skip(MatcherOfGreedyZeroOrMore.of(
					MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
				))
			).map(r -> {
				return MatchResult.of(n.range.compositeOf(r.range),Optional.of(new ExpressionParser(n.value.get(),r.value.get())));
			});
		});
	}
}

