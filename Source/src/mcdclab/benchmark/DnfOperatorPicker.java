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
package mcdclab.benchmark;

import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.mcdc.Conjunction;
import ca.uqac.lif.mcdc.Disjunction;
import ca.uqac.lif.mcdc.Negation;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.synthia.Picker;

/**
 * Generates random Boolean formulas in disjunctive normal form (DNF).
 */
public class DnfOperatorPicker implements Picker<Operator>
{
	/**
	 * A picker that selects the number of variables
	 */
	protected Picker<Integer> m_numVariables;
	
	/**
	 * A picker that selects the number of clauses
	 */
	protected Picker<Integer> m_numClauses;
	
	/**
	 * A picker that selects the number of variables per clause. This picker
	 * returns a float between 0 (no variables) and 1 (all variables).
	 */
	protected Picker<Float> m_clauseSize;
	
	/**
	 * A picker that determines if a variable should be negated
	 */
	protected Picker<Boolean> m_negation;
	
	/**
	 * Creates a new DNF operator picker.
	 * @param num_variables A picker that selects the number of variables
	 * @param num_clauses A picker that selects the number of clauses
	 * @param clause_size A picker that selects the number of variables per
	 * clause. This picker returns a float between 0 (no variables) and 1
	 * (all variables).
	 * @param negation A picker that determines if a variable should be negated
	 */
	public DnfOperatorPicker(Picker<Integer> num_variables, Picker<Integer> num_clauses, Picker<Float> clause_size, Picker<Boolean> negation)
	{
		super();
		m_numVariables = num_variables;
		m_numClauses = num_clauses;
		m_clauseSize = clause_size;
		m_negation = negation;
	}
	
	@Override
	public DnfOperatorPicker duplicate(boolean with_state) 
	{
		return new DnfOperatorPicker(m_numVariables.duplicate(with_state), 
				m_numClauses.duplicate(with_state), m_clauseSize.duplicate(with_state), 
				m_negation.duplicate(with_state));
	}

	@Override
	public Operator pick() 
	{
		int num_vars = m_numVariables.pick();
		AtomPicker atom = new AtomPicker(num_vars);
		int num_clauses = m_numClauses.pick();
		Operator[] clauses = new Operator[num_clauses];
		for (int n_c = 0; n_c < num_clauses; n_c++)
		{
			int vars_to_pick = (int) Math.ceil((float) num_vars * m_clauseSize.pick());
			Operator[] terms = new Operator[vars_to_pick];
			for (int n_v = 0; n_v < vars_to_pick; n_v++)
			{
				Atom a = atom.pick();
				boolean negated = m_negation.pick();
				if (negated)
				{
					Operator neg = new Negation(a);
					terms[n_v] = neg;
				}
				else
				{
					terms[n_v] = a;
				}
			}
			clauses[n_c] = new Conjunction(terms);
			atom.reset();
		}
		return new Disjunction(clauses);
	}

	@Override
	public void reset() 
	{
		m_numVariables.reset();
		m_numClauses.reset();
		m_clauseSize.reset();
		m_negation.reset();
	}
}
