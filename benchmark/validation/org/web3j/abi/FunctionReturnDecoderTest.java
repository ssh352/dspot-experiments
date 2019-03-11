package org.web3j.abi;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes16;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;


public class FunctionReturnDecoderTest {
    @Test
    public void testSimpleFunctionDecode() {
        Function function = new Function("test", Collections.<Type>emptyList(), Collections.singletonList(new TypeReference<Uint>() {}));
        Assert.assertThat(FunctionReturnDecoder.decode("0x0000000000000000000000000000000000000000000000000000000000000037", function.getOutputParameters()), IsEqual.equalTo(Collections.singletonList(new Uint(BigInteger.valueOf(55)))));
    }

    @Test
    public void testSimpleFunctionStringResultDecode() {
        Function function = new Function("simple", Arrays.asList(), Collections.singletonList(new TypeReference<Utf8String>() {}));
        List<Type> utf8Strings = FunctionReturnDecoder.decode(("0x0000000000000000000000000000000000000000000000000000000000000020" + ("000000000000000000000000000000000000000000000000000000000000000d" + "6f6e65206d6f72652074696d6500000000000000000000000000000000000000")), function.getOutputParameters());
        Assert.assertThat(utf8Strings.get(0).getValue(), CoreMatchers.is("one more time"));
    }

    @Test
    public void testFunctionEmptyStringResultDecode() {
        Function function = new Function("test", Collections.emptyList(), Collections.singletonList(new TypeReference<Utf8String>() {}));
        List<Type> utf8Strings = FunctionReturnDecoder.decode(("0x0000000000000000000000000000000000000000000000000000000000000020" + "0000000000000000000000000000000000000000000000000000000000000000"), function.getOutputParameters());
        Assert.assertThat(utf8Strings.get(0).getValue(), CoreMatchers.is(""));
    }

    @Test
    public void testMultipleResultFunctionDecode() {
        Function function = new Function("test", Collections.<Type>emptyList(), Arrays.asList(new TypeReference<Uint>() {}, new TypeReference<Uint>() {}));
        Assert.assertThat(FunctionReturnDecoder.decode(("0x0000000000000000000000000000000000000000000000000000000000000037" + "0000000000000000000000000000000000000000000000000000000000000007"), function.getOutputParameters()), IsEqual.equalTo(Arrays.asList(new Uint(BigInteger.valueOf(55)), new Uint(BigInteger.valueOf(7)))));
    }

