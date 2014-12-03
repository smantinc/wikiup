package org.wikiup.modules.oracle.dialect;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.inf.DialectInterpretActionInf;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.database.orm.util.SQLWrapper;

import java.util.List;

public class OffsetPhraseInterpretAction implements DialectInterpretActionInf {
    public void doAction(SQLStatement stmt, Document param) {
        List<SQLWrapper> wrappers = stmt.getWrappers();
        String offset = Documents.getDocumentValue(param, "offset", "0");
        for(SQLWrapper wrapper : wrappers)
            if(wrapper.getSuffix().startsWith(LimitPhraseInterpretAction.LIMIT_SUFFIX)) {
                String limit = wrapper.getSuffix().substring(LimitPhraseInterpretAction.LIMIT_SUFFIX.length());
                StringBuffer buf = new StringBuffer(" ) row_ where rownum <= ");
                wrapper.setPrefix("SELECT * from ( SELECT row_.*, rownum rownum_ FROM ( ");
                if(ValueUtil.isInteger(limit) && ValueUtil.isInteger(offset))
                    buf.append(ValueUtil.toInteger(limit, 0) + ValueUtil.toInteger(offset, 0));
                else
                    buf.append(StringUtil.format("({0} + {1})", offset, limit));
                buf.append(") WHERE rownum_ > ").append(offset);
                wrapper.setSuffix(buf.toString());
            }
    }
}