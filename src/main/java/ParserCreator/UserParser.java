package ParserCreator;

import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfCharacterClassMultiple;
import FunctionalMatcher.MatcherOfCharacterRange;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class UserParser implements IParserCreator {
	private final String name;

	protected UserParser(String name) {
		this.name = name;
	}

	@Override
	public String create() {
		return new Template(
			"{{:0}}.of(\r\n" +
			"	{{:1}}\r\n" +
			")"
		).apply(this.name,defaultCallbackString());
	}

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfSelect.of(
			MatcherOfCharacterClassMultiple.of(
				MatcherOfCharacterRange.of('a','z')
			).add(MatcherOfCharacterRange.of('A','Z'))
			.next(MatcherOfGreedyZeroOrMore.of(
				(str, start, end, m) -> {
					return Optional.of((IParserCreator)new UserParser(str.substring(start,end)));
				},
				MatcherOfCharacterClassMultiple.of(
					MatcherOfCharacterRange.of('a','z')
				).add(MatcherOfCharacterRange.of('A','Z')
				).add(MatcherOfCharacterRange.of('0','9')
				).add(MatcherOfAsciiCharacterClass.of("_")).toContinuation()
			))
		).match(state);
	}
}
