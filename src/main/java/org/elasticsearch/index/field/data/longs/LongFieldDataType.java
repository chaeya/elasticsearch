/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.field.data.longs;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.SortField;
import org.elasticsearch.index.cache.field.data.FieldDataCache;
import org.elasticsearch.index.field.data.FieldDataType;

import java.io.IOException;

/**
 *
 */
public class LongFieldDataType implements FieldDataType<LongFieldData> {

    @Override
    public ExtendedFieldComparatorSource newFieldComparatorSource(final FieldDataCache cache, final String missing) {
        if (missing == null) {
            return new ExtendedFieldComparatorSource() {
                @Override
                public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
                    return new LongFieldDataComparator(numHits, fieldname, cache);
                }

                @Override
                public int reducedType() {
                    return SortField.LONG;
                }
            };
        }
        if (missing.equals("_last")) {
            return new ExtendedFieldComparatorSource() {
                @Override
                public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
                    return new LongFieldDataMissingComparator(numHits, fieldname, cache, reversed ? Long.MIN_VALUE : Long.MAX_VALUE);
                }

                @Override
                public int reducedType() {
                    return SortField.LONG;
                }
            };
        }
        if (missing.equals("_first")) {
            return new ExtendedFieldComparatorSource() {
                @Override
                public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
                    return new LongFieldDataMissingComparator(numHits, fieldname, cache, reversed ? Long.MAX_VALUE : Long.MIN_VALUE);
                }

                @Override
                public int reducedType() {
                    return SortField.LONG;
                }
            };
        }
        return new ExtendedFieldComparatorSource() {
            @Override
            public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
                return new LongFieldDataMissingComparator(numHits, fieldname, cache, Long.parseLong(missing));
            }

            @Override
            public int reducedType() {
                return SortField.LONG;
            }
        };
    }

    @Override
    public LongFieldData load(IndexReader reader, String fieldName) throws IOException {
        return LongFieldData.load(reader, fieldName);
    }
}
