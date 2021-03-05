/*
    Graph-based MC/DC testing
    Copyright (C) 2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mcdclab;

import ca.uqac.lif.mcdc.Operator;

/**
 * Experiment that computes a test suite based on a technique and a
 * particular coverage criterion.
 */
public abstract class TestGenerationExperiment extends FormulaBasedExperiment
{
	/**
	 * Name of parameter "Method".
	 */
	public static final transient String METHOD = "Method";
	
	/**
	 * Name of parameter "Criterion".
	 */
	public static final transient String CRITERION = "Criterion";
	
	/**
	 * Name of parameter "Size".
	 */
	public static final transient String SIZE = "Size";
	
	/**
	 * Name of parameter "Time".
	 */
	public static final transient String TIME = "Time";
	
	/**
	 * Name of parameter "Coverage".
	 */
	public static final transient String COVERAGE = "Coverage";
	
	/**
	 * Name of parameter "Exhaustive size".
	 */
	public static final transient String SIZE_EXHAUSTIVE = "Exhaustive size"; 
	
	/**
	 * Creates a new experiment.
	 * @param formula The formula to generate coverage for
	 * @param formula_name A name given to the formula
	 */
	public TestGenerationExperiment(Operator formula, String formula_name)
	{
		this(formula, formula_name, Status.PREREQ_OK);
	}
	
	/**
	 * Creates a new experiment.
	 * @param formula The formula to generate coverage for
	 * @param formula_name A name given to the formula
	 * @param status The running status to give this experiment at creation time
	 */
	protected TestGenerationExperiment(Operator formula, String formula_name, Status status)
	{
		super(formula, formula_name, status);
		describe(METHOD, "The method used to generate test suites");
		describe(CRITERION, "The method used to generate test suites");
		describe(SIZE, "The size of the generated test suite");
		describe(TIME, "The time (in ms) taken to generate the test suite");
		describe(COVERAGE, "The coverage achieved by the generated test suite");
		describe(SIZE_EXHAUSTIVE, "The size that an exhaustive test suite would have for the same formula");
		write(SIZE_EXHAUSTIVE, Math.pow(2, formula.getVariables().size()));
	}	
}
