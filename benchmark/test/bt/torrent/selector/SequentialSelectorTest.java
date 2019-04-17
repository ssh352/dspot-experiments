/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bt.torrent.selector;


import bt.test.torrent.selector.UpdatablePieceStatistics;
import org.junit.Assert;
import org.junit.Test;


public class SequentialSelectorTest {
    @Test
    public void testSelector() {
        UpdatablePieceStatistics statistics = new UpdatablePieceStatistics(8);
        statistics.setPiecesCount(0, 0, 0, 0, 0, 0, 0, 0);
        Assert.assertEquals(0, SequentialSelectorTest.collect(SequentialSelector.sequential().getNextPieces(statistics)).length);
        statistics.setPiecesCount(2, 0, 0, 0, 1, 0, 0, 0);
        Assert.assertArrayEquals(new Integer[]{ 0, 4 }, SequentialSelectorTest.collect(SequentialSelector.sequential().getNextPieces(statistics)));
        statistics.setPieceCount(1, 1);
        Assert.assertArrayEquals(new Integer[]{ 0, 1, 4 }, SequentialSelectorTest.collect(SequentialSelector.sequential().getNextPieces(statistics)));
    }
}
