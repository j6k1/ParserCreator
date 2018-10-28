package ParserCreator;

import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class ParticalParen implements IParserCreator {
	private final IParserCreator child;

	protected ParticalParen(IParserCreator child) {
		this.child = child;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		child.create(sb, indent);
	}

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfJust.of("(").next(MatcherOfGreedyZeroOrMore.of(
			MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		)).next(MatcherOfSelect.of(
			(s -> ParticalParen.parse(s))
		).or(
			(s -> SelectParser.parse(s))
		).or(
			(s -> SequenceParser.parse(s))
		).or(
			(s -> LiteralParser.parse(s))
		)).skip(MatcherOfGreedyZeroOrMore.of(
			MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		)).skip(MatcherOfJust.of(")")).match(state);
	}
}