    @Test
    public void testDecodeMultipleStringValues() {
        Function function = new Function("function", Collections.<Type>emptyList(), Arrays.asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        Assert.assertThat(FunctionReturnDecoder.decode(("0x0000000000000000000000000000000000000000000000000000000000000080" + (((((((((("00000000000000000000000000000000000000000000000000000000000000c0" + "0000000000000000000000000000000000000000000000000000000000000100") + "0000000000000000000000000000000000000000000000000000000000000140") + "0000000000000000000000000000000000000000000000000000000000000004") + "6465663100000000000000000000000000000000000000000000000000000000") + "0000000000000000000000000000000000000000000000000000000000000004") + "6768693100000000000000000000000000000000000000000000000000000000") + "0000000000000000000000000000000000000000000000000000000000000004") + "6a6b6c3100000000000000000000000000000000000000000000000000000000") + "0000000000000000000000000000000000000000000000000000000000000004") + "6d6e6f3200000000000000000000000000000000000000000000000000000000")), function.getOutputParameters()), IsEqual.equalTo(Arrays.asList(new Utf8String("def1"), new Utf8String("ghi1"), new Utf8String("jkl1"), new Utf8String("mno2"))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDecodeStaticArrayValue() {
        List<TypeReference<Type>> outputParameters = new ArrayList<>(1);
        outputParameters.add(((TypeReference) (new TypeReference.StaticArrayTypeReference<org.web3j.abi.datatypes.StaticArray<Uint256>>(2) {})));
        outputParameters.add(((TypeReference) (new TypeReference<Uint256>() {})));
        List<Type> decoded = FunctionReturnDecoder.decode(("0x0000000000000000000000000000000000000000000000000000000000000037" + ("0000000000000000000000000000000000000000000000000000000000000001" + "000000000000000000000000000000000000000000000000000000000000000a")), outputParameters);
        List<Type> expected = Arrays.asList(new org.web3j.abi.datatypes.StaticArray(new Uint256(BigInteger.valueOf(55)), new Uint256(BigInteger.ONE)), new Uint256(BigInteger.TEN));
        Assert.assertThat(decoded, IsEqual.equalTo(expected));
    }

    @Test
    public void testVoidResultFunctionDecode() {
        Function function = new Function("test", Collections.emptyList(), Collections.emptyList());
        Assert.assertThat(FunctionReturnDecoder.decode("0x", function.getOutputParameters()), CoreMatchers.is(Collections.emptyList()));
    }

    @Test
    public void testEmptyResultFunctionDecode() {
        Function function = new Function("test", Collections.emptyList(), Collections.singletonList(new TypeReference<Uint>() {}));
        Assert.assertThat(FunctionReturnDecoder.decode("0x", function.getOutputParameters()), CoreMatchers.is(Collections.emptyList()));
    }

    @Test
    public void testDecodeIndexedUint256Value() {
        Uint256 value = new Uint256(BigInteger.TEN);
        String encoded = TypeEncoder.encodeNumeric(value);
        Assert.assertThat(FunctionReturnDecoder.decodeIndexedValue(encoded, new TypeReference<Uint256>() {}), IsEqual.equalTo(value));
    }

    @Test
    public void testDecodeIndexedStringValue() {
        Utf8String string = new Utf8String("some text");
        String encoded = TypeEncoder.encodeString(string);
        String hash = Hash.sha3(encoded);
        Assert.assertThat(FunctionReturnDecoder.decodeIndexedValue(hash, new TypeReference<Utf8String>() {}), IsEqual.equalTo(new Bytes32(Numeric.hexStringToByteArray(hash))));
    }

    @Test
    public void testDecodeIndexedBytes32Value() {
        String rawInput = "0x1234567890123456789012345678901234567890123456789012345678901234";
        byte[] rawInputBytes = Numeric.hexStringToByteArray(rawInput);
        Assert.assertThat(FunctionReturnDecoder.decodeIndexedValue(rawInput, new TypeReference<Bytes32>() {}), IsEqual.equalTo(new Bytes32(rawInputBytes)));
    }

    @Test
    public void testDecodeIndexedBytes16Value() {
        String rawInput = "0x1234567890123456789012345678901200000000000000000000000000000000";
        byte[] rawInputBytes = Numeric.hexStringToByteArray(rawInput.substring(0, 34));
        Assert.assertThat(FunctionReturnDecoder.decodeIndexedValue(rawInput, new TypeReference<Bytes16>() {}), IsEqual.equalTo(new Bytes16(rawInputBytes)));
    }

    @Test
    public void testDecodeIndexedDynamicBytesValue() {
        DynamicBytes bytes = new DynamicBytes(new byte[]{ 1, 2, 3, 4, 5 });
        String encoded = TypeEncoder.encodeDynamicBytes(bytes);
        String hash = Hash.sha3(encoded);
        Assert.assertThat(FunctionReturnDecoder.decodeIndexedValue(hash, new TypeReference<DynamicBytes>() {}), IsEqual.equalTo(new Bytes32(Numeric.hexStringToByteArray(hash))));
    }

    @Test
    public void testDecodeIndexedDynamicArrayValue() {
        DynamicArray<Uint256> array = new DynamicArray(new Uint256(BigInteger.TEN));
        String encoded = TypeEncoder.encodeDynamicArray(array);
        String hash = Hash.sha3(encoded);
        Assert.assertThat(FunctionReturnDecoder.decodeIndexedValue(hash, new TypeReference<DynamicArray>() {}), IsEqual.equalTo(new Bytes32(Numeric.hexStringToByteArray(hash))));
    }
}

