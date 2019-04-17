/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests;


import EntryPointId.DEFAULT;
import KieSessionTestConfiguration.STATEFUL_PSEUDO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests related to the stream support features
 */
@RunWith(Parameterized.class)
public class StreamsTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public StreamsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test(timeout = 10000)
    public void testEventAssertion() {
        final String drl = ((((((((((((((("package org.drools.compiler\n" + ("\n" + "import ")) + (StockTick.class.getCanonicalName())) + ";\n") + "\n") + "global java.util.List results;\n") + "\n") + "declare StockTick\n") + "    @role( event )\n") + "end\n") + "\n") + "rule \"Test entry point\"\n") + "when\n") + "    $st : StockTick( company == \"ACME\", price > 10 ) from entry-point StockStream\n") + "then\n") + "    results.add( $st );\n") + "end";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession(STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final List results = new ArrayList();
            session.setGlobal("results", results);
            final StockTick tick1 = new StockTick(1, "DROO", 50, System.currentTimeMillis());
            final StockTick tick2 = new StockTick(2, "ACME", 10, System.currentTimeMillis());
            final StockTick tick3 = new StockTick(3, "ACME", 10, System.currentTimeMillis());
            final StockTick tick4 = new StockTick(4, "DROO", 50, System.currentTimeMillis());
            final InternalFactHandle handle1 = ((InternalFactHandle) (session.insert(tick1)));
            final InternalFactHandle handle2 = ((InternalFactHandle) (session.insert(tick2)));
            final InternalFactHandle handle3 = ((InternalFactHandle) (session.insert(tick3)));
            final InternalFactHandle handle4 = ((InternalFactHandle) (session.insert(tick4)));
            Assert.assertNotNull(handle1);
            Assert.assertNotNull(handle2);
            Assert.assertNotNull(handle3);
            Assert.assertNotNull(handle4);
            Assert.assertTrue(handle1.isEvent());
            Assert.assertTrue(handle2.isEvent());
            Assert.assertTrue(handle3.isEvent());
            Assert.assertTrue(handle4.isEvent());
            session.fireAllRules();
            Assert.assertEquals(0, results.size());
            final StockTick tick5 = new StockTick(5, "DROO", 50, System.currentTimeMillis());
            final StockTick tick6 = new StockTick(6, "ACME", 10, System.currentTimeMillis());
            final StockTick tick7 = new StockTick(7, "ACME", 15, System.currentTimeMillis());
            final StockTick tick8 = new StockTick(8, "DROO", 50, System.currentTimeMillis());
            final EntryPoint entry = session.getEntryPoint("StockStream");
            final InternalFactHandle handle5 = ((InternalFactHandle) (entry.insert(tick5)));
            final InternalFactHandle handle6 = ((InternalFactHandle) (entry.insert(tick6)));
            final InternalFactHandle handle7 = ((InternalFactHandle) (entry.insert(tick7)));
            final InternalFactHandle handle8 = ((InternalFactHandle) (entry.insert(tick8)));
            Assert.assertNotNull(handle5);
            Assert.assertNotNull(handle6);
            Assert.assertNotNull(handle7);
            Assert.assertNotNull(handle8);
            Assert.assertTrue(handle5.isEvent());
            Assert.assertTrue(handle6.isEvent());
            Assert.assertTrue(handle7.isEvent());
            Assert.assertTrue(handle8.isEvent());
            session.fireAllRules();
            Assert.assertEquals(1, results.size());
            Assert.assertSame(tick7, results.get(0));
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testEntryPointReference() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("stream-test", kieBaseTestConfiguration, "org/drools/compiler/integrationtests/test_EntryPointReference.drl");
        final KieSession session = kbase.newKieSession();
        try {
            final List<StockTick> results = new ArrayList<>();
            session.setGlobal("results", results);
            final StockTick tick5 = new StockTick(5, "DROO", 50, System.currentTimeMillis());
            final StockTick tick6 = new StockTick(6, "ACME", 10, System.currentTimeMillis());
            final StockTick tick7 = new StockTick(7, "ACME", 30, System.currentTimeMillis());
            final StockTick tick8 = new StockTick(8, "DROO", 50, System.currentTimeMillis());
            final EntryPoint entry = session.getEntryPoint("stream1");
            final InternalFactHandle handle5 = ((InternalFactHandle) (entry.insert(tick5)));
            final InternalFactHandle handle6 = ((InternalFactHandle) (entry.insert(tick6)));
            final InternalFactHandle handle7 = ((InternalFactHandle) (entry.insert(tick7)));
            final InternalFactHandle handle8 = ((InternalFactHandle) (entry.insert(tick8)));
            Assert.assertNotNull(handle5);
            Assert.assertNotNull(handle6);
            Assert.assertNotNull(handle7);
            Assert.assertNotNull(handle8);
            Assert.assertTrue(handle5.isEvent());
            Assert.assertTrue(handle6.isEvent());
            Assert.assertTrue(handle7.isEvent());
            Assert.assertTrue(handle8.isEvent());
            session.fireAllRules();
            Assert.assertEquals(1, results.size());
            Assert.assertSame(tick7, results.get(0));
        } finally {
            session.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testModifyRetracOnEntryPointFacts() {
        final String drl = ((((((((((((((((((((((("package org.drools.compiler\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "global java.util.List results;\n") + "\n") + "declare StockTick\n") + "    @role( event )\n") + "end\n") + "\n") + "rule \"Test entry point 1\"\n") + "when\n") + "    $st : StockTick( company == \"ACME\", price > 10 && < 100 ) from entry-point \"stream1\"\n") + "then\n") + "    results.add( Double.valueOf( $st.getPrice() ) );\n") + "    modify( $st ) { setPrice( 110 ) }\n") + "end\n") + "\n") + "rule \"Test entry point 2\"\n") + "when\n") + "    $st : StockTick( company == \"ACME\", price > 100 ) from entry-point \"stream1\"\n") + "then\n") + "    results.add( Double.valueOf( $st.getPrice() ) );\n") + "    delete( $st );\n") + "end";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List<? extends Number> results = new ArrayList<>();
            session.setGlobal("results", results);
            final StockTick tick5 = new StockTick(5, "DROO", 50, System.currentTimeMillis());
            final StockTick tick6 = new StockTick(6, "ACME", 10, System.currentTimeMillis());
            final StockTick tick7 = new StockTick(7, "ACME", 30, System.currentTimeMillis());
            final StockTick tick8 = new StockTick(8, "DROO", 50, System.currentTimeMillis());
            final EntryPoint entry = session.getEntryPoint("stream1");
            final InternalFactHandle handle5 = ((InternalFactHandle) (entry.insert(tick5)));
            final InternalFactHandle handle6 = ((InternalFactHandle) (entry.insert(tick6)));
            final InternalFactHandle handle7 = ((InternalFactHandle) (entry.insert(tick7)));
            final InternalFactHandle handle8 = ((InternalFactHandle) (entry.insert(tick8)));
            Assert.assertNotNull(handle5);
            Assert.assertNotNull(handle6);
            Assert.assertNotNull(handle7);
            Assert.assertNotNull(handle8);
            Assert.assertTrue(handle5.isEvent());
            Assert.assertTrue(handle6.isEvent());
            Assert.assertTrue(handle7.isEvent());
            Assert.assertTrue(handle8.isEvent());
            session.fireAllRules();
            Assert.assertEquals(2, results.size());
            Assert.assertEquals(30, results.get(0).intValue());
            Assert.assertEquals(110, results.get(1).intValue());
            // the 3 non-matched facts continue to exist in the entry point
            Assert.assertEquals(3, entry.getObjects().size());
            // but no fact was inserted into the main session
            Assert.assertEquals(0, session.getObjects().size());
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testModifyOnEntryPointFacts() {
        final String drl = (((((((((((((((((((((((((("package org.drools.compiler\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "declare StockTick\n") + "        @role ( event )\n") + "end\n") + "rule R1 salience 100\n") + "    when\n") + "        $s1 : StockTick( company == \'RHT\', price == 10 ) from entry-point ep1\n") + "    then\n") + "        StockTick s = $s1;\n") + "        modify( s ) { setPrice( 50 ) };\n") + "end\n") + "rule R2 salience 90\n") + "    when\n") + "        $s1 : StockTick( company == \'RHT\', price == 10 ) from entry-point ep2\n") + "    then\n") + "        StockTick s = $s1;\n") + "        modify( s ) { setPrice( 50 ) };\n") + "end\n") + "rule R3 salience 80\n") + "    when\n") + "        $s1 : StockTick( company == \'RHT\', price == 10 ) from entry-point ep3\n") + "    then\n") + "        StockTick s = $s1;\n") + "        modify( s ) { setPrice( 50 ) };\n") + "end\n";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
            ksession.addEventListener(ael);
            final EntryPoint ep1 = ksession.getEntryPoint("ep1");
            final EntryPoint ep2 = ksession.getEntryPoint("ep2");
            final EntryPoint ep3 = ksession.getEntryPoint("ep3");
            ep1.insert(new StockTick(1, "RHT", 10, 1000));
            ep2.insert(new StockTick(1, "RHT", 10, 1000));
            ep3.insert(new StockTick(1, "RHT", 10, 1000));
            final int rulesFired = ksession.fireAllRules();
            Assert.assertEquals(3, rulesFired);
            final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            Mockito.verify(ael, Mockito.times(3)).afterMatchFired(captor.capture());
            final List<AfterMatchFiredEvent> aafe = captor.getAllValues();
            Assert.assertThat(aafe.get(0).getMatch().getRule().getName(), CoreMatchers.is("R1"));
            Assert.assertThat(aafe.get(1).getMatch().getRule().getName(), CoreMatchers.is("R2"));
            Assert.assertThat(aafe.get(2).getMatch().getRule().getName(), CoreMatchers.is("R3"));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEntryPointWithAccumulateAndMVEL() {
        final String drl = ((((((((("package org.drools.compiler\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "rule R1 dialect \'mvel\'\n") + "    when\n") + "        $n : Number() from accumulate( \n") + "                 StockTick() from entry-point ep1,\n") + "                 count(1))") + "    then\n") + "end\n";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
            ksession.addEventListener(ael);
            final EntryPoint ep1 = ksession.getEntryPoint("ep1");
            ep1.insert(new StockTick(1, "RHT", 10, 1000));
            final int rulesFired = ksession.fireAllRules();
            Assert.assertEquals(1, rulesFired);
            final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            Mockito.verify(ael, Mockito.times(1)).afterMatchFired(captor.capture());
            final List<AfterMatchFiredEvent> aafe = captor.getAllValues();
            Assert.assertThat(aafe.get(0).getMatch().getRule().getName(), CoreMatchers.is("R1"));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testGetEntryPointList() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("stream-test", kieBaseTestConfiguration, "org/drools/compiler/integrationtests/test_EntryPointReference.drl");
        final KieSession session = kbase.newKieSession();
        try {
            final EntryPoint def = session.getEntryPoint(DEFAULT.getEntryPointId());
            final EntryPoint s1 = session.getEntryPoint("stream1");
            final EntryPoint s2 = session.getEntryPoint("stream2");
            final EntryPoint s3 = session.getEntryPoint("stream3");
            final Collection<? extends EntryPoint> eps = session.getEntryPoints();
            Assert.assertEquals(4, eps.size());
            Assert.assertTrue(eps.contains(def));
            Assert.assertTrue(eps.contains(s1));
            Assert.assertTrue(eps.contains(s2));
            Assert.assertTrue(eps.contains(s3));
        } finally {
            session.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEventDoesNotExpireIfNotInPattern() {
        final String drl = ((((((((((((("package org.drools.compiler\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "declare StockTick\n") + "    @role( event )\n") + "    @expires( 1s )\n") + "end\n") + "\n") + "rule X\n") + "when\n") + "    eval( true )\n") + "then \n") + "    // no-op\n") + "end";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession(STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final RuleRuntimeEventListener wml = Mockito.mock(RuleRuntimeEventListener.class);
            ksession.addEventListener(wml);
            final PseudoClockScheduler clock = ksession.getSessionClock();
            final StockTick st1 = new StockTick(1, "RHT", 100, 1000);
            final StockTick st2 = new StockTick(2, "RHT", 100, 1000);
            ksession.insert(st1);
            ksession.insert(st2);
            Mockito.verify(wml, Mockito.times(2)).objectInserted(ArgumentMatchers.any(ObjectInsertedEvent.class));
            Assert.assertThat(ksession.getObjects().size(), CoreMatchers.equalTo(2));
            Assert.assertThat(((Collection<Object>) (ksession.getObjects())), CoreMatchers.hasItems(st1, st2));
            ksession.fireAllRules();
            clock.advanceTime(3, TimeUnit.SECONDS);
            ksession.fireAllRules();
            Assert.assertThat(ksession.getObjects().size(), CoreMatchers.equalTo(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEventExpirationSetToZero() {
        final String drl = ((((((((((((("package org.drools.compiler\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "declare StockTick\n") + "    @role( event )\n") + "    @expires( 0 )\n") + "end\n") + "\n") + "rule X\n") + "when\n") + "    StockTick()\n") + "then \n") + "    // no-op\n") + "end";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession(STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final RuleRuntimeEventListener wml = Mockito.mock(RuleRuntimeEventListener.class);
            ksession.addEventListener(wml);
            final AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
            ksession.addEventListener(ael);
            final PseudoClockScheduler clock = ksession.getSessionClock();
            final StockTick st1 = new StockTick(1, "RHT", 100, 1000);
            final StockTick st2 = new StockTick(2, "RHT", 100, 1000);
            ksession.insert(st1);
            ksession.insert(st2);
            Assert.assertThat(ksession.fireAllRules(), CoreMatchers.equalTo(2));
            Mockito.verify(wml, Mockito.times(2)).objectInserted(ArgumentMatchers.any(ObjectInsertedEvent.class));
            Mockito.verify(ael, Mockito.times(2)).matchCreated(ArgumentMatchers.any(MatchCreatedEvent.class));
            Assert.assertThat(ksession.getObjects().size(), CoreMatchers.equalTo(2));
            Assert.assertThat(((Collection<Object>) (ksession.getObjects())), CoreMatchers.hasItems(st1, st2));
            clock.advanceTime(3, TimeUnit.SECONDS);
            ksession.fireAllRules();
            Assert.assertThat(ksession.getObjects().size(), CoreMatchers.equalTo(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testEventExpirationValue() {
        final String drl1 = (((((((((("package org.drools.pkg1\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "declare StockTick\n") + "    @role(event)\n") + "end\n") + "rule X\n") + "when\n") + "    StockTick()\n") + "then\n") + "end\n";
        final String drl2 = (((((((((("package org.drools.pkg2\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "declare StockTick\n") + "    @role(event)\n") + "end\n") + "rule X\n") + "when\n") + "    StockTick()\n") + "then\n") + "end\n";
        final InternalKnowledgeBase kbase = ((InternalKnowledgeBase) (KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl1, drl2)));
        final List<ObjectTypeNode> otns = kbase.getRete().getObjectTypeNodes();
        final ObjectType stot = new ClassObjectType(StockTick.class);
        for (final ObjectTypeNode otn : otns) {
            if (otn.getObjectType().isAssignableFrom(stot)) {
                Assert.assertEquals(NEVER_EXPIRES, otn.getExpirationOffset());
            }
        }
    }

    @Test(timeout = 10000)
    public void testDeclaredEntryPoint() {
        final String drl = "package org.jboss.qa.brms.declaredep\n" + ((((((("declare entry-point UnusedEntryPoint\n" + "end\n") + "rule HelloWorld\n") + "    when\n") + "        String( ) from entry-point UsedEntryPoint\n") + "    then\n") + "        // consequences\n") + "end\n");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            Assert.assertNotNull(ksession.getEntryPoint("UsedEntryPoint"));
            Assert.assertNotNull(ksession.getEntryPoint("UnusedEntryPoint"));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testWindowDeclaration() {
        final String drl = ((((((((((((((((("package org.drools.compiler\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "declare StockTick\n") + "    @role(event)\n") + "end\n") + "declare window RedHatTicks\n") + "    StockTick( company == \'RHT\' )\n") + "               over window:length(5)\n") + "               from entry-point ticks\n") + "end\n") + "rule X\n") + "when\n") + "    accumulate( $s : StockTick( price > 20 ) from window RedHatTicks,\n") + "                $sum : sum( $s.getPrice() ),\n") + "                $cnt : count( $s ) )\n") + "then\n") + "end\n";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
            ksession.addEventListener(ael);
            final EntryPoint ep = ksession.getEntryPoint("ticks");
            ep.insert(new StockTick(1, "ACME", 20, 1000));// not in the window

            ep.insert(new StockTick(2, "RHT", 20, 1000));// not > 20

            ep.insert(new StockTick(3, "RHT", 30, 1000));
            ep.insert(new StockTick(4, "ACME", 30, 1000));// not in the window

            ep.insert(new StockTick(5, "RHT", 25, 1000));
            ep.insert(new StockTick(6, "ACME", 10, 1000));// not in the window

            ep.insert(new StockTick(7, "RHT", 10, 1000));// not > 20

            ep.insert(new StockTick(8, "RHT", 40, 1000));
            ksession.fireAllRules();
            final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            Mockito.verify(ael, Mockito.times(1)).afterMatchFired(captor.capture());
            final AfterMatchFiredEvent aafe = captor.getValue();
            Assert.assertThat(((Number) (aafe.getMatch().getDeclarationValue("$sum"))).intValue(), CoreMatchers.is(95));
            Assert.assertThat(((Number) (aafe.getMatch().getDeclarationValue("$cnt"))).intValue(), CoreMatchers.is(3));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testWindowDeclaration2() {
        final String drl = "package org.drools.compiler\n" + (((((((((((((("declare Double\n" + "    @role(event)\n") + "end\n") + "declare window Streem\n") + "    Double() over window:length( 10 ) from entry-point data\n") + "end\n") + "rule \"See\"\n") + "when\n") + "    $sum : Double() from accumulate (\n") + "        $d: Double()\n") + "            from window Streem,\n") + "        sum( $d )\n") + "    )\n") + "then\n") + "end");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
            ksession.addEventListener(ael);
            final EntryPoint ep = ksession.getEntryPoint("data");
            ep.insert(10.0);
            ep.insert(11.0);
            ep.insert(12.0);
            ksession.fireAllRules();
            final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            Mockito.verify(ael, Mockito.times(1)).afterMatchFired(captor.capture());
            final AfterMatchFiredEvent aafe = captor.getValue();
            Assert.assertThat(((Number) (aafe.getMatch().getDeclarationValue("$sum"))).intValue(), CoreMatchers.is(33));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testMultipleWindows() {
        final String drl = ((((((((((("package org.drools.compiler\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "declare StockTick\n") + "    @role(event)\n") + "end\n") + "rule FaultsCoincide\n") + "when\n") + "   f1 : StockTick( company == \"RHT\" ) over window:length( 1 )\n") + "   f2 : StockTick( company == \"JBW\" ) over window:length( 1 )\n") + "then\n") + "end";
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
            ksession.addEventListener(ael);
            final StockTick st1 = new StockTick(1, "RHT", 10, 1000);
            ksession.insert(st1);
            final StockTick st2 = new StockTick(2, "JBW", 10, 1000);
            ksession.insert(st2);
            ksession.fireAllRules();
            final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            Mockito.verify(ael, Mockito.times(1)).afterMatchFired(captor.capture());
            final AfterMatchFiredEvent aafe = captor.getValue();
            Assert.assertThat(aafe.getMatch().getDeclarationValue("f1"), CoreMatchers.is(st1));
            Assert.assertThat(aafe.getMatch().getDeclarationValue("f2"), CoreMatchers.is(st2));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testWindowWithEntryPointCompilationError() {
        final String drl = ((((((((("import " + (Cheese.class.getCanonicalName())) + ";\n") + "declare window X\n") + "   Cheese( type == \"gorgonzola\" ) over window:time(1m) from entry-point Z\n") + "end\n") + "rule R when\n") + "   $c : Cheese( price < 100 ) from window X\n") + "then\n") + "   System.out.println($c);\n") + "end\n";
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).withFailMessage("Should have raised a compilation error as Cheese is not declared as an event.").isNotEmpty();
    }

    @Test(timeout = 10000)
    public void testAtomicActivationFiring() throws Exception {
        // JBRULES-3383
        final String drl = "package org.drools.compiler.test\n" + (((((((((((((((((((((((((((((((((("declare Event\n" + "   @role(event)\n") + "   name : String\n") + "end\n") + "declare Monitor\n") + "   @role(event)\n") + "   event : Event\n") + "   name : String\n") + "end\n") + "\n") + "rule \"start monitoring\"\n") + "when\n") + "    $e : Event( $in : name )\n") + "    not Monitor( name == $in )\n") + "then\n") + "    Monitor m = new Monitor( $e, $in );\n") + "    insert( m );\n") + "end\n") + "\n") + "rule \"stop monitoring\"\n") + "timer( int: 1s )\n") + "when\n") + "    $m : Monitor( $in : name )\n") + "    $e : Event( name == $in )\n") + "then\n") + "    retract( $m );\n") + "    retract( $m.getEvent() );\n") + "end\n") + "rule \"halt\"\n") + "salience -1\n") + "when\n") + "    not Event( )\n") + "then\n") + "    drools.halt();\n") + "end\n");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("stream-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.addEventListener(new DebugAgendaEventListener());
            final FactType eventType = kbase.getFactType("org.drools.compiler.test", "Event");
            final Object event = eventType.newInstance();
            eventType.set(event, "name", "myName");
            ksession.insert(event);
            ksession.fireUntilHalt();
        } finally {
            ksession.dispose();
        }
    }
}
