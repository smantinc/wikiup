package org.wikiup.core.impl.df;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.DocumentSaxHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

abstract class AbstractDocumentReader {
    /*
      private DocumentBuilder builder = null;
    
      private DocumentBuilder getDOMBuilder()
      {
        if(builder == null)
          builder = DomUtil.getDOMBuilder();
        return builder;
      }
    
      protected Document parse(InputSource source)
      {
        DocumentBuilder docBuilder = getDOMBuilder();
        Document doc = null;
        try
        {
          doc = DomUtil.duplicate(docBuilder.parse(source));
        }
        catch (IOException ex)
        {
          Assert.fail(ex);
        }
        catch (SAXException ex)
        {
          Assert.fail(ex);
        }
        return doc;
      }
    /*/
    protected Document parse(InputSource source) {
        DocumentSaxHandler handler = new DocumentSaxHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(source, handler);
        } catch(ParserConfigurationException ex) {
            Assert.fail(ex);
        } catch(SAXException ex) {
            Assert.fail(ex);
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        return handler.getDocument();
    }
/**/
}
