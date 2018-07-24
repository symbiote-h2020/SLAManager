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

import java.util.AbstractList;
import java.util.List;

/**
 * Unmodifiable List wrapper over 2 lists. 
 * 
 * There is no such wrapper in jdk. 
 * @see http://stackoverflow.com/a/13868352
 */
public class CompositeList<E> extends AbstractList<E> {

    private final List<E> list1;
    private final List<E> list2;

    public CompositeList(List<E> list1, List<E> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    @Override
    public E get(int index) {
        if (index < list1.size()) {
            return list1.get(index);
        }
        return list2.get(index-list1.size());
    }

    @Override
    public int size() {
        return list1.size() + list2.size();
    }
}