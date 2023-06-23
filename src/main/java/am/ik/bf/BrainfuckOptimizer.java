package am.ik.bf;

import java.util.List;

import am.ik.bf.statement.Statement;

public interface BrainfuckOptimizer {

	List<Statement> optimize(List<Statement> statements);

}
