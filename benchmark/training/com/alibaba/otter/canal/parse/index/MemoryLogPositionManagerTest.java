package com.alibaba.otter.canal.parse.index;


import org.junit.Test;


public class MemoryLogPositionManagerTest extends AbstractLogPositionManagerTest {
    @Test
    public void testAll() {
        MemoryLogPositionManager logPositionManager = new MemoryLogPositionManager();
        logPositionManager.start();
        doTest(logPositionManager);
        logPositionManager.stop();
    }
}

