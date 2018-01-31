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
package eu.atos.sla.common.collections;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TransformingListTest {
    List<String> source = Arrays.asList("1", "2", "3", "4");
    TransformingList.Adapter<String, Integer> adapter = new TransformingList.Adapter<String, Integer>() {
        @Override
        public Integer apply(String from) {
            System.out.println("Converting " + from);
            return Integer.valueOf(from);
        }
    };
    TransformingList<String, Integer> target = new TransformingList<String, Integer>(source, adapter);

    @Test
    public void testSize() {
        assertEquals(4, target.size());
    }

    @Test
    public void testGetInt() {
        int c = 1;
        for (int i : target) {
            assertEquals(c++, i);
        }
    }
}
