package com.hankcs.hanlp.recognition.ns;


import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import junit.framework.TestCase;


public class PlaceRecognitionTest extends TestCase {
    public void testSeg() throws Exception {
        // HanLP.Config.enableDebug();
        DijkstraSegment segment = new DijkstraSegment();
        segment.enableJapaneseNameRecognize(false);
        segment.enableTranslatedNameRecognize(false);
        segment.enableNameRecognize(false);
        segment.enableCustomDictionary(false);
        segment.enablePlaceRecognize(true);
        // System.out.println(segment.seg("????????????????????????"));
    }
}

