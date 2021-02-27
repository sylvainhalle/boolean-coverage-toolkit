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
package ca.uqac.lif.mcdc;

/**
 * Tree transformation that acts on a specific clause of a formula. This
 * clause is designated by a number corresponding to its relative position in
 * the formula. Obviously, clause-based transformations only make sense for
 * formulas having clauses.
 */
public abstract class ClauseBasedTruncation implements Truncation
{
	/**
	 * The number of the clause to focus on.
	 */
	protected int m_clauseNb;
	
	/**
	 * Creates a new instance of the truncation.
	 * @param clause_nb The number of the clause to focus on
	 */
	public ClauseBasedTruncation(int clause_nb)
	{
		super();
		m_clauseNb = clause_nb;
	}
	
	/**
	 * Calculates the number of clauses in a formula.
	 * @param phi The formula
	 * @return The number of clauses, or 0 if the top-level operator is
	 * neither a conjunction nor a disjunction
	 */
	public static int countClauses(Operator phi)
	{
		int num_clauses = 0;
		if (phi instanceof Conjunction)
		{
			num_clauses = ((Conjunction) phi).m_operands.size();
		}
		else if (phi instanceof Disjunction)
		{
			num_clauses = ((Disjunction) phi).m_operands.size();
		}
		return num_clauses;
	}
}
