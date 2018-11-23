package ParserCreator;

public interface IParserCreator {
	public String create();

	default String defaultCallbackString() {
		return CodeUtil.defaultCallbackString();
	}

	default String repeatString(char c, int indent) {
		return Template.repeatString(c, indent);
	}
}
