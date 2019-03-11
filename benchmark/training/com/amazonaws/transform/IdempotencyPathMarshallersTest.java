/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.transform;


import PathMarshallers.PathMarshaller;
import org.junit.Assert;
import org.junit.Test;

import static PathMarshallers.IDEMPOTENCY;


public class IdempotencyPathMarshallersTest {
    private final PathMarshaller marshaller = IDEMPOTENCY;

    @Test
    public void nullPathValue_ReplacesWithGeneratedValue() {
        Assert.assertTrue(marshaller.marshall("/foo/{pathParam}", "pathParam", ((String) (null))).matches("/foo/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyPathValue_ThrowsException() {
        marshaller.marshall("/foo/{pathParam}", "pathParam", "");
    }

    @Test
    public void nonEmptyPathValue_ReplacesPlaceholder() {
        Assert.assertEquals("/foo/nonEmpty", marshaller.marshall("/foo/{pathParam}", "pathParam", "nonEmpty"));
    }

    @Test
    public void pathValueWithSlashes_UrlEncodedWhenReplaced() {
        Assert.assertEquals("/foo/has%2Fslash", marshaller.marshall("/foo/{pathParam}", "pathParam", "has/slash"));
    }
}

