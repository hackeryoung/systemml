/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2013
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.test.integration.functions.unary.scalar;

import org.junit.Test;

import com.ibm.bi.dml.test.integration.AutomatedTestBase;
import com.ibm.bi.dml.test.integration.TestConfiguration;
import com.ibm.bi.dml.test.utils.TestUtils;



/**
 * <p><b>Positive tests:</b></p>
 * <ul>
 * 	<li>positive scalar (int, double)</li>
 * 	<li>negative scalar (int, double)</li>
 * 	<li>zero scalar (int, double)</li>
 * 	<li>random scalar (int, double)</li>
 * </ul>
 * <p><b>Negative tests:</b></p>
 * 
 * 
 */
public class ACosTest extends AutomatedTestBase 
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2013\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	@Override
	public void setUp() {
		baseDirectory = SCRIPT_DIR + "functions/unary/scalar/";
		
		// positive tests
		availableTestConfigurations.put("PositiveTest", new TestConfiguration("ACosTest",
				new String[] { "int", "double" }));
		availableTestConfigurations.put("NegativeTest", new TestConfiguration("ACosTest",
				new String[] { "int", "double" }));
		availableTestConfigurations.put("ZeroTest", new TestConfiguration("ACosTest",
				new String[] { "int", "double" }));
		availableTestConfigurations.put("RandomTest", new TestConfiguration("ACosTest",
				new String[] { "int", "double" }));
		
		// negative tests
	}
	
	@Test
	public void testPositive() {
		int intValue = 5;
		double doubleValue = 5.0;
		
		TestConfiguration config = availableTestConfigurations.get("PositiveTest");
		config.addVariable("int", intValue);
		config.addVariable("double", doubleValue);
		
		loadTestConfiguration("PositiveTest");
		
		double computedIntValue = Math.acos(intValue);
		double computedDoubleValue = Math.acos(doubleValue);
		
		createHelperMatrix();
		writeExpectedHelperMatrix("int", computedIntValue);
		writeExpectedHelperMatrix("double", computedDoubleValue);
		
		runTest();
		
		compareResults();
	}
	
	@Test
	public void testNegative() {
		int intValue = -5;
		double doubleValue = -5.0;
		
		TestConfiguration config = availableTestConfigurations.get("ZeroTest");
		config.addVariable("int", intValue);
		config.addVariable("double", doubleValue);
		
		loadTestConfiguration("ZeroTest");
		
		double computedIntValue = Math.acos(intValue);
		double computedDoubleValue = Math.acos(doubleValue);
		
		createHelperMatrix();
		writeExpectedHelperMatrix("int", computedIntValue);
		writeExpectedHelperMatrix("double", computedDoubleValue);
		
		runTest();
		
		compareResults();
	}
	
	@Test
	public void testZero() {
		int intValue = 0;
		double doubleValue = 0.0;
		
		TestConfiguration config = availableTestConfigurations.get("NegativeTest");
		config.addVariable("int", intValue);
		config.addVariable("double", doubleValue);
		
		loadTestConfiguration("NegativeTest");
		
		double computedIntValue = Math.acos(intValue);
		double computedDoubleValue = Math.acos(doubleValue);
		
		createHelperMatrix();
		writeExpectedHelperMatrix("int", computedIntValue);
		writeExpectedHelperMatrix("double", computedDoubleValue);
		
		runTest();
		
		compareResults();
	}
	
	@Test
	public void testRandom() {
		int intValue = TestUtils.getRandomInt();
		double doubleValue = TestUtils.getRandomDouble();
		
		TestConfiguration config = availableTestConfigurations.get("RandomTest");
		config.addVariable("int", intValue);
		config.addVariable("double", doubleValue);
		
		loadTestConfiguration("RandomTest");
		
		double computedIntValue = Math.acos(intValue);
		double computedDoubleValue = Math.acos(doubleValue);
		
		createHelperMatrix();
		writeExpectedHelperMatrix("int", computedIntValue);
		writeExpectedHelperMatrix("double", computedDoubleValue);
		
		runTest();
		
		compareResults();
	}
	
}