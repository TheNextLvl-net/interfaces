package net.thenextlvl.interfaces.reader;

import net.thenextlvl.interfaces.RenderContext;
import org.jetbrains.annotations.Contract;

/**
 * Provides methods for parsing and evaluating arithmetic expressions in interface rendering.
 *
 * @since 0.3.0
 */
public sealed interface ArithmeticsParser permits SimpleArithmeticsParser {
    /**
     * Returns the singleton instance of {@link ArithmeticsParser}.
     *
     * @return The Arithmetics instance.
     * @since 0.3.0
     */
    @Contract(pure = true)
    static ArithmeticsParser parser() {
        return SimpleArithmeticsParser.INSTANCE;
    }

    /**
     * Evaluates an arithmetic expression using the values from the given context.
     *
     * @param expression The arithmetic expression to evaluate.
     * @param context    The context to use for evaluating the expression.
     * @return The result of the evaluation.
     * @since 0.3.0
     */
    @Contract(pure = true)
    double evaluate(String expression, RenderContext context);

    /**
     * Evaluates a parsed arithmetic expression.
     *
     * @param parsed The parsed arithmetic expression to evaluate.
     * @return The result of the evaluation.
     * @since 0.3.0
     */
    @Contract(pure = true)
    double evaluate(String parsed);

    /**
     * Parses an arithmetic expression using the values from the given context.
     *
     * @param expression The arithmetic expression to parse.
     * @param context    The context to use for parsing the expression.
     * @return The parsed expression.
     * @since 0.3.0
     */
    @Contract(pure = true)
    String parse(String expression, RenderContext context);
}
