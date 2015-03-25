package org.wikiup.core.impl.el;

import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.core.exception.MalformedTemplateException;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateExpressionLanguage implements ExpressionLanguage<Dictionary<?>, Object>, DocumentAware {
    final static public char[] SELECTORS = {'!', '+', '?', '@'};

    private char token;
    private char openBracket;
    private char closeBracket;
    private Pattern variablePattern;

    public TemplateExpressionLanguage() {
        init(WikiupConfigure.VARIABLE_TOKEN, WikiupConfigure.OPEN_BRACKET, WikiupConfigure.CLOSE_BRACKET);
    }

    private void init(char token, char openBracket, char closeBracket) {
        this.token = token;
        this.openBracket = openBracket;
        this.closeBracket = closeBracket;
        variablePattern = getVariableNamePattern();
    }

    public Object evaluate(Dictionary<?> context, String script) {
        return isVariableToken(script) ? getVariable(replaceValue(trimVariableName(script, true), context), context) : replaceValue(script, context);
    }

    private Object getVariable(String str, Dictionary<?> dictionary) {
        TokenTemplate token = new TokenTemplate(str, 0);
        return token.selector != 0 ? getTokenObject(token, dictionary) : dictionary.get(str);
    }

    private String replaceValue(String str, Dictionary<?> dictionary) {
        if(str != null) {
            StringBuilder buf = new StringBuilder();
            int headPos = 0, tailPos = 0;
            while(headPos != -1) {
                headPos = str.indexOf(token, headPos);
                if(headPos != -1) {
                    if((headPos > 0 && str.charAt(headPos - 1) == '\\') || headPos == str.length() - 1) {
                        buf.append(str.substring(tailPos, headPos - (headPos == str.length() - 1 ? 0 : 1)));
                        buf.append(token);
                        headPos = tailPos = headPos + 1;
                    } else {
                        TokenTemplate token = new TokenTemplate(str, headPos + 1);
                        buf.append(str.substring(tailPos, headPos));
                        headPos = tailPos = headPos + token.length + 1;
                        buf.append(ValueUtil.toString(getTokenObject(token, dictionary), ""));
                    }
                }
            }
            buf.append(str.substring(tailPos));
            return buf.toString();
        }
        return null;
    }

    private Object getTokenObject(TokenTemplate token, Dictionary<?> dictionary) {
        switch(token.selector) {
            case 0:
                return dictionary.get(replaceValue(token.getToken(0), dictionary));
            case '!':
                try {
                    return dictionary.get(replaceValue(token.getToken(0), dictionary));
                } catch(Exception ex) {
                }
                return replaceValue(token.getToken(1, null), dictionary);
            case '+':
                try {
                    Object obj = dictionary.get(replaceValue(token.getToken(0), dictionary));
                    if(obj != null && StringUtil.isNotEmpty(obj.toString()))
                        return obj;
                } catch(Exception ex1) {
                }
                return replaceValue(token.getToken(1, null), dictionary);
            case '?':
                try {
                    String str = ValueUtil.toString(dictionary.get(replaceValue(token.getToken(0), dictionary)), null);
                    if(StringUtil.isNotEmpty(str) && ValueUtil.toBoolean(str, true) && ValueUtil.toInteger(str, -1) != 0)
                        return replaceValue(token.getToken(1), dictionary);
                } catch(Exception ex1) {
                }
                return replaceValue(token.getToken(2, null), dictionary);
            case '@':
                try {
                    Object obj = dictionary.get(replaceValue(token.getToken(0), dictionary));
                    int i;
                    if(obj != null)
                        for(i = 1; i < token.tokens.length; i += 2)
                            if(obj.toString().equals(replaceValue(token.getToken(i), dictionary)))
                                return replaceValue(token.getToken(i + 1), dictionary);
                } catch(Exception ex1) {
                }
                return replaceValue(token.getToken(token.tokens.length - 1), dictionary);
        }
        return null;
    }

    private Pattern getVariableNamePattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("([").append(WikiupConfigure.CHARACTER_PATTERN).append('\\').append(closeBracket);
        pattern.append("]|(\\").append(token).append('\\').append(openBracket).append("))+");
        return Pattern.compile(pattern.toString());
    }

    public void aware(Document desc) {
        String vp = Documents.getDocumentValue(desc, "variable-pattern", null);
        if(vp != null && vp.length() > 2)
            init(vp.charAt(0), vp.charAt(1), vp.charAt(2));
    }

    private class TokenTemplate {
        public char selector = 0;
        public String[] tokens;
        public int length;

        private TokenTemplate(String str, int offset) {
            parseTemplate(str, offset);
        }

        private String getToken(int index) {
            return getToken(index, "");
        }

        private String getToken(int index, String def) {
            return index < tokens.length ? tokens[index] : def;
        }

        private void parseTemplate(String str, int offset) {
            int start = offset;
            char c = str.charAt(start);
            if(isSelector(c)) {
                selector = c;
                start++;
            }
            parseTokens(str, offset, start);
        }

        private boolean isSelector(char c) {
            for(char s : SELECTORS)
                if(s == c)
                    return true;
            return false;
        }

        private void parseTokens(String str, int offset, int start) {
            List<String> list = new ArrayList<String>();
            char buf[] = str.toCharArray();
            int pos = start, len = buf.length;
            do {
                if(list.isEmpty()) {
                    String name = getVariableToken(str, pos);
                    pos += name.length();
                    list.add(trimVariableName(name.charAt(0) == selector ? name.substring(1) : name, false));
                } else {
                    int end = findVariableEndTokenPositionByBracket(str, pos);
                    list.add(trimVariableName(str.substring(pos, end + 1), false));
                    pos = end + 1;
                }
            } while(pos < len && buf[pos] == openBracket);
            tokens = list.toArray(new String[list.size()]);
            length = pos - offset;
        }
    }

    private String trimVariableName(String str, boolean isStartedWithVarToken) {
        int len = str.length();
        if(len > (isStartedWithVarToken ? 2 : 1)) {
            char c = str.charAt(0);
            if(c == token && isStartedWithVarToken)
                return trimVariableName(str.substring(1), false);
            return c == openBracket &&
                    str.charAt(len - 1) == closeBracket ? str.substring(1, len - 1) : str;
        }
        return str;
    }

    private int findVariableEndTokenPositionByBracket(String str, int start) {
        int depth = 0;
        int pos = start, headPos, tailPos;
        do {
            headPos = str.indexOf(openBracket, pos);
            tailPos = str.indexOf(closeBracket, pos);
            Assert.isTrue(tailPos != -1, MalformedTemplateException.class);
            depth += (headPos < tailPos && headPos != -1) ? 1 : -1;
            pos = (headPos < tailPos && headPos != -1) ? headPos + 1 : tailPos + 1;
        } while(depth != 0);
        return tailPos;
    }

    private int findVariableEndTokenPositionByPattern(String str, int start) {
        Matcher matcher = variablePattern.matcher(str.substring(start));
        return matcher.lookingAt() ? start + matcher.end() - 1 : start;
    }

    private String getVariableToken(String str, int start) {
        int pos = str.charAt(start) == openBracket ? findVariableEndTokenPositionByBracket(str, start)
                : findVariableEndTokenPositionByPattern(str, start);
        return str.substring(start, pos + 1);
    }

    private boolean isVariableToken(String str) {
        if(str.length() > 0 && str.charAt(0) == token) {
            int pos = (str.length() > 3 && str.substring(1, 3).indexOf(openBracket) != -1) ?
                    findVariableEndTokenPositionByBracket(str, 1) : findVariableEndTokenPositionByPattern(str, 1);
            return pos == str.length() - 1;
        }
        return false;
    }

    @Override
    public String toString() {
        return "template";
    }
}
