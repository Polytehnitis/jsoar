/*
 * Copyright (c) 2008  Dave Ray <daveray@gmail.com>
 *
 * Created on Sep 18, 2008
 */
package org.jsoar.kernel.rhs.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jsoar.JSoarTest;
import org.jsoar.kernel.symbols.Symbols;
import org.junit.jupiter.api.Test;

/**
 * @author ray
 */
class MultiplyTest extends JSoarTest
{
    
    @Test
    void testZeroArgs() throws Exception
    {
        Multiply multiply = new Multiply();
        
        assertEquals(1, multiply.execute(rhsFuncContext, Symbols.asList(syms)).asInteger().getValue());
    }
    
    @Test
    void testOneIntArg() throws Exception
    {
        Multiply multiply = new Multiply();
        
        assertEquals(33, multiply.execute(rhsFuncContext, Symbols.asList(syms, 33)).asInteger().getValue());
    }
    
    @Test
    void testOneFloatArg() throws Exception
    {
        Multiply multiply = new Multiply();
        
        assertEquals(123.4, multiply.execute(rhsFuncContext, Symbols.asList(syms, 123.4)).asDouble().getValue(), 0.0001);
    }
    
    @Test
    void testMixedArgs() throws Exception
    {
        Multiply multiply = new Multiply();
        
        assertEquals(2 * 123.4 * -2, multiply.execute(rhsFuncContext, Symbols.asList(syms, 2, 123.4, -2)).asDouble().getValue(), 0.0001);
    }
    
    @Test
    void testIntArgs() throws Exception
    {
        Multiply multiply = new Multiply();
        
        assertEquals(2 * 3 * 4 * -2, multiply.execute(rhsFuncContext, Symbols.asList(syms, 2, 3, 4, -2)).asInteger().getValue());
    }
    
    @Test
    void testLargeIntArgs() throws Exception
    {
        Multiply multiply = new Multiply();
        
        assertEquals(2000000000000L * 3 * 4 * -2, multiply.execute(rhsFuncContext, Symbols.asList(syms, 2000000000000L, 3, 4, -2)).asInteger().getValue());
    }
    
    @Test
    void testFloatArgs() throws Exception
    {
        Multiply multiply = new Multiply();
        
        assertEquals(2.0 * 3.0 * 4.0 * -2.0, multiply.execute(rhsFuncContext, Symbols.asList(syms, 2.0, 3.0, 4.0, -2.0)).asDouble().getValue(), 0.0001);
    }
    
}
