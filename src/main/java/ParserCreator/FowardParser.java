package ParserCreator;

import java.util.Optional;

import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class FowardParser implements IParserCreator {
	private final IParserCreator child;
	private final boolean positive;

	protected FowardParser(IParserCreator child, boolean positive) {
		this.child = child;
		this.positive = positive;
	}

	protected FowardParser(IParserCreator child) {
		this(child, true);
	}

	@Override
	public void create(StringBuilder sb, int indent) {
		if(positive) {
			write(sb,indent,"FowardMatcher.of(");
			writeDefaultCallback(sb, indent);
			sb.append(',');
			child.create(sb, indent);
			sb.append(')');
		} else {
			write(sb,indent,"FowardMatcher.of(");
			writeDefaultCallback(sb, indent);
			sb.append(',');
			sb.append("NegativeMatcher.of(\n");
			sb.append(repeatString('\t', indent+1));
			child.create(sb, indent+1);
			sb.append('\n');
			sb.append(repeatString('\t', indent));
			sb.append("))");
		}
	}

	public static Optional<MatchResult<IParserCreator>> parse(State state) {
		return MatcherOfSelect.of(MatcherOfJust.of("(?=")
			.next(MatcherOfGreedyZeroOrMore.of(
				MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
			)).next(MatcherOfSelect.of(
				(str, start, end, m) -> {
					return m.flatMap(r -> r.value.map(v -> (IParserCreator)new FowardParser(v)));
				}, (s -> ParticalParen.parse(s))
			).or(
				(s -> SelectParser.parse(s))
			).or(
				(s -> SequenceParser.parse(s))
			).or(
				(s -> LiteralParser.parse(s))
			)).skip(MatcherOfGreedyZeroOrMore.of(
				MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
		)).skip(MatcherOfJust.of(")"))
		).or(MatcherOfJust.of("(?!")
			.next(MatcherOfGreedyZeroOrMore.of(
				MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
			)).next(MatcherOfSelect.of(
				(str, start, end, m) -> {
					return m.flatMap(r -> r.value.map(v -> (IParserCreator)new FowardParser(v,false)));
				}, (s -> ParticalParen.parse(s))
			).or(
				(s -> SelectParser.parse(s))
			).or(
				(s -> SequenceParser.parse(s))
			).or(
				(s -> LiteralParser.parse(s))
			)).skip(MatcherOfGreedyZeroOrMore.of(
				MatcherOfAsciiCharacterClass.of(" \t\r\n").toContinuation()
			)).skip(MatcherOfJust.of(")"))
		).match(state);
	}
}
