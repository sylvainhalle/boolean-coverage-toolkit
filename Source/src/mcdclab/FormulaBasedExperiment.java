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

import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.mcdc.Operator;

public abstract class FormulaBasedExperiment extends Experiment
{
	/**
	 * Name of parameter "Formula".
	 */
	public static final transient String FORMULA = "Formula";
	
	/**
	 * Name of parameter "Formula size".
	 */
	public static final transient String FORMULA_SIZE = "Formula size";
	
	/**
	 * Name of parameter "Number of variables".
	 */
	public static final transient String NUM_VARS = "Number of variables";
	
	/**
	 * The formula to generate coverage for.
	 */
	protected transient Operator m_formula;
	
	/**
	 * Creates a new experiment.
	 * @param formula The formula to generate coverage for
	 * @param formula_name A name given to the formula
	 * @param status The running status to give this experiment at creation time
	 */
	public FormulaBasedExperiment(Operator formula, String formula_name, Status status)
	{
		super(status);
		describe(FORMULA, "The formula to generate coverage for");
		describe(FORMULA_SIZE, "The size of the formula to generate coverage for");
		describe(NUM_VARS, "The number of variables in the formula");
		setInput(FORMULA, formula_name);
		setInput(FORMULA_SIZE, formula.getSize());
		setInput(NUM_VARS, formula.getVariables().size());
		setDescription(formula.toString());
		m_formula = formula;
	}
	
	/**
	 * Creates a new experiment.
	 * @param formula The formula to generate coverage for
	 * @param formula_name A name given to the formula
	 */
	public FormulaBasedExperiment(Operator formula, String formula_name)
	{
		this(formula, formula_name, Status.PREREQ_OK);
	}
	
	@Override
	public int countDataPoints()
	{
		return 3;
	}
	
	/**
	 * Reads a float from an experiment parameter. This convenience method
	 * circumvents a known bug in {@link Experiment#readFloat(String)} that
	 * makes it always return an integer instead of the expected float.
	 * @param parameter The parameter to read from
	 * @return The float value
	 */
	public float readFractional(String parameter)
	{
		JsonElement j = read(parameter);
		if (!(j instanceof JsonNumber))
		{
			return 0;
		}
		return ((JsonNumber) j).numberValue().floatValue();
	}
}
