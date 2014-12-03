package org.wikiup.core.impl.el;

import org.wikiup.core.exception.IllegalExpressionException;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalExpressionLanguage implements ExpressionLanguage<Getter<?>, Object> {
    private static final String OPTS = "+-*/%><!|&=";
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("^([\\w\\d_]+)\\(([^)]+)\\)$");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\-?\\d*\\.?\\d*");

    public Object evaluate(Getter<?> context, String script) {
        String value = ValueUtil.toString(compile(script).calucate());
        return toObject(value);
    }

    private Object toObject(String value) {
        int idx = value.indexOf('.');
        if(idx != -1 && ValueUtil.toInteger(value.substring(idx + 1), -1) == 0)
            return Integer.valueOf(ValueUtil.toInteger(value.substring(0, idx), 0));
        if(ValueUtil.isInteger(value))
            return Integer.valueOf(value);
        return ValueUtil.toDouble(value, 0);
    }

    private int getOperatorPriority(char operator) {
        switch(operator) {
            case '+':
            case '-':
                return 0;
            case '%':
                return 3;
            case '*':
            case '/':
                return 5;
            case '=':
            case '<':
            case '>':
                return -5;
        }
        return -1000;
    }

    private boolean isOperator(char value) {
        return OPTS.indexOf(value) != -1;
    }

    private boolean isVariable(String expr) {
        return VARIABLE_PATTERN.matcher(expr).matches();
    }

    private ExpressionNode compile(String expr) {
        String el = trimExpression(expr);
        ExpressionNode exprNode = simpleCompile(el);
        if(exprNode == null) {
            Stack<ExpressionNode> exprs = new Stack<ExpressionNode>();
            Stack<Character> operators = new Stack<Character>();
            splitByOperator(el, exprs, operators);
            exprNode = reduce(exprs, operators);
            Assert.notNull(exprNode, IllegalExpressionException.class, expr);
        }
        return exprNode;
    }

    private ExpressionNode simpleCompile(String expr) {
        return isVariable(expr) ? new VariableExpressionNode(expr) : compileFunction(expr);
    }

    private ExpressionNode compileFunction(String expr) {
        Matcher matcher = parseFunction(expr);
        if(matcher != null) {
            String[] params = matcher.group(2).split(",");
            ExpressionNode[] exprs = new ExpressionNode[params.length];
            int i;
            for(i = 0; i < exprs.length; i++)
                exprs[i] = compile(params[i]);
            return new FunctionCallExpressionNode(matcher.group(1), exprs);
        }
        return null;
    }

    private String trimExpression(String expr) {
        String el = StringUtil.trim(expr);
        while(el.startsWith("(") && expr.endsWith(")"))
            el = el.substring(1, el.length() - 1);
        return el;
    }

    private ExpressionNode reduce(Stack<ExpressionNode> exprs, Stack<Character> operators) {
        while(exprs.size() > 2) {
            ExpressionNode rvalue = exprs.pop();
            char op2 = operators.pop();
            if(getOperatorPriority(op2) > getOperatorPriority(operators.peek()))
                exprs.push(new ArithmeticExpressionNode(exprs.pop(), rvalue, op2));
            else
                return new ArithmeticExpressionNode(reduce(exprs, operators), rvalue, op2);
        }
        return exprs.size() == 2 ? new ArithmeticExpressionNode(exprs.get(0), exprs.get(1), operators.get(0)) : null;
    }

    private void splitByOperator(String expr, List<ExpressionNode> exprs, List<Character> operators) {
        int lastPos = 0, i, len = expr.length();
        for(i = 0; i < len; i++) {
            char c = expr.charAt(i);
            if(isOperator(c)) {
                appendExpression(exprs, expr.substring(lastPos, i));
                operators.add(c);
                lastPos = i + 1;
            } else if(c == '(') {
                int endPos = getBracketBlockEndPos(expr, i);
                exprs.add(compile(expr.substring(lastPos, endPos)));
                i = endPos - 1;
                lastPos = endPos;
            }
        }
        appendExpression(exprs, expr.substring(lastPos));
    }

    private void appendExpression(List<ExpressionNode> exprs, String expr) {
        String exp = StringUtil.trim(expr);
        if(StringUtil.isNotEmpty(exp))
            exprs.add(compile(exp));
    }

    private int getBracketBlockEndPos(String expr, int offset) {
        int depth = 1, i, len = expr.length();
        for(i = offset + 1; i < len && depth != 0; i++) {
            char c = expr.charAt(i);
            if(c == ')')
                depth--;
            else if(c == '(')
                depth++;
        }
        return (i < len || depth == 0) ? i : -1;
    }

    private Matcher parseFunction(String value) {
        Matcher matcher = FUNCTION_PATTERN.matcher(value);
        return matcher.matches() ? matcher : null;
    }

    private static interface ExpressionNode {
        public Object calucate();
    }

    private static class VariableExpressionNode implements ExpressionNode {
        private double variable;

        public VariableExpressionNode(Object variable) {
            this.variable = ValueUtil.toDouble(variable, -1);
        }

        public Object calucate() {
            return variable;
        }
    }

    private static class FunctionCallExpressionNode implements ExpressionNode {
        private String function;
        private ExpressionNode[] parameters;

        public FunctionCallExpressionNode(String function, ExpressionNode[] parameters) {
            this.function = function;
            this.parameters = parameters;
        }

        public Object calucate() {
            String func = function.toLowerCase();
            Class[] clsParam = new Class[parameters.length];
            Object[] dbParam = new Object[parameters.length];
            Object ret = null;
            int i;
            for(i = 0; i < dbParam.length; i++) {
                dbParam[i] = ValueUtil.toDouble(parameters[i].calucate(), -1);
                clsParam[i] = double.class;
            }
            try {
                Method fun = Math.class.getMethod(func, clsParam);
                ret = fun.invoke(null, dbParam);
            } catch(NoSuchMethodException e) {
                Assert.fail(IllegalExpressionException.class, "function " + function + " not suppored");
            } catch(InvocationTargetException e) {
                Assert.fail(e);
            } catch(IllegalAccessException e) {
                Assert.fail(e);
            }
            return ret;
        }
    }

    private static class ArithmeticExpressionNode implements ExpressionNode {
        private ExpressionNode lvalue, rvalue;
        private char operator;

        public ArithmeticExpressionNode(ExpressionNode lvalue, ExpressionNode rvalue, char operator) {
            this.lvalue = lvalue;
            this.rvalue = rvalue;
            this.operator = operator;
        }

        public Object calucate() {
            double lv = ValueUtil.toDouble(lvalue.calucate(), -1);
            double rv = ValueUtil.toDouble(rvalue.calucate(), -1);
            switch(operator) {
                case '+':
                    return lv + rv;
                case '-':
                    return lv - rv;
                case '*':
                    return lv * rv;
                case '/':
                    return lv / rv;
                case '%':
                    return lv - ((long) (lv / rv)) * rv;
                case '>':
                    return lv > rv ? 1 : 0;
                case '<':
                    return lv < rv ? 1 : 0;
                case '|':
                    return lv > 0 || rv > 0 ? 1 : 0;
                case '&':
                    return lv > 0 && rv > 0 ? 1 : 0;
                case '=':
                    return lv == rv ? 1 : 0;
            }
            throw new IllegalExpressionException("illegal operator:" + operator);
        }
    }

    @Override
    public String toString() {
        return "eval";
    }
}
