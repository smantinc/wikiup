package org.wikiup.modules.mysql.driver;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.inf.DatabaseDriver;
import org.wikiup.database.orm.inf.SQLDialectInf;
import org.wikiup.modules.mysql.dialect.MySQLDialect;

public class MySQLDatabaseDriver implements DatabaseDriver, DocumentAware
{
  private String host;
  private String schema;
  private String url;

  private int port;

  public String getHost()
  {
    return host;
  }

  public void setHost(String host)
  {
    this.host = host;
  }

  public String getSchema()
  {
    return schema;
  }

  public void setSchema(String schema)
  {
    this.schema = schema;
  }

  public void setConnectionURL(String url)
  {
    this.url = url;
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public Class getDriverClass()
  {
    return Interfaces.getClass("com.mysql.jdbc.Driver");
  }

  public SQLDialectInf getDialect()
  {
    return new MySQLDialect();
  }

  public String getConnectionURL()
  {
    return url != null ? url : "jdbc:mysql://" + host + ":" + (port == 0 ? 3306 : port) + "/" + schema +
        "?useUnicode=true&characterEncoding=utf-8";
  }

  public void aware(Document desc)
  {
    host = Documents.getDocumentValue(desc, "host", null);
    schema = Documents.getDocumentValue(desc, "schema", null);
    port = Documents.getDocumentInteger(desc, "port", 0);
    url = Documents.getDocumentValue(desc, "url", null);
  }
}

