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

import ca.uqac.lif.mcdc.Operator;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;

/**
 * Produces 20 formulas from a study of Boolean conditions in avionics
 * software. The formulas are a sample taken from Appendix C of:
 * <blockquote>
 * J.J. Chilenski. <i>An Investigation of Three Forms of the Modified Condition
 * Decision Coverage (MCDC) Criterion</i>. Technical Report DOT/FAA/AR-01/18,
 * April 2001.
 * </blockquote>
 * In each formula, the unique conditions appearing in the original expressions
 * have been replaced by atomic variables starting from letter <i>a</i>. 
 */
public class FaaBenchmark extends FormulaBenchmark
{
	@Override
	public Operator getFormula(int id)
	{
		switch (id)
		{
		case 1:
			return getFormula1();
		case 2:
			return getFormula2();
		case 3:
			return getFormula3();
		case 4:
			return getFormula4();
		case 5:
			return getFormula5();
		case 6:
			return getFormula6();
		case 7:
			return getFormula7();
		case 8:
			return getFormula8();
		case 9:
			return getFormula9();
		case 10:
			return getFormula10();
		case 11:
			return getFormula11();
		case 12:
			return getFormula12();
		case 13:
			return getFormula13();
		case 14:
			return getFormula14();
		case 15:
			return getFormula15();
		case 16:
			return getFormula16();
		case 17:
			return getFormula17();
		case 18:
			return getFormula18();
		case 19:
			return getFormula19();
		case 20:
			return getFormula20();
		default:
			return null;
		}
	}

	protected static Operator getFormula1()
	{
		return Or(And(a, b, f, g), And(c, b, f, g), And(d, b, f, g), And(e, b, f, g));
	}

	protected static Operator getFormula2()
	{
		return And(Not(a), Not(b), c);
	}

	protected static Operator getFormula3()
	{
		return Or(And(a, b), And(a, c), And(b, c));
	}

	protected static Operator getFormula4()
	{
		return And(Or(a, b), c, d);
	}

	protected static Operator getFormula5()
	{
		return Or(Or(Not(a), Not(b), d), Or(Not(c), e));
	}

	protected static Operator getFormula6()
	{
		return And(b, Not(Or(c, d, e, f, g, a)));
	}

	protected static Operator getFormula7()
	{
		return And(a, h, b, c, d, Not(e), Not(f), Not(g));
	}

	protected static Operator getFormula8()
	{
		return Or(a, b, c, d, And(Not(h), e), And(f, g));
	}

	protected static Operator getFormula9()
	{
		return And(Or(And(a, Not(b)), And(c, d)), Not(e), Not(f), Not(g), Not(h), Not(i));
	}

	protected static Operator getFormula10()
	{
		return Or(And(a, Or(b, c)), And(d, Or(e, f)), And(g, Or(h, i)));
	}
	
	protected static Operator getFormula11()
	{
		return And(a, Or(And(Not(b), Not(c), Not(d), Not(e)), And(Not(f), Not(g), Not(h), Not(i))));
	}

	protected static Operator getFormula12()
	{
		return And(Or(And(a, Not(b)), And(c, d)), Not(e), Not(f), Not(g), Not(h), Not(i), Not(j));
	}

	protected static Operator getFormula13()
	{
		return Or(And(Or(a, b), c, Not(d)), And(e, f, g, h, i, j));
	}

	protected static Operator getFormula14()
	{
		return Or(a, And(b, c), And(d, e), And(f, g), And(h, i), j);
	}

	protected static Operator getFormula15()
	{
		return Or(a, b, f, And(Or(And(g, h), And(i, j)), Not(Or(c, d, e))));
	}

	protected static Operator getFormula16()
	{
		return And(Or(And(Or(And(a, Not(b)), And(j, Not(Or(c, d, k, e)))), f), g), Not(Or(h, i)));
	}

	protected static Operator getFormula17()
	{
		return And(Or(And(a, Not(b)), And(c, d)), Not(e), Not(f), Not(g), Not(h), Not(i), Not(j), Not(k));
	}

	protected static Operator getFormula18()
	{
		return And(Or(And(Or(a, b, c, d, e, f), Not(g)), And(h, i)), Not(j), Not(k), Not(l));
	}

	protected static Operator getFormula19()
	{
		return And(Or(And(a, Not(b)), And(c, d)), Not(e), Not(f), Not(g), Not(h), Not(i), Not(j), Not(k), Not(l));
	}

	protected static Operator getFormula20()
	{
		return Or(And(a, b), And(c, d), And(e, f), And(g, h), And(i, j), And(k, l), m);
	}
}
