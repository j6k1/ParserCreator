package ParserCreator;

import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class OneOrMoreParser implements IParserCreator {
	private final IParserCreator child;

	protected OneOrMoreParser(IParserCreator child) {
		this.child = child;
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		write(sb,indent,"MatcherOfGreedyOneOrMore.of(");
		writeDefaultCallback(sb, indent);
		sb.append(',');
		child.create(sb, indent);
		sb.append(".toContinuation()");
		sb.append(')');
	}

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfJust.of("(").next(MatcherOfGreedyZeroOrMore.of(
			MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		)).next(MatcherOfSelect.of(
			(str, start, end, m) -> {
				return m.flatMap(r -> r.value.map(v -> (IParserCreator)new OneOrMoreParser(v)));
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
		)).skip(MatcherOfJust.of(")+")).match(state);
	}
}
