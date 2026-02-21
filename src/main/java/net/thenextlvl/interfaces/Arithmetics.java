package net.thenextlvl.interfaces;

import java.util.Locale;

final class Arithmetics {
    public static String compile(final String expression, final RenderContext context) {
        return expression
                .replace(" ", "")
                .toLowerCase(Locale.ROOT)
                .replace("row", String.valueOf(context.row()))
                .replace("column", String.valueOf(context.column()))
                .replace("index", String.valueOf(context.index()))
                .replace("slot", String.valueOf(context.slot()));
    }

    public static double evaluate(final String compiled) {
        return parseExpression(compiled, new int[]{0});
    }

    private static double parseExpression(final String expr, final int[] pos) {
        double result = parseTerm(expr, pos);
        while (pos[0] < expr.length()) {
            final char op = expr.charAt(pos[0]);
            if (op == '+') {
                pos[0]++;
                result += parseTerm(expr, pos);
            } else if (op == '-') {
                pos[0]++;
                result -= parseTerm(expr, pos);
            } else {
                break;
            }
        }
        return result;
    }

    private static double parseTerm(final String expr, final int[] pos) {
        double result = parseFactor(expr, pos);
        while (pos[0] < expr.length()) {
            final char op = expr.charAt(pos[0]);
            if (op == '*') {
                pos[0]++;
                result *= parseFactor(expr, pos);
            } else if (op == '/') {
                pos[0]++;
                result /= parseFactor(expr, pos);
            } else {
                break;
            }
        }
        return result;
    }

    private static double parseFactor(final String expr, final int[] pos) {
        if (pos[0] < expr.length() && expr.charAt(pos[0]) == '(') {
            pos[0]++; // skip '('
            final double result = parseExpression(expr, pos);
            pos[0]++; // skip ')'
            return result;
        }
        return parseNumber(expr, pos);
    }

    private static double parseNumber(final String expr, final int[] pos) {
        final int start = pos[0];
        while (pos[0] < expr.length() && (Character.isDigit(expr.charAt(pos[0])) || expr.charAt(pos[0]) == '.')) {
            pos[0]++;
        }
        return Double.parseDouble(expr.substring(start, pos[0]));
    }
}
