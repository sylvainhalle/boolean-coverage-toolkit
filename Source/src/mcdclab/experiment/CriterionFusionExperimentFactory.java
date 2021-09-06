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
package mcdclab.experiment;

import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;
import mcdclab.MyLaboratory;
import mcdclab.OperatorProvider;

import static mcdclab.experiment.CriterionFusionExperiment.FORMULA;
import static mcdclab.experiment.CriterionFusionExperiment.CRITERIA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates experiments that compare test suite generation for separate
 * conditions to test suite generation for all conditions at once.
 */
public class CriterionFusionExperimentFactory extends FormulaBasedExperimentFactory<CriterionFusionExperiment>
{
	/**
	 * String representing the combined criterion "clause + 2-way coverage".
	 */
	public static final String C_CLAUSE_2WAY = TestSuiteGenerationFactory.C_CLAUSE + "," + TestSuiteGenerationFactory.C_2WAY;
	
	/**
	 * String representing the combined criterion "clause + 3-way coverage".
	 */
	public static final String C_CLAUSE_3WAY = TestSuiteGenerationFactory.C_CLAUSE + "," + TestSuiteGenerationFactory.C_3WAY;
	
	/**
	 * String representing the combined criterion "MC/DC + predicate coverage".
	 */
	public static final String C_MCDC_PREDICATE = TestSuiteGenerationFactory.C_MCDC + "," + TestSuiteGenerationFactory.C_PREDICATE;
	
	/**
	 * String representing the combined criterion "MC/DC + clause coverage".
	 */
	public static final String C_MCDC_CLAUSE = TestSuiteGenerationFactory.C_MCDC + "," + TestSuiteGenerationFactory.C_CLAUSE;
	
	/**
	 * String representing the combined criterion "MC/DC + 2-way".
	 */
	public static final String C_MCDC_2WAY = TestSuiteGenerationFactory.C_MCDC + "," + TestSuiteGenerationFactory.C_2WAY;
	
	/**
	 * String representing the combined criterion "MC/DC + 3-way".
	 */
	public static final String C_MCDC_3WAY = TestSuiteGenerationFactory.C_MCDC + "," + TestSuiteGenerationFactory.C_3WAY;
	
	/**
	 * String representing the combined criterion "MC/DC + MUMCUT".
	 */
	public static final String C_MCDC_MUMCUT = TestSuiteGenerationFactory.C_MCDC + "," + TestSuiteGenerationFactory.C_MUMCUT;
	
	/**
	 * Creates a new experiment factory.
	 * @param lab The lab to which the experiments will be added
	 * @param provider A provider for formulas 
	 */
	public CriterionFusionExperimentFactory(MyLaboratory lab, OperatorProvider provider)
	{
		super(lab, CriterionFusionExperiment.class, provider);
	}
	
	@Override
	protected CriterionFusionExperiment createExperiment(Region r)
	{
		String formula_name = r.getString(FORMULA);
		Operator op = m_provider.getFormula(r.getString(FORMULA));
		if (op == null)
		{
			return null;
		}
		return getCriterionFusionExperiment(op, formula_name, r.getString(CRITERIA));
	}
	
	protected static CriterionFusionExperiment getCriterionFusionExperiment(Operator formula, String formula_name, String criteria)
	{
		Set<Set<Truncation>> truncation_sets = new HashSet<Set<Truncation>>();
		String[] a_criteria = criteria.split(",");
		List<String> criteria_names = new ArrayList<String>(a_criteria.length);
		for (String criterion : a_criteria)
		{
			criteria_names.add(criterion.trim());
			truncation_sets.add(TestSuiteGenerationFactory.getTruncations(formula, criterion));
		}
		return new CriterionFusionExperiment(formula, formula_name, criteria_names, truncation_sets);
	}
}
