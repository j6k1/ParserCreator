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
	public void create(StringBuilder sb, int indent) {
		write(sb, indent, "MatcherOfJust.of(");
		writeDefaultCallback(sb, indent+1);
		sb.append(',');
		sb.append("\"" + ident + "\"");
		sb.append(')');
	}

	public static Optional<MatchResult<LiteralParser>> parse(State state) {
		return MatcherOfSelect.of(
				MatcherOfJust.of("\"")
				.next(MatcherOfGreedyZeroOrMore.of(
					(str, start, end, m) -> {
						return Optional.of(new LiteralParser(str.substring(start,end)));
					},
					MatcherOfNegativeCharacterClass.of(
						MatcherOfAsciiCharacterClass.of("\"")
					).toContinuation()
				)).skip(MatcherOfJust.of("\""))
			).or(
					MatcherOfJust.of("'")
					.next(MatcherOfGreedyZeroOrMore.of(
						(str, start, end, m) -> {
							return Optional.of(new LiteralParser(str.substring(start,end)));
						},
						MatcherOfNegativeCharacterClass.of(
							MatcherOfAsciiCharacterClass.of("'")
						).toContinuation()
					)).skip(MatcherOfJust.of("'"))
			).match(state);
	}
}
