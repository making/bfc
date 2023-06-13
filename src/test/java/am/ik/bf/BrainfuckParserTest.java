package am.ik.bf;

import java.util.List;

import am.ik.bf.expression.Expression;
import am.ik.bf.expression.IncrementPointerExpression;
import am.ik.bf.expression.IncrementValueExpression;
import am.ik.bf.statement.ExpressionStatement;
import am.ik.bf.statement.LoopStatement;
import am.ik.bf.statement.Statement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrainfuckParserTest {

	@Test
	void parseNoLoop() {
		final List<Statement> statements = BrainfuckParser.parse("++-->><<");
		assertThat(statements).hasSize(1);
		assertThat(statements.get(0)).isInstanceOf(ExpressionStatement.class);
		final List<Expression> expressions = ((ExpressionStatement) statements.get(0)).expressions();
		assertThat(expressions).hasSize(8);
		assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(1));
		assertThat(expressions.get(1)).isEqualTo(new IncrementValueExpression(1));
		assertThat(expressions.get(2)).isEqualTo(new IncrementValueExpression(-1));
		assertThat(expressions.get(3)).isEqualTo(new IncrementValueExpression(-1));
		assertThat(expressions.get(4)).isEqualTo(new IncrementPointerExpression(1));
		assertThat(expressions.get(5)).isEqualTo(new IncrementPointerExpression(1));
		assertThat(expressions.get(6)).isEqualTo(new IncrementPointerExpression(-1));
		assertThat(expressions.get(7)).isEqualTo(new IncrementPointerExpression(-1));
	}

	@Test
	void parseWithLoop() {
		final List<Statement> statements = BrainfuckParser.parse("++[>++<-]+");
		assertThat(statements).hasSize(3);
		assertThat(statements.get(0)).isInstanceOf(ExpressionStatement.class);
		assertThat(statements.get(1)).isInstanceOf(LoopStatement.class);
		assertThat(statements.get(2)).isInstanceOf(ExpressionStatement.class);
		{
			final List<Expression> expressions = ((ExpressionStatement) statements.get(0)).expressions();
			assertThat(expressions).hasSize(2);
			assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(1));
			assertThat(expressions.get(1)).isEqualTo(new IncrementValueExpression(1));
		} //
		{
			final List<Statement> loopStatements = ((LoopStatement) statements.get(1)).statements();
			assertThat(loopStatements).hasSize(1);
			final List<Expression> expressions = ((ExpressionStatement) loopStatements.get(0)).expressions();
			assertThat(expressions).hasSize(5);
			assertThat(expressions.get(0)).isEqualTo(new IncrementPointerExpression(1));
			assertThat(expressions.get(1)).isEqualTo(new IncrementValueExpression(1));
			assertThat(expressions.get(2)).isEqualTo(new IncrementValueExpression(1));
			assertThat(expressions.get(3)).isEqualTo(new IncrementPointerExpression(-1));
			assertThat(expressions.get(4)).isEqualTo(new IncrementValueExpression(-1));
		} //
		{
			final List<Expression> expressions = ((ExpressionStatement) statements.get(2)).expressions();
			assertThat(expressions).hasSize(1);
			assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(1));
		}
	}

	@Test
	void parseWithNestedLoop() {
		final List<Statement> statements = BrainfuckParser.parse("+[+[+]-]-");
		assertThat(statements).hasSize(3);
		assertThat(statements.get(0)).isInstanceOf(ExpressionStatement.class);
		assertThat(statements.get(1)).isInstanceOf(LoopStatement.class);
		assertThat(statements.get(2)).isInstanceOf(ExpressionStatement.class);
		{
			final List<Expression> expressions = ((ExpressionStatement) statements.get(0)).expressions();
			assertThat(expressions).hasSize(1);
			assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(1));
		} //
		{
			final List<Statement> loopStatements = ((LoopStatement) statements.get(1)).statements();
			assertThat(loopStatements).hasSize(3);
			assertThat(loopStatements.get(0)).isInstanceOf(ExpressionStatement.class);
			assertThat(loopStatements.get(1)).isInstanceOf(LoopStatement.class);
			assertThat(loopStatements.get(2)).isInstanceOf(ExpressionStatement.class);
			{
				final List<Expression> expressions = ((ExpressionStatement) loopStatements.get(0)).expressions();
				assertThat(expressions).hasSize(1);
				assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(1));
			} //
			{
				final List<Statement> nestedLoopStatements = ((LoopStatement) loopStatements.get(1)).statements();
				assertThat(nestedLoopStatements).hasSize(1);
				assertThat(nestedLoopStatements.get(0)).isInstanceOf(ExpressionStatement.class);
				{
					final List<Expression> expressions = ((ExpressionStatement) nestedLoopStatements.get(0))
						.expressions();
					assertThat(expressions).hasSize(1);
					assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(1));
				}
			} //
			{
				final List<Expression> expressions = ((ExpressionStatement) loopStatements.get(2)).expressions();
				assertThat(expressions).hasSize(1);
				assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(-1));
			}
		} //
		{
			final List<Expression> expressions = ((ExpressionStatement) statements.get(2)).expressions();
			assertThat(expressions).hasSize(1);
			assertThat(expressions.get(0)).isEqualTo(new IncrementValueExpression(-1));
		}
	}

}