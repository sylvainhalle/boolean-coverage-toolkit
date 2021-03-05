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

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.table.ExperimentTable;

/**
 * An {@link ExperimentTable} that only accepts
 * {@link HittingSetTestGenerationExperiment}s.
 */
public class HittingSetExperimentTable extends ExperimentTable
{
	public HittingSetExperimentTable(String ... params)
	{
		super(params);
	}
	
	@Override
	public ExperimentTable add(Experiment e)
	{
		if (e instanceof HittingSetTestGenerationExperiment)
		{
			return super.add(e);
		}
		return this;
	}
}
