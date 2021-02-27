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

import ca.uqac.lif.labpal.ExperimentFactory;

/**
 * Base class to all factories in this lab.
 *
 * @param <E> The type of experiments produced by the factoy
 */
public abstract class FormulaBasedExperimentFactory<E extends FormulaBasedExperiment> extends ExperimentFactory<MyLaboratory,E>
{
	/**
	 * A provider for formulas
	 */
	protected transient OperatorProvider m_provider;
	
	public FormulaBasedExperimentFactory(MyLaboratory lab, Class<E> clazz, OperatorProvider provider)
	{
		super(lab, clazz);	
		m_provider = provider;
	}
}
