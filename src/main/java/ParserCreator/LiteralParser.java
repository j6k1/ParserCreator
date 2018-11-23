package ParserCreator;

import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfNegativeCharacterClass;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class LiteralParser implements IParserCreator {
	private final String ident;

	public LiteralParser(String ident) {
		this.ident = ident;
	}

	@Override
	public String create() {
		return new Template("MatcherOfJust.of(\r\n" +
			"	{{:0}},\r\n" +
			"	\"{{:1}}\"\r\n" +
			")"
		).apply(CodeUtil.defaultCallbackString(),this.ident);
	}

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfSelect.of(
				MatcherOfJust.of("\"")
				.next(MatcherOfGreedyZeroOrMore.of(
					(str, start, end, m) -> {
						return Optional.of((IParserCreator)new LiteralParser(str.substring(start,end)));
					},
					MatcherOfNegativeCharacterClass.of(
						MatcherOfAsciiCharacterClass.of("\"")
					).toContinuation()
				)).skip(MatcherOfJust.of("\""))
			).or(
				MatcherOfJust.of("'")
				.next(MatcherOfGreedyZeroOrMore.of(
					(str, start, end, m) -> {
						return Optional.of((IParserCreator)new LiteralParser(str.substring(start,end)));
					},
					MatcherOfNegativeCharacterClass.of(
						MatcherOfAsciiCharacterClass.of("'")
					).toContinuation()
				)).skip(MatcherOfJust.of("'"))
			).match(state);
	}
}
