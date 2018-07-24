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

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

public class DurationUtils {

    private static final DatatypeFactory dtFactory; 
    public static final Duration EMPTY_DURATION;
    public static final Date ZERO_DATE = new Date(0L);
    
    static {
        try {
            dtFactory = DatatypeFactory.newInstance();
            EMPTY_DURATION = newDuration(0);
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    
    public static Duration newDuration(String lexicalRepresentation) {
        return dtFactory.newDuration(lexicalRepresentation);
    }
    
    public static Duration newDuration(long durationInMilliSeconds) {
        return dtFactory.newDuration(durationInMilliSeconds);
    }

    public static Date durationToDate(Duration duration) {
        long ms = duration.getTimeInMillis(ZERO_DATE);
        return new Date(ms);
    }
    

}
