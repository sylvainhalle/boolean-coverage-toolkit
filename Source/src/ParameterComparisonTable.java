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
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TempTable;
import ca.uqac.lif.petitpoucet.NodeFunction;

/**
 * Table that creates (x,y) points out of two parameters read
 */
public class ParameterComparisonTable extends Table
{
	/**
	 * The experiments contained in this table.
	 */
	protected List<Experiment> m_experiments;
	
	/**
	 * The first parameter to read from each experiment.
	 */
	protected String m_firstParam;
	
	/**
	 * The second parameter to read from each experiment.
	 */
	protected String m_secondParam;
	
	public ParameterComparisonTable(String first_param, String second_param)
	{
		super();
		m_experiments = new ArrayList<Experiment>();
		m_firstParam = first_param;
		m_secondParam = second_param;
	}
	
	/**
	 * Adds a new experiment to this table
	 * @param e
	 */
	public void add(Experiment e)
	{
		if (!m_experiments.contains(e))
		{
			m_experiments.add(e);
		}
	}
	
	@Override
	public TempTable getDataTable(boolean temp)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TempTable getDataTable(boolean temp, String... arg1)
	{
		// Ignore column ordering
		return getDataTable(temp);
	}

	@Override
	public NodeFunction getDependency(int arg0, int arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
