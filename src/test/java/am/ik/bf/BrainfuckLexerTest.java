package am.ik.bf;

import java.util.List;

import am.ik.bf.BrainfuckToken.TokenType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrainfuckLexerTest {

	@Test
	void tokenize() {
		final BrainfuckLexer lexer = new BrainfuckLexer("++++++++[>++++++++<-]>+.");
		final List<BrainfuckToken> tokens = lexer.tokenize();
		assertThat(tokens).hasSize(24);
		assertThat(tokens).containsExactly( //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.LOOP_START), //
				new BrainfuckToken(TokenType.INCREMENT_POINTER), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.DECREMENT_POINTER), //
				new BrainfuckToken(TokenType.DECREMENT_VALUE), //
				new BrainfuckToken(TokenType.LOOP_END), //
				new BrainfuckToken(TokenType.INCREMENT_POINTER), //
				new BrainfuckToken(TokenType.INCREMENT_VALUE), //
				new BrainfuckToken(TokenType.OUTPUT));
	}

}