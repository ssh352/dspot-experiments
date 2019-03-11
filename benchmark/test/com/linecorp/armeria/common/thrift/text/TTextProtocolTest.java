/**
 * Copyright 2015 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
/**
 * =================================================================================================
 */
/**
 * Copyright 2011 Twitter, Inc.
 */
/**
 * -------------------------------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this work except in compliance with the License.
 */
/**
 * You may obtain a copy of the License in the LICENSE file, or at:
 */
/**
 *
 */
/**
 * https://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * =================================================================================================
 */
package com.linecorp.armeria.common.thrift.text;


import RpcDebugService.Processor.doDebug;
import TApplicationException.BAD_SEQUENCE_ID;
import TMessageType.CALL;
import TMessageType.EXCEPTION;
import TMessageType.ONEWAY;
import TMessageType.REPLY;
import com.linecorp.armeria.common.thrift.text.RpcDebugService.doDebug_args;
import com.linecorp.armeria.common.thrift.text.RpcDebugService.doDebug_result;
import com.linecorp.armeria.internal.thrift.TApplicationExceptions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TIOStreamTransport;
import org.junit.Test;


/**
 * Tests the TTextProtocol.
 *
 * <p>TODO(Alex Roetter): add more tests, especially ones that verify
 * that we generate ParseErrors for invalid input
 *
 * @author Alex Roetter
 */
public class TTextProtocolTest {
    private String fileContents;

    private Base64 base64Encoder;

    /**
     * Read in (deserialize) a thrift message in TTextProtocol format
     * from a file on disk, then serialize it back out to a string.
     * Finally, deserialize that string and compare to the original
     * message.
     */
    @Test
    public void tTextProtocolReadWriteTest() throws Exception {
        // Deserialize the file contents into a thrift message.
        final ByteArrayInputStream bais1 = new ByteArrayInputStream(fileContents.getBytes());
        final TTextProtocolTestMsg msg1 = new TTextProtocolTestMsg();
        msg1.read(new TTextProtocol(new TIOStreamTransport(bais1)));
        assertThat(msg1).isEqualTo(testMsg());
        // Serialize that thrift message out to a byte array
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg1.write(new TTextProtocol(new TIOStreamTransport(baos)));
        final byte[] bytes = baos.toByteArray();
        // Deserialize that string back to a thrift message.
        final ByteArrayInputStream bais2 = new ByteArrayInputStream(bytes);
        final TTextProtocolTestMsg msg2 = new TTextProtocolTestMsg();
        msg2.read(new TTextProtocol(new TIOStreamTransport(bais2)));
        assertThat(msg2).isEqualTo(msg1);
    }

