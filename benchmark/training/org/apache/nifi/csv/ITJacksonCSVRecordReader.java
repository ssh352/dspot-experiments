/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.csv;


import CSVFormat.DEFAULT;
import RecordFieldType.DATE;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ITJacksonCSVRecordReader {
    private final CSVFormat format = DEFAULT.withFirstRecordAsHeader().withTrim().withQuote('"');

    @Test
    public void testParserPerformance() throws IOException, MalformedRecordException {
        // Generates about 130MB of data
        final int NUM_LINES = 2500000;
        StringBuilder sb = new StringBuilder("id,name,balance,address,city,state,zipCode,country\n");
        for (int i = 0; i < NUM_LINES; i++) {
            sb.append("1,John Doe,4750.89D,123 My Street,My City,MS,11111,USA\n");
        }
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream bais = new ByteArrayInputStream(sb.toString().getBytes());final JacksonCSVRecordReader reader = new JacksonCSVRecordReader(bais, Mockito.mock(ComponentLog.class), schema, format, true, false, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), "UTF-8")) {
            Record record;
            int numRecords = 0;
            while ((record = reader.nextRecord()) != null) {
                Assert.assertNotNull(record);
                numRecords++;
            } 
            Assert.assertEquals(NUM_LINES, numRecords);
        }
    }
}

