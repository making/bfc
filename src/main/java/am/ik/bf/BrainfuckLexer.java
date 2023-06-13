package am.ik.bf;

import java.util.ArrayList;
import java.util.List;

public final class BrainfuckLexer {

	private final String input;

	private int position;

	public BrainfuckLexer(String input) {
		this.input = input;
		this.position = 0;
	}

	public List<BrainfuckToken> tokenize() {
		final List<BrainfuckToken> tokens = new ArrayList<>();
		while (this.position < this.input.length()) {
			final char currentChar = input.charAt(position);
			tokens.add(BrainfuckToken.valueOf(currentChar));
			position++;
		}

		return tokens;
	}

}
