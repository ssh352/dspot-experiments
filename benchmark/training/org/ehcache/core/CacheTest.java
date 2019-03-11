/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.core;


import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import org.ehcache.StateTransitionException;
import org.ehcache.Status;
import org.ehcache.core.spi.LifeCycled;
import org.ehcache.core.spi.store.Store;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class CacheTest {
    @Test
    public void testTransistionsState() {
        Store<Object, Object> store = Mockito.mock(Store.class);
        InternalCache ehcache = getCache(store);
        Assert.assertThat(ehcache.getStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        ehcache.init();
        Assert.assertThat(ehcache.getStatus(), CoreMatchers.is(Status.AVAILABLE));
        ehcache.close();
        Assert.assertThat(ehcache.getStatus(), CoreMatchers.is(Status.UNINITIALIZED));
    }

    @Test
    public void testThrowsWhenNotAvailable() throws StoreAccessException {
        Store<Object, Object> store = Mockito.mock(Store.class);
        Store.Iterator mockIterator = Mockito.mock(Store.Iterator.class);
        Mockito.when(store.iterator()).thenReturn(mockIterator);
        InternalCache<Object, Object> ehcache = getCache(store);
        try {
            ehcache.get("foo");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.put("foo", "bar");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.remove("foo");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.remove("foo", "bar");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.containsKey("foo");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.replace("foo", "bar");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.replace("foo", "foo", "bar");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.putIfAbsent("foo", "bar");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.clear();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.iterator();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.getAll(Collections.singleton("foo"));
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.removeAll(Collections.singleton("foo"));
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            ehcache.putAll(Collections.singletonMap("foo", "bar"));
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        ehcache.init();
        final Iterator iterator = ehcache.iterator();
        ehcache.close();
        try {
            iterator.hasNext();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            iterator.next();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
        try {
            iterator.remove();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), CoreMatchers.is(true));
        }
    }

    @Test
    public void testDelegatesLifecycleCallsToStore() throws Exception {
        InternalCache ehcache = getCache(Mockito.mock(Store.class));
        final LifeCycled mock = Mockito.mock(LifeCycled.class);
        ehcache.addHook(mock);
        ehcache.init();
        Mockito.verify(mock).init();
        ehcache.close();
        Mockito.verify(mock).close();
    }

    @Test
    public void testFailingTransitionGoesToLowestStatus() throws Exception {
        final LifeCycled mock = Mockito.mock(LifeCycled.class);
        InternalCache ehcache = getCache(Mockito.mock(Store.class));
        Mockito.doThrow(new Exception()).when(mock).init();
        ehcache.addHook(mock);
        try {
            ehcache.init();
            Assert.fail();
        } catch (StateTransitionException e) {
            Assert.assertThat(ehcache.getStatus(), CoreMatchers.is(Status.UNINITIALIZED));
        }
        Mockito.reset(mock);
        ehcache.init();
        Assert.assertThat(ehcache.getStatus(), CoreMatchers.is(Status.AVAILABLE));
        ehcache.close();
    }

    @Test
    public void testPutIfAbsent() throws StoreAccessException {
        final AtomicReference<Object> existingValue = new AtomicReference<>();
        final Store store = Mockito.mock(Store.class);
        final String value = "bar";
        Mockito.when(store.computeIfAbsent(ArgumentMatchers.eq("foo"), ArgumentMatchers.any(Function.class))).thenAnswer(( invocationOnMock) -> {
            final Function<Object, Object> biFunction = ((Function<Object, Object>) (invocationOnMock.getArguments()[1]));
            if ((existingValue.get()) == null) {
                final Object newValue = biFunction.apply(invocationOnMock.getArguments()[0]);
                existingValue.compareAndSet(null, newValue);
            }
            return new Store.ValueHolder<Object>() {
                @Override
                public Object get() {
                    return existingValue.get();
                }

                @Override
                public long creationTime() {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public long expirationTime() {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public boolean isExpired(long expirationTime) {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public long lastAccessTime() {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public long getId() {
                    throw new UnsupportedOperationException("Implement me!");
                }
            };
        });
        Mockito.when(store.putIfAbsent(ArgumentMatchers.eq("foo"), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Consumer.class))).then(( invocation) -> {
            final Object toReturn;
            if ((toReturn = existingValue.get()) == null) {
                existingValue.compareAndSet(null, invocation.getArguments()[1]);
            }
            return new Store.ValueHolder<Object>() {
                @Override
                public Object get() {
                    return toReturn;
                }

                @Override
                public long creationTime() {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public long expirationTime() {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public boolean isExpired(long expirationTime) {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public long lastAccessTime() {
                    throw new UnsupportedOperationException("Implement me!");
                }

                @Override
                public long getId() {
                    throw new UnsupportedOperationException("Implement me!");
                }
            };
        });
        InternalCache<Object, Object> ehcache = getCache(store);
        ehcache.init();
        Assert.assertThat(ehcache.putIfAbsent("foo", value), CoreMatchers.nullValue());
        Assert.assertThat(ehcache.putIfAbsent("foo", "foo"), CoreMatchers.is(value));
        Assert.assertThat(ehcache.putIfAbsent("foo", "foobar"), CoreMatchers.is(value));
        Assert.assertThat(ehcache.putIfAbsent("foo", value), CoreMatchers.is(value));
    }

    @Test
    public void testInvokesHooks() {
        Store store = Mockito.mock(Store.class);
        InternalCache ehcache = getCache(store);
        final LifeCycled hook = Mockito.mock(LifeCycled.class);
        ehcache.addHook(hook);
        ehcache.init();
        try {
            Mockito.verify(hook).init();
        } catch (Exception e) {
            Assert.fail();
        }
        Mockito.reset(hook);
        try {
            if (ehcache instanceof Ehcache) {
                ((Ehcache) (ehcache)).removeHook(hook);
            } else {
                ((Ehcache) (ehcache)).removeHook(hook);
            }
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
        ehcache.close();
        try {
            Mockito.verify(hook).close();
        } catch (Exception e) {
            Assert.fail();
        }
    }
}

