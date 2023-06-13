package am.ik.bf;

public record BrainfuckToken(TokenType type) {

	public enum TokenType {

		INCREMENT_POINTER('>'), DECREMENT_POINTER('<'), INCREMENT_VALUE('+'), DECREMENT_VALUE('-'), OUTPUT('.'),
		INPUT(','), LOOP_START('['), LOOP_END(']'), UNKNOWN((char) 0);

		final char symbol;

		TokenType(char symbol) {
			this.symbol = symbol;
		}

	}

	public static BrainfuckToken valueOf(char c) {
		for (TokenType tokenType : TokenType.values()) {
			if (tokenType.symbol == c) {
				return new BrainfuckToken(tokenType);
			}
		}
		return new BrainfuckToken(TokenType.UNKNOWN);
	}
}
