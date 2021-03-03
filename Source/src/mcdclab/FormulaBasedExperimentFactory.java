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

import java.util.Collection;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;

/**
 * Base class to all factories in this lab.
 *
 * @param <E> The type of experiments produced by the factoy
 */
public abstract class FormulaBasedExperimentFactory<T extends FormulaBasedExperiment> extends ExperimentFactory<MyLaboratory,T>
{
	/**
	 * A provider for formulas
	 */
	protected transient OperatorProvider m_provider;

	public FormulaBasedExperimentFactory(MyLaboratory lab, Class<T> clazz, OperatorProvider provider)
	{
		super(lab, clazz);	
		m_provider = provider;
	}

	@SuppressWarnings("unchecked")
	public T get(Region r, boolean include)
	{
		T exp = null;
		Collection<Experiment> col = m_lab.filterExperiments(r, m_class);
		if (col.isEmpty())
		{
			// Experiment does not exist
			if (include)
			{
				exp = createExperiment(r);
				if (exp != null)
				{
					m_lab.add(exp);
				}
			}
		}
		else
		{
			for (Experiment e : col)
			{
				exp = (T) e;
			}
		}
		return exp;
	}
}
