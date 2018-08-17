/**
 * Copyright 2017 Atos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.sla;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlParser<T> {
    private static String CHARSET = "utf-8";

    private JAXBContext context;
    private Unmarshaller unmarshaller;
    private Marshaller marshaller;
    
    public XmlParser(Class<T> class_) throws JAXBException {
        context = JAXBContext.newInstance(class_);
    }
    
    public XmlParser(Class<?>...classes) throws JAXBException {
        context = JAXBContext.newInstance(classes);
    }
    
    public XmlParser(String... packages) throws JAXBException {
        String contextPath = Utils.join(":", packages);
        context = JAXBContext.newInstance(contextPath);
    }
    
    public XmlParser(JAXBContext context) {
        this.context = context;
    }
    
    public T deserialize(InputStream is) throws JAXBException {
        Reader reader = new InputStreamReader(is, Charset.forName(CHARSET));

        T result = deserialize(reader);
        try {
            reader.close();
        } catch (IOException e) {
            /*
             * Unlikely to happen
             */
            throw new JAXBException(e.getMessage(), e);
        }
        return result;
    }
    
    public T deserialize(Reader reader) throws JAXBException {
        if (unmarshaller == null) {
            unmarshaller = context.createUnmarshaller();
        }
        
        @SuppressWarnings("unchecked")
        T result = (T) unmarshaller.unmarshal(reader);
        
        return result;
    }

    public void serialize(T t, OutputStream os) throws JAXBException {
        if (marshaller == null) {
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            /*
             * http://stackoverflow.com/a/22756191
             */
            marshaller.setProperty(Marshaller.JAXB_ENCODING, CHARSET);
        }
        
        marshaller.marshal(t, os);
    }
    
    public String toString(T t) throws JAXBException {
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        serialize(t, os);
        try {
            return os.toString(CHARSET);
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException(CHARSET + " is not supported");
        }
    }
    
    public static String removeXmlHeader(String serialized) {
        
        return serialized.replaceFirst("<?[^>]*?>", "");
    }
}
