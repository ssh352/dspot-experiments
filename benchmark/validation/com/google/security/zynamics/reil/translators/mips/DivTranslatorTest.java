/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.reil.translators.mips;


import OperandSize.DWORD;
import ReilRegisterStatus.DEFINED;
import com.google.common.collect.Lists;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyMips;
import com.google.security.zynamics.reil.interpreter.EmptyInterpreterPolicy;
import com.google.security.zynamics.reil.interpreter.Endianness;
import com.google.security.zynamics.reil.interpreter.InterpreterException;
import com.google.security.zynamics.reil.interpreter.ReilInterpreter;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import com.google.security.zynamics.reil.translators.StandardEnvironment;
import com.google.security.zynamics.zylib.disassembly.ExpressionType;
import com.google.security.zynamics.zylib.disassembly.IInstruction;
import com.google.security.zynamics.zylib.disassembly.MockInstruction;
import com.google.security.zynamics.zylib.disassembly.MockOperandTree;
import com.google.security.zynamics.zylib.disassembly.MockOperandTreeNode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class DivTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyMips(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final DivTranslator translator = new DivTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testDivFour() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$gp", BigInteger.valueOf(4294967289L), DWORD, DEFINED);
        interpreter.setRegister("$ra", BigInteger.valueOf(4294967293L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$gp"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$ra"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("div", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        // check correct outcome
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294967295L), interpreter.getVariableValue("HI"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("LO"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testDivOne() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$sp", BigInteger.valueOf(7L), DWORD, DEFINED);
        interpreter.setRegister("$ra", BigInteger.valueOf(3L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$sp"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$ra"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("div", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        // check correct outcome
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(1L), interpreter.getVariableValue("HI"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("LO"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testDivThree() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$sp", BigInteger.valueOf(7L), DWORD, DEFINED);
        interpreter.setRegister("$ra", BigInteger.valueOf(4294967293L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$sp"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$ra"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("div", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        // check correct outcome
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(1L), interpreter.getVariableValue("HI"));
        Assert.assertEquals(BigInteger.valueOf(4294967294L), interpreter.getVariableValue("LO"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testDivTwo() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$gp", BigInteger.valueOf(4294967289L), DWORD, DEFINED);
        interpreter.setRegister("$ra", BigInteger.valueOf(3L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$gp"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$ra"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("div", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        // check correct outcome
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294967295L), interpreter.getVariableValue("HI"));
        Assert.assertEquals(BigInteger.valueOf(4294967294L), interpreter.getVariableValue("LO"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}
