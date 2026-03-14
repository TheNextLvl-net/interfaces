package net.thenextlvl.interfaces.reader;

import net.thenextlvl.interfaces.RenderContext;

import java.util.Locale;

final class SimpleArithmeticsParser implements ArithmeticsParser {
    public static final ArithmeticsParser INSTANCE = new SimpleArithmeticsParser();

    private SimpleArithmeticsParser() {
    }

    @Override
    public double evaluate(String expression, RenderContext context) {
        return evaluate(parse(expression, context));
    }

    @Override
    public double evaluate(String compiled) {
        return parseExpression(compiled, new int[]{0});
    }

    @Override
    public String parse(String expression, RenderContext context) {
        return expression
                .replace(" ", "")
                .toLowerCase(Locale.ROOT)
                .replace("row", String.valueOf(context.row()))
                .replace("column", String.valueOf(context.column()))
                .replace("index", String.valueOf(context.index()))
                .replace("slot", String.valueOf(context.slot()));
    }

    private static double parseExpression(final String expression, final int[] pos) {
        double result = parseTerm(expression, pos);
        while (pos[0] < expression.length()) {
            final char op = expression.charAt(pos[0]);
            if (op == '+') {
                pos[0]++;
                result += parseTerm(expression, pos);
            } else if (op == '-') {
                pos[0]++;
                result -= parseTerm(expression, pos);
            } else {
                break;
            }
        }
        return result;
    }

    private static double parseTerm(final String expression, final int[] pos) {
        double result = parseFactor(expression, pos);
        while (pos[0] < expression.length()) {
            final char op = expression.charAt(pos[0]);
            if (op == '*') {
                pos[0]++;
                result *= parseFactor(expression, pos);
            } else if (op == '/') {
                pos[0]++;
                result /= parseFactor(expression, pos);
            } else {
                break;
            }
        }
        return result;
    }

    private static double parseFactor(final String expression, final int[] pos) {
        if (pos[0] < expression.length() && expression.charAt(pos[0]) == '(') {
            pos[0]++; // skip '('
            final double result = parseExpression(expression, pos);
            pos[0]++; // skip ')'
            return result;
        }
        return parseNumber(expression, pos);
    }

    private static double parseNumber(final String expression, final int[] pos) {
        final int start = pos[0];
        while (pos[0] < expression.length() && (Character.isDigit(expression.charAt(pos[0])) || expression.charAt(pos[0]) == '.')) {
            pos[0]++;
        }
        return Double.parseDouble(expression.substring(start, pos[0]));
    }
}
