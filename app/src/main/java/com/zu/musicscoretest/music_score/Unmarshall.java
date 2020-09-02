package com.zu.musicscoretest.music_score;


import com.zu.musicxml3_0.ScorePartwise;
import com.zu.musicxml3_0.ScoreTimewise;
import com.zu.musicxml3_0.opus.Opus;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.StreamReaderDelegate;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unmarshall {


    /** The official URI for XLink namespace. */
    private static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";
    /** The collection of all supported attributes with xlink: prefix. */
    private static final List<String> XLINK_ATTRIBUTES = Arrays.asList(
            "href",
            "type",
            "role",
            "title",
            "show",
            "actuate");

    private static final Map<Class, JAXBContext> jaxbContextMap = new HashMap<>();

    private static JAXBContext getContext(Class clazz) throws JAXBException
    {
        JAXBContext context = jaxbContextMap.get(clazz);
        if(context == null)
        {
            synchronized (jaxbContextMap)
            {
                context = jaxbContextMap.get(clazz);
                if(context == null)
                {
                    context = JAXBContext.newInstance(clazz);
                    jaxbContextMap.put(clazz, context);
                    return context;
                }
            }
        }
        return context;
    }

    public static Object unmarshall(InputStream is) throws UnmarshallException
    {
        try{
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

            MyXmlStreamReader xmlReader = new MyXmlStreamReader(inputFactory.createXMLStreamReader(is));

            XMLEventReader eventReader = inputFactory.createXMLEventReader(xmlReader);

            while(eventReader.hasNext())
            {
                XMLEvent event = eventReader.peek();
                if(event.isStartElement())
                {
                    StartElement rootStart = event.asStartElement();
                    QName qName = rootStart.getName();
                    String name = qName.getLocalPart();
                    if("opus".equals(name))
                    {
                        Unmarshaller um = getContext(Opus.class).createUnmarshaller();
                        return um.unmarshal(eventReader, Opus.class).getValue();
                    }
                    else if("score-partwise".equals(name))
                    {
                        Unmarshaller um = getContext(ScorePartwise.class).createUnmarshaller();
                        return um.unmarshal(eventReader, ScorePartwise.class).getValue();
                    }
                    else if("score-timewise".equals(name))
                    {
                        Unmarshaller um = getContext(ScoreTimewise.class).createUnmarshaller();
                        return um.unmarshal(eventReader, ScoreTimewise.class).getValue();
                    }
                    else
                    {
                        eventReader.next();
                    }
                }
                else
                {
                    eventReader.next();
                }
            }
            return null;

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new UnmarshallException(e);
        }
    }

    private static class MyXmlStreamReader extends StreamReaderDelegate
    {

        public MyXmlStreamReader(XMLStreamReader reader)
        {
            super(reader);
        }

        @Override
        public QName getAttributeName(int index) {
            String prefix = getAttributePrefix(index);
            if("xlink".equals(prefix))
            {
                QName qName = super.getAttributeName(index);
                String local = qName.getLocalPart();

                if (XLINK_ATTRIBUTES.contains(local)) {
                    return new QName(XLINK_NAMESPACE_URI, local, "xlink");
                }
            }
            return super.getAttributeName(index);
        }
    }

    public static class UnmarshallException extends Exception{
        public UnmarshallException(Throwable cause)
        {
            super(cause);
        }
    }
}
