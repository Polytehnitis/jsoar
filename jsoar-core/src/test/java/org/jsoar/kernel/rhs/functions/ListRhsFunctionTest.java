/*
 * Copyright (c) 2010 Dave Ray <daveray@gmail.com>
 *
 * Created on May 28, 2010
 */
package org.jsoar.kernel.rhs.functions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jsoar.kernel.Agent;
import org.jsoar.kernel.RunType;
import org.jsoar.kernel.symbols.Symbol;
import org.jsoar.util.ByRef;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author ray
 */
class ListRhsFunctionTest
{
    private Agent agent;
    
    @BeforeEach
    void setUp() throws Exception
    {
        agent = new Agent();
        agent.getTrace().disableAll();
    }
    
    @AfterEach
    void tearDown() throws Exception
    {
        agent.dispose();
    }
    
    @Test
    void testEmptyList() throws Exception
    {
        final ByRef<Boolean> succeeded = ByRef.create(false);
        agent.getRhsFunctions().registerHandler(new StandaloneRhsFunctionHandler("succeeded")
        {
            
            @Override
            public Symbol execute(RhsFunctionContext context,
                    List<Symbol> arguments) throws RhsFunctionException
            {
                succeeded.value = true;
                return null;
            }
        });
        agent.getProductions().loadProduction("" +
                "callList (state <s> ^superstate nil) " +
                "--> " +
                "(<s> ^result (list))");
        agent.getProductions().loadProduction("" +
                "checkResult (state <s> ^superstate nil ^result nil) " +
                "-->" +
                "(succeeded)");
        
        agent.runFor(1, RunType.DECISIONS);
        assertTrue(succeeded.value);
    }
    
    @Test
    void testList() throws Exception
    {
        final ByRef<Boolean> succeeded = ByRef.create(false);
        agent.getRhsFunctions().registerHandler(new StandaloneRhsFunctionHandler("succeeded")
        {
            
            @Override
            public Symbol execute(RhsFunctionContext context,
                    List<Symbol> arguments) throws RhsFunctionException
            {
                succeeded.value = true;
                return null;
            }
        });
        agent.getProductions().loadProduction("" +
                "callList (state <s> ^superstate nil) " +
                "--> " +
                "(<s> ^result (list a b 99 3.14))");
        agent.getProductions().loadProduction("" +
                "checkResult (state <s> ^superstate nil ^result <r>) " +
                "(<r> ^value a ^next <n1>)" +
                "(<n1> ^value b ^next <n2>)" +
                "(<n2> ^value 99 ^next <n3>)" +
                "(<n3> ^value 3.14 ^next nil)" +
                "-->" +
                "(succeeded)");
        
        agent.runFor(1, RunType.DECISIONS);
        assertTrue(succeeded.value);
    }
    
}
