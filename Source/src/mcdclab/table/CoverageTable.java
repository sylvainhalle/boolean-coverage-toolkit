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
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import ca.uqac.lif.petitpoucet.NodeFunction;
import mcdclab.experiment.FormulaBasedExperiment;

public class CoverageTable extends Table
{
	/**
	 * The experiment parameter used to create groups
	 */
	protected transient String m_groupBy;
	
	/**
	 * The parameter to read in each experiment
	 */
	protected transient String m_parameter;
	
	/**
	 * A map grouping experiments according to a parameter
	 */
	protected transient Map<String,Set<FormulaBasedExperiment>> m_experiments;
	
	/**
	 * A list keeping track of all experiment groups present in the table.
	 * The list is used to enumerate these groups always in the same order.
	 */
	protected List<String> m_groups;
	
	public CoverageTable(String group_by, String parameter)
	{
		super();
		m_groupBy = group_by;
		m_parameter = parameter;
		m_experiments = new HashMap<String,Set<FormulaBasedExperiment>>();
		m_groups = new ArrayList<String>();
	}
	
	/**
	 * Adds an experiment to the table.
	 * @param e The experiment to add
	 */
	public void add(FormulaBasedExperiment e)
	{
		String group = e.readString(m_groupBy);
		Set<FormulaBasedExperiment> exps = null;
		if (m_experiments.containsKey(group))
		{
			exps = m_experiments.get(group);
		}
		else
		{
			exps = new HashSet<FormulaBasedExperiment>();
			m_groups.add(group);
		}
		exps.add(e);
		m_experiments.put(group, exps);
	}
	
	@Override
	protected TempTable getDataTable(boolean temp, String ... columns)
	{
		// Ignore columns
		return getDataTable(temp);
	}

	@Override
	public TempTable getDataTable(boolean temp)
	{
		TempTable tt = new TempTable(getId(), m_groupBy, m_parameter);
		for (String g : m_groups)
		{
			Set<FormulaBasedExperiment> exps = m_experiments.get(g);
			float total = 0, n = 0;
			for (FormulaBasedExperiment e : exps)
			{
				total += e.readFractional(m_parameter);
				n++;
			}
			TableEntry te = new TableEntry();
			te.put(m_groupBy, g);
			if (n > 0)
			{
				te.put(m_parameter, NumberHelper.roundToSignificantFigures(total / n, 3));				
			}
			tt.add(te);
		}
		return tt;
	}

	@Override
	public NodeFunction getDependency(int paramInt1, int paramInt2)
	{
		// Not supported
		return null;
	}
	
	@Override
	public CoverageTable duplicate(boolean with_state)
	{
		throw new UnsupportedOperationException("Cannot duplicate this table");
	}
}