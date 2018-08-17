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
package eu.atos.sla.parser;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DateTimeAdapterTest {
    DateTimeAdapter adapter = new DateTimeAdapter();

    @Before
    public void initMethod() {
        /*
         * resets the adapter
         */
        DateTimeAdapter.setMarshallTimezone("UTC");
        DateTimeAdapter.setUnmarshallTimezone("UTC");
    }
    
    @Test
    public void testUnmarshalString() throws Exception {
        Date d;
        d = adapter.unmarshal("1970-01-01T00:00:00Z");
        assertEquals(0, d.getTime());
        d = adapter.unmarshal("1970-01-01T01:00:00+01:00");
        assertEquals(0, d.getTime());
        d = adapter.unmarshal("1970-01-01T00:00:00+00:00");
        assertEquals(0, d.getTime());
    }
    
    @Test
    public void testUnmarshalShouldFail() throws Exception {
        checkUnmarshalFails("1970-01-01T01:00:00+0100");
        checkUnmarshalFails("1970-01-01T01:00:00");
    }

    @Test
    public void testMarshalDate() throws Exception {
    
        checkMarshal(new Date(0), "1970-01-01T00:00:00Z");
        checkMarshal(new Date(3600 * 1000), "1970-01-01T01:00:00Z");
        
    }

    @Test
    public void testMarshalDateWithTimeZone() throws Exception {
    
        DateTimeAdapter.setMarshallTimezone("CET");
        checkMarshal(new Date(0), "1970-01-01T01:00:00+01:00");
        checkMarshal(new Date(3600 * 1000), "1970-01-01T02:00:00+01:00");
    }
    
    public void testMarshalUnmarshalWithTimeZone() throws Exception {
        
        DateTimeAdapter.setMarshallTimezone("CET");
        
        String s = "1970-01-01T00:00:00Z";
        Date date = adapter.unmarshal(s);
        assertEquals(0, date.getTime());
        
        String actual = adapter.marshal(date);
        assertEquals("1970-01-01T01:00:00+01:00", actual);
    }

    private void checkMarshal(Date date, String expected) throws Exception {
        String actual = adapter.marshal(date);
        System.out.println(date + " " + actual);
        assertEquals(expected, actual);
    }
    
    private void checkUnmarshalFails(String s) throws Exception {
        
        try {
            adapter.unmarshal(s);
            fail("Unmarshalling of " + s + " should fail");
        } catch (IllegalArgumentException e) {
            /* does nothing */
        }
    }
}