    @Test
    public void rpcCall() throws Exception {
        final String request = ("{\n" + (((((((((("  \"method\" : \"doDebug\",\n" + "  \"type\" : \"CALL\",\n") + "  \"seqid\" : 1,\n") + "  \"args\" : {\n") + "    \"methodArg1\" : \"foo1\",\n") + "    \"methodArg2\" : 200,\n") + "    \"details\" : {\n") + "      \"detailsArg1\" : \"foo2\",\n") + "      \"detailsArg2\" : 100\n") + "    }\n") + "  }\n")) + '}';
        TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        final TMessage header = prot.readMessageBegin();
        final doDebug_args args = new doDebug().getEmptyArgsInstance();
        args.read(prot);
        prot.readMessageEnd();
        assertThat(header.name).isEqualTo("doDebug");
        assertThat(header.type).isEqualTo(CALL);
        assertThat(header.seqid).isOne();
        assertThat(args.getMethodArg1()).isEqualTo("foo1");
        assertThat(args.getMethodArg2()).isEqualTo(200);
        assertThat(args.getDetails().getDetailsArg1()).isEqualTo("foo2");
        assertThat(args.getDetails().getDetailsArg2()).isEqualTo(100);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        prot = new TTextProtocol(new TIOStreamTransport(outputStream));
        prot.writeMessageBegin(header);
        args.write(prot);
        prot.writeMessageEnd();
        assertThatJson(new String(outputStream.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(request);
    }

    @Test
    public void rpcCall_noSeqId() throws Exception {
        final String request = ("{\n" + ((((((((("  \"method\" : \"doDebug\",\n" + "  \"type\" : \"CALL\",\n") + "  \"args\" : {\n") + "    \"methodArg1\" : \"foo1\",\n") + "    \"methodArg2\" : 200,\n") + "    \"details\" : {\n") + "      \"detailsArg1\" : \"foo2\",\n") + "      \"detailsArg2\" : 100\n") + "    }\n") + "  }\n")) + '}';
        final TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        final TMessage header = prot.readMessageBegin();
        final doDebug_args args = new doDebug().getEmptyArgsInstance();
        args.read(prot);
        prot.readMessageEnd();
        assertThat(header.name).isEqualTo("doDebug");
        assertThat(header.type).isEqualTo(CALL);
        assertThat(header.seqid).isZero();
    }

    @Test
    public void rpcCall_oneWay() throws Exception {
        final String request = ("{\n" + (((((((((("  \"method\" : \"doDebug\",\n" + "  \"type\" : \"ONEWAY\",\n") + "  \"seqid\" : 1,\n") + "  \"args\" : {\n") + "    \"methodArg1\" : \"foo1\",\n") + "    \"methodArg2\" : 200,\n") + "    \"details\" : {\n") + "      \"detailsArg1\" : \"foo2\",\n") + "      \"detailsArg2\" : 100\n") + "    }\n") + "  }\n")) + '}';
        TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        final TMessage header = prot.readMessageBegin();
        final doDebug_args args = new doDebug().getEmptyArgsInstance();
        args.read(prot);
        prot.readMessageEnd();
        assertThat(header.name).isEqualTo("doDebug");
        assertThat(header.type).isEqualTo(ONEWAY);
        assertThat(header.seqid).isEqualTo(1);
        assertThat(args.getMethodArg1()).isEqualTo("foo1");
        assertThat(args.getMethodArg2()).isEqualTo(200);
        assertThat(args.getDetails().getDetailsArg1()).isEqualTo("foo2");
        assertThat(args.getDetails().getDetailsArg2()).isEqualTo(100);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        prot = new TTextProtocol(new TIOStreamTransport(outputStream));
        prot.writeMessageBegin(header);
        args.write(prot);
        prot.writeMessageEnd();
        assertThatJson(new String(outputStream.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(request);
    }

    @Test
    public void rpcReply() throws Exception {
        final String request = ("{\n" + ((((((("  \"method\" : \"doDebug\",\n" + "  \"type\" : \"REPLY\",\n") + "  \"seqid\" : 100,\n") + "  \"args\" : {\n") + "    \"success\" : {\n") + "      \"response\" : \"Nice response\"\n") + "    }\n") + "  }\n")) + '}';
        TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        final TMessage header = prot.readMessageBegin();
        final doDebug_result result = new doDebug_result();
        result.read(prot);
        prot.readMessageEnd();
        assertThat(header.name).isEqualTo("doDebug");
        assertThat(header.type).isEqualTo(REPLY);
        assertThat(header.seqid).isEqualTo(100);
        assertThat(result.getSuccess().getResponse()).isEqualTo("Nice response");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        prot = new TTextProtocol(new TIOStreamTransport(outputStream));
        prot.writeMessageBegin(header);
        result.write(prot);
        prot.writeMessageEnd();
        assertThatJson(new String(outputStream.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(request);
    }

    @Test
    public void rpcException() throws Exception {
        final String request = ("{\n" + ((((((("  \"method\" : \"doDebug\",\n" + "  \"type\" : \"EXCEPTION\",\n") + "  \"seqid\" : 101,\n") + "  \"args\" : {\n") + "    \"e\" : {\n") + "      \"reason\" : \"Bad rpc\"\n") + "    }\n") + "  }\n")) + '}';
        TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        final TMessage header = prot.readMessageBegin();
        final doDebug_result result = new doDebug_result();
        result.read(prot);
        prot.readMessageEnd();
        assertThat(header.name).isEqualTo("doDebug");
        assertThat(header.type).isEqualTo(EXCEPTION);
        assertThat(header.seqid).isEqualTo(101);
        assertThat(result.getE().getReason()).isEqualTo("Bad rpc");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        prot = new TTextProtocol(new TIOStreamTransport(outputStream));
        prot.writeMessageBegin(header);
        result.write(prot);
        prot.writeMessageEnd();
        assertThatJson(new String(outputStream.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(request);
    }

    @Test
    public void rpcTApplicationException() throws Exception {
        final String request = ("{\n" + ((((((("  \"method\" : \"doDebug\",\n" + "  \"type\" : \"EXCEPTION\",\n") + "  \"seqid\" : 101,\n") + "  \"args\" : {\n") + "    \"type\" : 4,\n") + "    \"message\" : \"bad_seq_id\"\n") + "    }\n") + "  }\n")) + '}';
        TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        final TMessage header = prot.readMessageBegin();
        final TApplicationException result = TApplicationExceptions.read(prot);
        prot.readMessageEnd();
        assertThat(header.name).isEqualTo("doDebug");
        assertThat(header.type).isEqualTo(EXCEPTION);
        assertThat(header.seqid).isEqualTo(101);
        assertThat(result.getType()).isEqualTo(BAD_SEQUENCE_ID);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        prot = new TTextProtocol(new TIOStreamTransport(outputStream));
        prot.writeMessageBegin(header);
        new TApplicationException(TApplicationException.BAD_SEQUENCE_ID, "bad_seq_id").write(prot);
        prot.writeMessageEnd();
        assertThatJson(new String(outputStream.toByteArray(), StandardCharsets.UTF_8)).isEqualTo(request);
    }

    @Test(expected = TException.class)
    public void rpcNoMethod() throws Exception {
        final String request = ("{\n" + (((((((("  \"type\" : \"CALL\",\n" + "  \"args\" : {\n") + "    \"methodArg1\" : \"foo1\",\n") + "    \"methodArg2\" : 200,\n") + "    \"details\" : {\n") + "      \"detailsArg1\" : \"foo2\",\n") + "      \"detailsArg2\" : 100\n") + "    }\n") + "  }\n")) + '}';
        final TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        prot.readMessageBegin();
    }

    @Test(expected = TException.class)
    public void rpcNoType() throws Exception {
        final String request = ("{\n" + (((((((("  \"method\" : \"doDebug\",\n" + "  \"args\" : {\n") + "    \"methodArg1\" : \"foo1\",\n") + "    \"methodArg2\" : 200,\n") + "    \"details\" : {\n") + "      \"detailsArg1\" : \"foo2\",\n") + "      \"detailsArg2\" : 100\n") + "    }\n") + "  }\n")) + '}';
        final TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        prot.readMessageBegin();
    }

    @Test(expected = TException.class)
    public void noRpcArgs() throws Exception {
        final String request = ("{\n" + ("  \"method\" : \"doDebug\"\n" + "  \"type\" : \"CALL\",\n")) + '}';
        final TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        prot.readMessageBegin();
    }

    @Test(expected = TException.class)
    public void rpcArgsNotObject() throws Exception {
        final String request = ("{\n" + ("  \"method\" : \"doDebug\",\n" + "  \"args\" : 100\n")) + '}';
        final TTextProtocol prot = new TTextProtocol(new TIOStreamTransport(new ByteArrayInputStream(request.getBytes())));
        prot.readMessageBegin();
    }
}

