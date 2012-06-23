////////////////////////////////////////////////////////////////////////////////
//
//  MATTBOLT.BLOGSPOT.COM
//  Copyright(C) 2012 Matt Bolt
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at:
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package bolt.web.coffee.dependency;

import java.util.Collections;
import java.util.List;

/**
 * This class tracks a single {@code T} instance with a {@code List} of {@link DependencyReference} instances that
 * represent the outgoing edges for the {@code T} instance in the dependency graph.
 *
 * @author Matt Bolt
 */
public class OutgoingReferences<T extends NamedDependency> {

    private final T item;
    private final List<DependencyReference<T>> outgoing;

    public OutgoingReferences(T item, List<DependencyReference<T>> outgoing) {
        this.item = item;
        this.outgoing = outgoing;
    }

    public T getItem() {
        return item;
    }

    public List<DependencyReference<T>> getOutgoing() {
        return Collections.unmodifiableList(outgoing);
    }
}
