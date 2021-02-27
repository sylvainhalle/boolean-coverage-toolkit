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

import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;

import static mcdclab.CriterionFusionExperiment.CRITERIA;
import static mcdclab.CriterionFusionExperiment.SIZE_GLOBAL;
import static mcdclab.CriterionFusionExperiment.SIZE_MERGED;
import static mcdclab.CriterionFusionExperiment.TIME_GLOBAL;
import static mcdclab.CriterionFusionExperiment.TIME_MERGED;
import static mcdclab.FormulaBasedExperiment.FORMULA;
import static mcdclab.FormulaBasedExperiment.FORMULA_SIZE;
import static mcdclab.TestGenerationExperiment.CRITERION;
import static mcdclab.TestGenerationExperiment.METHOD;
import static mcdclab.TestGenerationExperiment.SIZE;
import static mcdclab.TestGenerationExperiment.TIME;

/**
 * The main laboratory.
 */
public class MyLaboratory extends Laboratory
{
	@Override
	public void setup()
	{

		boolean include_safecomp = false;
		boolean include_comparison = true;
		
		// Comparison with SAFECOMP 2018
		if (include_safecomp)
		{
			// The factory to generate experiments
			Group g = new Group("Test suite generation experiments");
			add(g);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(METHOD, HittingSetTestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_MCDC, TestSuiteGenerationFactory.C_2WAY, TestSuiteGenerationFactory.C_3WAY);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider);
			big_r.add(FORMULA, op_provider.getNames());
			
			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider);
			
			for (Region c_r : big_r.all(CRITERION))
			{
				String criterion = c_r.getString(CRITERION);
				ExperimentTable et_time = new ExperimentTable(FORMULA, METHOD, TIME);
				et_time.setShowInList(false);
				TransformedTable tt_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_time);
				tt_time.setTitle("Test generation time " + criterion);
				ExperimentTable et_size = new ExperimentTable(FORMULA, METHOD, SIZE);
				et_size.setShowInList(false);
				TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(METHOD, SIZE), et_size);
				tt_size.setTitle("Test suite size " + criterion);
				ExperimentTable et_size_vs_time = new ExperimentTable(SIZE, METHOD, TIME);
				et_size_vs_time.setShowInList(false);
				TransformedTable tt_size_vs_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_size_vs_time);
				tt_size_vs_time.setTitle("Generation time vs. test suite size " + criterion);
				for (Region f_r : c_r.all(FORMULA))
				{
					TestGenerationExperiment e = factory.get(f_r);
					if (e == null)
					{
						continue;
					}
					g.add(e);
					et_time.add(e);
					et_size.add(e);
					et_size_vs_time.add(e);
				}
				add(tt_time, tt_size, tt_size_vs_time);
				Scatterplot p_size_vs_time = new Scatterplot(tt_size_vs_time);
				p_size_vs_time.withLines(false);
				add(p_size_vs_time);				
			}
		}

		// Comparison merged vs. global
		if (include_comparison)
		{
			Group g = new Group("Merged vs. global test suite generation");
			add(g);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(CRITERIA, 
					CriterionFusionExperimentFactory.C_MCDC_PREDICATE, 
					CriterionFusionExperimentFactory.C_MCDC_2WAY,
					CriterionFusionExperimentFactory.C_CLAUSE_2WAY);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider);
			big_r.add(FORMULA, op_provider.getNames());
			
			// The factory to generate experiments
			CriterionFusionExperimentFactory factory = new CriterionFusionExperimentFactory(this, op_provider);

			CriteriaRatioTable crt_size = new CriteriaRatioTable(CRITERIA, "Criteria", SIZE_GLOBAL, SIZE_MERGED, "Size ratio");
			crt_size.setTitle("Size ratio between global and merged");
			CriteriaRatioTable crt_time = new CriteriaRatioTable(CRITERIA, "Criteria", TIME_GLOBAL, TIME_MERGED, "Time ratio");
			crt_time.setTitle("Time ratio between global and merged");
			add(crt_size, crt_time);
			for (Region c_r : big_r.all(CRITERIA))
			{
				String criteria = c_r.getString(CRITERIA);
				ExperimentTable et_size = new ExperimentTable(SIZE_GLOBAL, SIZE_MERGED);
				et_size.setTitle("Size comparison between global and merged " + criteria);
				ExperimentTable et_time = new ExperimentTable(TIME_GLOBAL, TIME_MERGED);
				et_time.setTitle("Time comparison between global and merged " + criteria);
				add(et_size, et_time);
				for (Region f_r : c_r.all(FORMULA))
				{
					CriterionFusionExperiment cfe = factory.get(f_r);
					if (cfe == null)
					{
						continue;
					}
					et_size.add(cfe);
					et_time.add(cfe);
					crt_size.add(cfe);
					crt_time.add(cfe);
					g.add(cfe);
				}
				Scatterplot p_size = new Scatterplot(et_size);
				p_size.setTitle(et_size.getTitle());
				p_size.withLines(false);
				add(p_size);
				Scatterplot p_time = new Scatterplot(et_time);
				p_time.setTitle(et_time.getTitle());
				p_time.withLines(false);
				add(p_time);
			}
		}
	}

	protected static void addFormulas(OperatorProvider provider)
	{
		int num_formulas = 10; // Normally 20, set to a smaller number for tests
		DnfOperatorPicker picker = new DnfOperatorPicker(new RandomInteger(1,10), new RandomInteger(2,20), new RandomFloat(), new RandomBoolean());
		for (int i = 1; i <= num_formulas; i++)
		{
			provider.add("TCAS " + i, TCASBenchmark.getFormula(i));
		}
		for (int i = 1; i < num_formulas; i++)
		{
			provider.add("Random " + i, picker.pick());
		}
	}

	public static void main(String[] args)
	{
		// Nothing else to do here
		MyLaboratory.initialize(args, MyLaboratory.class);
	}
}
