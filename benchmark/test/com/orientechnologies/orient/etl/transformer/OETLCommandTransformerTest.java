/**
 * * Copyright 2010-2016 OrientDB LTD (info(-at-)orientdb.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.orientechnologies.orient.etl.transformer;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.etl.OETLBaseTest;
import org.junit.Test;


/**
 * Created by frank on 21/09/2016.
 */
public class OETLCommandTransformerTest extends OETLBaseTest {
    @Test
    public void shouldAllowSingleQuoteInsideFieldValue() {
        // Data contains a field with single quote: Miner O'Greedy
        // so the query must be enclosed inside double quote "
        configure(((((((((((("{\n" + (((((((((((((((((((((((("  \'config\': {\n" + "    \'log\': \'INFO\'\n") + "  },\n") + "  \'source\': {\n") + "    \'content\': {\n") + "      \'value\': \"name,surname\n Jay, Miner O\'Greedy \n Jay, Miner O\'Greedy   \"\n") + "    }\n") + "  },\n") + "  \'transformers\': [\n") + "    {\n") + "      \'command\': {\n") + "       \'log\': \'INFO\',\n") + "       \'output\': \'previous\',\n") + "       \'language\': \'sql\',\n") + "        \'command\': \"SELECT name FROM Person WHERE surname= \"={eval(\'$input.surname\')}\"\"\n") + "      }\n") + "    },\n") + "  {vertex: {class:'Person', skipDuplicates:false}} ") + "],") + "  \'extractor\': {\n") + "    \'csv\': {}\n") + "  },\n") + "  \'loader\': {\n") + "      \'orientdb\': {\n") + // + "       'log': 'DEBUG',\n"
        "        'dbURL': 'memory:")) + (name.getMethodName())) + "\',\n") + "        \'dbType\': \'graph\',\n") + "        \'useLightweightEdges\': false ,\n") + "         \"classes\": [\n") + "        {\"name\":\"Person\", \"extends\": \"V\" }") + "      ]") + "      }\n") + "    }\n") + "}"));
        proc.execute();
        ODatabaseDocument db = proc.getLoader().getPool().acquire();
        assertThat(db.countClass("Person")).isEqualTo(2);
    }
}

