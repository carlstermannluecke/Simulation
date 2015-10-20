package velinov.java.xml.sax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * An abstract SAX Xml reader.
 * 
 * @author Vladimir Velinov
 * @since 11.05.2015
 * @param <T>
 */
public abstract class AbsXmlSaxReader<T> extends DefaultHandler {
  
  protected final XMLReader       reader;
  private   final File            xmlFile;
  private   final FileInputStream inStream;
  protected final InputSource     xmlSource;
  protected final List<T>         objects;
  
  /**
   * default constructor.
   * @param _xmlFile 
   * @throws SAXException 
   * @throws FileNotFoundException 
   */
  protected AbsXmlSaxReader(File _xmlFile) throws SAXException, 
      FileNotFoundException 
  {
    this.reader    = XMLReaderFactory.createXMLReader();
    this.xmlFile   = _xmlFile;
    this.inStream  = new FileInputStream(this.xmlFile);
    this.xmlSource = new InputSource(this.inStream);
    this.objects   = new ArrayList<T>();
    
    this.reader.setEntityResolver(new EmptyEntityResolver());
    this.reader.setContentHandler(this);
  }
  
  /**
   * @throws IOException
   * @throws SAXException
   */
  public void parseXmlDocument() throws IOException, SAXException {
    try {
      this.reader.parse(this.xmlSource);
      this.onParsingFinished();
    }
    catch (IOException _e) {
      throw _e;
    }
    catch (StopParsingException _e) {
      return;
    }
    finally {
      this.inStream.close();
    }
    
  }
  
  protected void stopParsing() throws StopParsingException {
    throw new StopParsingException();
  }
  
  @Override
  public void startElement(String _uri, String _localName, String _qName, 
      Attributes _attributes) throws SAXException 
  {
    this.onStartElementRead(_uri, _localName, _qName, _attributes);
  }
  
  @Override
  public void endElement(String _uri, String _localName, String _qName) 
      throws SAXException 
  {
    this.onEndElementRead(_uri, _localName, _qName);
  }
  
  protected abstract void onStartElementRead(String _uri, String _localName, 
      String _qName, Attributes _attributes) throws SAXException;
  
  protected abstract void onEndElementRead(String _uri, String _localName, 
      String _qName) throws SAXException;
  
  protected abstract void onParsingFinished();
  
}
