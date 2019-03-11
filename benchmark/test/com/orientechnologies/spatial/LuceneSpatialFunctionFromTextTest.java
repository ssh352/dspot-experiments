/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * For more information: http://www.orientdb.com
 */
package com.orientechnologies.spatial;


import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.IOException;
import org.junit.Test;


/**
 * Created by Enrico Risa on 13/08/15.
 */
public class LuceneSpatialFunctionFromTextTest extends BaseSpatialLuceneTest {
    @Test
    public void geomFromTextLineStringTest() {
        ODocument point = lineStringDoc();
        checkFromText(point, (("select ST_GeomFromText('" + (BaseSpatialLuceneTest.LINESTRINGWKT)) + "') as geom"));
    }

    @Test
    public void geomFromTextMultiLineStringTest() {
        ODocument point = multiLineString();
        checkFromText(point, (("select ST_GeomFromText('" + (BaseSpatialLuceneTest.MULTILINESTRINGWKT)) + "') as geom"));
    }

    @Test
    public void geomFromTextPointTest() {
        ODocument point = point();
        checkFromText(point, (("select ST_GeomFromText('" + (BaseSpatialLuceneTest.POINTWKT)) + "') as geom"));
    }

    @Test
    public void geomFromTextMultiPointTest() {
        ODocument point = multiPoint();
        checkFromText(point, (("select ST_GeomFromText('" + (BaseSpatialLuceneTest.MULTIPOINTWKT)) + "') as geom"));
    }

    @Test
    public void geomFromTextPolygonTest() {
        ODocument polygon = polygon();
        checkFromText(polygon, (("select ST_GeomFromText('" + (BaseSpatialLuceneTest.POLYGONWKT)) + "') as geom"));
    }

    @Test
    public void geomFromTextMultiPolygonTest() throws IOException {
        ODocument polygon = loadMultiPolygon();
        checkFromText(polygon, (("select ST_GeomFromText('" + (BaseSpatialLuceneTest.MULTIPOLYGONWKT)) + "') as geom"));
    }

    @Test
    public void geomCollectionFromText() {
        checkFromCollectionText(geometryCollection(), (("select ST_GeomFromText('" + (BaseSpatialLuceneTest.GEOMETRYCOLLECTION)) + "') as geom"));
    }
}

