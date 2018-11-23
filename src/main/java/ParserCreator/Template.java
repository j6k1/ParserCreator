package ParserCreator;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import FunctionalMatcher.FowardMatcher;
import FunctionalMatcher.IMatcher;
import FunctionalMatcher.MatchResult;
import FunctionalMatcher.MatchResultType;
import FunctionalMatcher.MatcherOfAnyChar;
import FunctionalMatcher.MatcherOfAsciiCharacterClass;
import FunctionalMatcher.MatcherOfFold;
import FunctionalMatcher.MatcherOfGreedyOneOrMore;
import FunctionalMatcher.MatcherOfGreedyZeroOrMore;
import FunctionalMatcher.MatcherOfJust;
import FunctionalMatcher.MatcherOfSelect;
import FunctionalMatcher.State;

public class Template {
	private final String tpl;

	public Template(String tpl) {
		this.tpl = tpl;
	}

	public String apply(Object... args) {
		return MatcherOfFold.of((str, start, end, lst) -> {
			StringBuilder sb = new StringBuilder();

			for(MatchResult<String> token: lst) {
				token.value.ifPresent(t -> sb.append(t));
			}

			return Optional.of(sb.toString());
		}, MatcherOfGreedyZeroOrMore.of(
			MatcherOfSelect.of(
				MatcherOfJust.of("\"").next(
					MatcherOfFold.of((str, start, end, lst) -> {
						StringBuilder sb = new StringBuilder();

						for(MatchResult<String> token: lst) {
							token.value.ifPresent(t -> sb.append(t));
						}

						return Optional.of(sb.toString());
					},
					MatcherOfGreedyOneOrMore.of(
						MatcherOfSelect.of(MatcherOfJust.of("\\")
							.next(MatcherOfAnyChar.of((str,start,end,m) -> {
								return Optional.of(str.substring(start,end));
							}, false))
						).or(MatcherOfJust.of((str,start,end,m) -> {
							return Optional.of("!");
						}, "!!")).or(MatcherOfJust.of("!{{:").next(
							MatcherOfGreedyOneOrMore.of(
								(str,start,end,m) -> {
									return Optional.of(str.substring(start, end));
								},
								MatcherOfAsciiCharacterClass.of("0123456789").toContinuation()
							)
						).skip(MatcherOfJust.of("}}")))
						.or(new Placeholder(args,""))
						.or(MatcherOfAnyChar.of((str,start,end,m) -> {
							return Optional.of(str.substring(start,end));
						}, false)).toContinuation()
					)
				)).skip(MatcherOfJust.of("\""))
			).or(s1 -> {
				return MatcherOfGreedyZeroOrMore.of(
					(str,start,end,m) -> {
						return Optional.of(str.substring(start,end));
					},
					MatcherOfAsciiCharacterClass.of(
						new MatchResultType<String>(),
						" \t"
					).toContinuation()
				).match(s1).flatMap(r1 -> r1.next(s1,
					new Placeholder(args,r1.value.orElse(""))
				));
			}).or(
				MatcherOfAnyChar.of((str,start,end,m) -> {
					return Optional.of(str.substring(start, end));
				},true).skip(
					FowardMatcher.of(MatcherOfJust.of("{{"))
				)
			).toContinuation()
		)).match(State.of(this.tpl,0,false)).flatMap(r -> r.value).orElse("");
	}
}
class Placeholder implements IMatcher<String> {
	private final Object[] args;
	private final String indent;

	public Placeholder(Object[] args, String indent) {
		this.args = args;
		this.indent = indent;
	}

	public Optional<MatchResult<String>> match(State state) {
		return MatcherOfJust.of("{{:").next(
			MatcherOfGreedyOneOrMore.of(
				(str,start,end,m) -> {
					int index = Integer.parseInt(str.substring(start,end));

					return Optional.of(
						String.join("\n", Arrays.stream(args[index].toString().split("\\r\\n|\\r|\\n")).map(l -> {
							return indent + l;
						}).collect(Collectors.toList()))
					);
				},
				MatcherOfAsciiCharacterClass.of("0123456789").toContinuation()
			)
		).skip(MatcherOfJust.of("}}")).match(state);
	}
}

