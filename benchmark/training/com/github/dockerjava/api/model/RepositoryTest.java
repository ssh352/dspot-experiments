package com.github.dockerjava.api.model;


import org.junit.Assert;
import org.junit.Test;


public class RepositoryTest {
    @Test
    public void testRepository() throws Exception {
        Repository repo = new Repository("10.0.0.1/jim");
        Repository repo1 = new Repository("10.0.0.1:1234/jim");
        Repository repo2 = new Repository("busybox");
        Assert.assertEquals("jim", repo.getPath());
        Assert.assertEquals("jim", repo1.getPath());
        Assert.assertEquals("busybox", repo2.getPath());
        Assert.assertEquals(1234, repo1.getURL().getPort());
    }
}

