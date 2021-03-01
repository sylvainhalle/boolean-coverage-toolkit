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

import ca.uqac.lif.mcdc.Valuation;
import ca.uqac.lif.synthia.Picker;

public class ValuationPicker implements Picker<Valuation> 
{
	protected String[] m_variables;
	
	protected Picker<Boolean> m_booleanPicker;
	
	public ValuationPicker(Picker<Boolean> boolean_picker, String ... variables)
	{
		super();
		m_booleanPicker = boolean_picker;
		m_variables = variables;
	}
	
	@Override
	public ValuationPicker duplicate(boolean with_state)
	{
		return new ValuationPicker(m_booleanPicker.duplicate(with_state), m_variables);
	}

	@Override
	public Valuation pick()
	{
		Valuation v = new Valuation();
		for (String s : m_variables)
		{
			v.set(s, m_booleanPicker.pick());
		}
		return v;
	}

	@Override
	public void reset() 
	{
		m_booleanPicker.reset();
	}
}
