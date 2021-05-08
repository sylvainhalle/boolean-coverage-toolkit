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
package mcdclab.macro;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.NumberHelper;
import ca.uqac.lif.labpal.macro.MacroScalar;
import mcdclab.experiment.FormulaBasedExperiment;
import mcdclab.experiment.TestGenerationExperiment;

/**
 * Computes the average size ratio compared to an exhaustive test suite, for a
 * set of experiments. For experiments using MUMCUT coverage, this is the same
 * calculation as the last line of column "Percent" in Table 1 of Chen et al.,
 * APSEC 1999.
 */
public class ExhaustivePercentageMacro extends MacroScalar
{
	/**
	 * The list of experiments associated to this macro
	 */
	protected transient List<FormulaBasedExperiment> m_experiments;
	
	/**
	 * Creates a new instance of the macro.
	 * @param lab The lab to which the macro is associated
	 * @param name The name of the macro
	 * @param method The name of the test generation method common to all the
	 * experiments that are added to this macro
	 */
	public ExhaustivePercentageMacro(Laboratory lab, String name, String method)
	{
		super(lab, name, "Computes the total size ratio compared to an exhaustive test suite, for method " + method);
		m_experiments = new ArrayList<FormulaBasedExperiment>();
	}
	
	/**
	 * Adds an experiment to this macro.
	 * @param e The experiment to add
	 */
	public void add(FormulaBasedExperiment e)
	{
		m_experiments.add(e);
	}
	
	@Override
	public JsonNumber getValue()
	{
		float ratio = 0, total = 0;
		for (FormulaBasedExperiment e : m_experiments)
		{
			ratio += e.readFractional(TestGenerationExperiment.SIZE) / e.readFractional(TestGenerationExperiment.SIZE_EXHAUSTIVE);
			total++;
		}
		if (total > 0)
		{
			return new JsonNumber(NumberHelper.roundToSignificantFigures(ratio /total, 2) * 100);
		}
		return new JsonNumber(0);
	}
	
}
