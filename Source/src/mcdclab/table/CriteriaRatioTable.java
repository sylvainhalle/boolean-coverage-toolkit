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
package mcdclab.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.labpal.NumberHelper;
import ca.uqac.lif.labpal.provenance.ExperimentValue;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import ca.uqac.lif.petitpoucet.AggregateFunction;
import ca.uqac.lif.petitpoucet.NodeFunction;
import mcdclab.experiment.FormulaBasedExperiment;

/**
 * Table that splits experiments into categories based on one of their
 * parameters, and computes the average ratio between two other values
 * for all experiments of each category.
 */
public class CriteriaRatioTable extends Table
{
	/**
	 * The name of the first parameter to read in each experiment
	 */
	protected String m_parameter1;

	/**
	 * The name of the second parameter to read in each experiment
	 */
	protected String m_parameter2;

	/**
	 * The experiment parameter used to split experiments into categories
	 */
	protected String m_category;

	/**
	 * The caption to give to the "category" column of the table
	 */
	protected String m_categoryCaption;

	/**
	 * The caption to give to the "value" column of the table
	 */
	protected String m_valueCaption;

	/**
	 * The set experiments to read from, split into categories
	 * (the map's key)
	 */
	protected transient Map<String,Set<FormulaBasedExperiment>> m_experiments;
	
	/**
	 * The dependencies for lineage in the second column of the table
	 */
	protected transient List<NodeFunction> m_dependencies;

	/**
	 * Creates a new table.
	 * @param category The experiment parameter used to split experiments
	 * into categories
	 * @param category_caption The caption to give to the "category" column of
	 * the table
	 * @param param1 The name of the first parameter to read in each
	 * experiment
	 * @param param2 The name of the second parameter to read in each
	 * experiment
	 * @param value_caption The caption to give to the "value" column of the table
	 */
	public CriteriaRatioTable(String category, String category_caption, String param1, String param2, String value_caption)
	{
		super();
		m_category = category;
		m_parameter1 = param1;
		m_parameter2 = param2;
		m_categoryCaption = category_caption;
		m_valueCaption = value_caption;
		m_experiments = new HashMap<String,Set<FormulaBasedExperiment>>();
		m_dependencies = new ArrayList<NodeFunction>();
	}

	/**
	 * Adds an experiment to the table
	 * @param e The experiment
	 */
	public void add(FormulaBasedExperiment e)
	{
		String category = e.readString(m_category);
		Set<FormulaBasedExperiment> exps = null;
		if (!m_experiments.containsKey(category))
		{
			exps = new HashSet<FormulaBasedExperiment>();
			m_experiments.put(category, exps);
		}
		else
		{
			exps = m_experiments.get(category);
		}
		exps.add(e);
	}

	@Override
	public TempTable getDataTable(boolean temp)
	{
		TempTable table = new TempTable(getId(), m_categoryCaption, m_valueCaption);
		m_dependencies.clear();
		for (Map.Entry<String,Set<FormulaBasedExperiment>> entry : m_experiments.entrySet())
		{
			String category = entry.getKey();
			Set<NodeFunction> deps = new HashSet<NodeFunction>();
			float ratio = 0, n = 0;
			for (FormulaBasedExperiment e : entry.getValue())
			{
				float v1 = e.readFractional(m_parameter1);
				float v2 = e.readFractional(m_parameter2);
				if (v1 == 0 || v2 == 0)
				{
					continue;
				}
				ratio += v1 / v2;
				n++;
				Set<NodeFunction> q_deps = new HashSet<NodeFunction>(2);
				q_deps.add(new ExperimentValue(e, m_parameter1));
				q_deps.add(new ExperimentValue(e, m_parameter2));
				AggregateFunction af = new AggregateFunction("Quotient", q_deps);
				deps.add(af);
			}
			if (n > 0)
			{
				TableEntry te = new TableEntry();
				te.put(m_categoryCaption, category);
				te.put(m_valueCaption, NumberHelper.roundToSignificantFigures(ratio / n, 3));
				AggregateFunction af = new AggregateFunction("Average", deps);
				te.addDependency(m_valueCaption, af);
				m_dependencies.add(af);
				table.add(te);
			}
		}
		return table;
	}

	@Override
	protected TempTable getDataTable(boolean arg0, String... arg1) 
	{
		throw new UnsupportedOperationException("Column ordering not supported for this table");
	}

	@Override
	public NodeFunction getDependency(int line, int col) 
	{
		if (col != 1 || line >= m_dependencies.size())
		{
			return null;
		}
		return m_dependencies.get(line);
	}
}
