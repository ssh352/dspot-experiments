/**
 * *****************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 * *****************************************************************************
 */
package com.eclipsesource.v8;


import org.junit.Test;


public class NullScriptExecuteTest {
    private V8 v8;

    @Test(expected = NullPointerException.class)
    public void testStringScript() {
        v8.executeStringScript(null);
    }

    @Test(expected = NullPointerException.class)
    public void testArrayScript() {
        v8.executeArrayScript(null);
    }

    @Test(expected = NullPointerException.class)
    public void testBooleancript() {
        v8.executeBooleanScript(null);
    }

    @Test(expected = NullPointerException.class)
    public void testDoubleScript() {
        v8.executeDoubleScript(null);
    }

    @Test(expected = NullPointerException.class)
    public void testIntScript() {
        v8.executeIntegerScript(null);
    }

    @Test(expected = NullPointerException.class)
    public void testObjectScript() {
        v8.executeObjectScript(null);
    }

    @Test(expected = NullPointerException.class)
    public void testScript() {
        v8.executeScript(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStringScript() {
        v8.executeVoidScript(null);
    }
}

