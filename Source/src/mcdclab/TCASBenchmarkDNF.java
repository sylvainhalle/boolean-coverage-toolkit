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

import ca.uqac.lif.mcdc.Operator;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;

/**
 * TCAS Boolean Predicates in Minimal DNF. The formulas are as taken from
 * Appendix B of Kaminski and Amman, ICST 2009.
 *
 */
public class TCASBenchmarkDNF extends FormulaBenchmark
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
		return Or(And(a, Not(b), d, Not(e), Not(h), Not(f)), And(a, Not(b), Not(d), e, Not(h), Not(f)), And(a, Not(b), c, d, Not(e), Not(f)), And(a, Not(b), c, Not(d), e, Not(f)), And(Not(a), b, Not(d), e, Not(f)));
	}
	
	protected static Operator getFormula2()
	{
		return Or(And(a, Not(b), c, Not(d), Not(e), Not(g), h, Not(i), Not(f)), And(a, Not(b), Not(d), Not(e), Not(g), Not(h), Not(i), f), And(a, Not(b), Not(c), Not(e), Not(g), Not(h), Not(i), f), And(a, Not(b), Not(c), Not(d), Not(g), Not(h), Not(i), f), And(a, Not(b), c, Not(d), Not(e), g, Not(h), Not(f)), And(a, Not(b), c, Not(d), Not(e), Not(h), i, Not(f)), And(a, Not(b), Not(c), d, Not(e), g, Not(h), Not(f)), And(a, Not(b), Not(c), d, Not(e), Not(h), i, Not(f)), And(a, Not(b), Not(c), Not(d), e, g, Not(h), Not(f)), And(a, Not(b), Not(c), Not(d), e, Not(h), i, Not(f)), And(Not(a), b, c, Not(d), Not(e), Not(h), i, Not(f)), And(Not(a), b, Not(c), d, Not(e), Not(h), i, Not(f)), And(Not(a), b, Not(c), Not(d), e, Not(h), i, Not(f)));
	}

	protected static Operator getFormula3()
	{
		return Or(And(Not(a), Not(b), c, Not(g), Not(i), Not(k), Not(m)), And(Not(a), Not(b), c, g, Not(h), Not(l), Not(m)), And(Not(a), Not(b), c, Not(g), Not(h), i, Not(m)), And(Not(a), Not(b), c, g, i, Not(l), Not(m)), And(Not(a), Not(b), c, g, i, Not(k), Not(m)), And(Not(a), Not(b), c, Not(h), Not(k), Not(m)), And(Not(a), b, Not(c), Not(g), Not(i), Not(k)), And(a, Not(b), Not(c), Not(g), Not(i), Not(k)), And(Not(a), Not(b), c, Not(i), Not(k), f), And(Not(a), b, Not(c), Not(g), Not(h), i), And(Not(a), b, Not(c), g, Not(h), Not(l)), And(a, Not(b), Not(c), Not(g), Not(h), i), And(a, Not(b), Not(c), g, Not(h), Not(l)), And(Not(a), Not(b), c, Not(h), i, f), And(Not(a), b, Not(c), g, i, Not(k)), And(Not(a), b, Not(c), g, i, Not(l)), And(a, Not(b), Not(c), g, i, Not(k)), And(a, Not(b), Not(c), g, i, Not(l)), And(a, Not(b), Not(c), Not(h), Not(k)), And(Not(a), b, Not(c), Not(h), Not(k)), And(a, Not(b), Not(c), g, f), And(Not(a), b, Not(c), g, f), And(Not(a), Not(b), c, g, f), And(a, Not(b), Not(c), Not(d)), And(a, Not(b), Not(c), Not(e)));
	}
	
	protected static Operator getFormula4()
	{
		return Or(And(a, Not(b), d), And(a, Not(c), d), And(e));
	}
	
	protected static Operator getFormula5()
	{
		return Or(And(a, Not(g), Not(i), Not(k)), And(a, g, Not(h), Not(l)), And(a, Not(g), Not(h), i), And(a, g, i, Not(l)), And(a, g, i, Not(k)), And(a, Not(h), Not(k)), And(a, Not(c)), And(a, Not(b)), And(f));
	}
	
	protected static Operator getFormula6()
	{
		return Or(And(Not(a), b, Not(c), d, e, g, Not(h), i, j, Not(k), Not(f)), And(a, Not(b), c, Not(d), e, g, Not(h), i, j, Not(k), Not(f)), And(Not(a), b, Not(c), d, e, Not(g), Not(h), Not(j), f), And(Not(a), b, Not(c), d, e, Not(g), Not(h), Not(k), f), And(a, Not(b), c, Not(d), e, Not(g), Not(h), Not(j), f), And(a, Not(b), c, Not(d), e, Not(g), Not(h), Not(k), f));
	}
	
	protected static Operator getFormula7()
	{
		return Or(And(Not(a), b, Not(c), d, e, Not(g), Not(i), Not(j)), And(Not(a), b, Not(c), d, e, Not(h), Not(i), Not(k)), And(a, Not(b), c, Not(d), e, Not(g), Not(i), Not(j)), And(a, Not(b), c, Not(d), e, Not(h), Not(i), Not(k)), And(a, Not(b), c, Not(d), e, Not(g), Not(k)), And(a, Not(b), c, Not(d), e, Not(h), Not(j)), And(Not(a), b, Not(c), d, e, Not(g), Not(k)), And(Not(a), b, Not(c), d, e, Not(h), Not(j)));
	}
	
	protected static Operator getFormula8()
	{
		return Or(And(Not(a), b, Not(c), d, e, Not(g), h, Not(f)), And(a, Not(b), c, Not(d), e, Not(g), h, Not(f)), And(Not(a), b, Not(c), d, e, g, Not(h), f), And(a, Not(b), c, Not(d), e, g, Not(h), f));
	}
	
	protected static Operator getFormula9()
	{
		return Or(And(Not(a), Not(b), Not(c), d, Not(e), Not(g), f), And(Not(a), b, c, Not(d), Not(e), Not(g), f));
	}
	
	protected static Operator getFormula10()
	{
		return Or(And(a, Not(b), Not(c), d, Not(e), g, Not(j), Not(l), Not(m), f), And(a, Not(b), Not(c), d, Not(e), h, Not(j), Not(l), Not(m), f), And(a, Not(b), Not(c), d, Not(e), i, Not(j), Not(l), Not(m), f), And(a, Not(b), Not(c), d, Not(e), g, j, Not(k), Not(m), f), And(a, Not(b), Not(c), d, Not(e), h, j, Not(k), Not(m), f), And(a, Not(b), Not(c), d, Not(e), i, j, Not(k), Not(m), f));
	}
	
	protected static Operator getFormula11()
	{
		return Or(And(a, Not(b), Not(c), Not(g), Not(h), Not(i), Not(j), Not(l)), And(a, Not(b), Not(c), Not(g), Not(h), Not(i), j, Not(k)), And(a, Not(b), Not(c), Not(g), Not(h), Not(i), Not(j), m), And(a, Not(b), Not(c), Not(d), Not(e), Not(j), Not(l)), And(a, Not(b), Not(c), Not(d), Not(e), Not(j), m), And(a, Not(b), Not(c), Not(d), Not(e), j, Not(k)), And(a, Not(b), Not(c), Not(j), Not(l), Not(f)), And(a, Not(b), Not(c), j, Not(k), Not(f)), And(a, Not(b), Not(c), Not(j), m, Not(f)));
	}
	
	protected static Operator getFormula12()
	{
		return null;
	}
	
	protected static Operator getFormula13()
	{
		return Or(And(a), And(b), And(c), And(Not(d), e, f, Not(g), Not(h)), And(i, j, Not(l)), And(i, k, Not(l)));
	}
	
	protected static Operator getFormula14()
	{
		return Or(And(a, e, Not(h)), And(a, d, Not(h)), And(a, c, e), And(a, c, d), And(b, e), And(b, f));
	}
	
	protected static Operator getFormula15()
	{
		return Or(And(b, e, i), And(b, d, i), And(b, c, i), And(a, e, i), And(a, e, g), And(a, d, i), And(a, d, g), And(a, c, i), And(a, c, h), And(a, c, g), And(a, f));
	}
	
	protected static Operator getFormula16()
	{
		return Or(And(c, Not(g), Not(i), Not(k), Not(m)), And(c, g, Not(h), Not(l), Not(m)), And(c, Not(g), Not(h), i, Not(m)), And(c, g, i, Not(l), Not(m)), And(c, g, i, Not(k), Not(m)), And(c, Not(h), Not(k), Not(m)), And(b, Not(g), Not(i), Not(k)), And(a, Not(g), Not(i), Not(k)), And(b, Not(g), Not(h), i), And(b, g, Not(h), Not(l)), And(a, Not(g), Not(h), i), And(a, g, Not(h), Not(l)), And(b, g, i, Not(k)), And(b, g, i, Not(l)), And(a, g, i, Not(k)), And(a, g, i, Not(l)), And(a, Not(h), Not(k)), And(b, Not(h), Not(k)), And(Not(i), Not(k), f), And(Not(h), i, f), And(g, f), And(a, Not(e)), And(a, Not(d)));
	}
	
	protected static Operator getFormula17()
	{
		return Or(And(a, c, e, g, i, j), And(a, c, e, h, i, k), And(b, d, e, g, i, j), And(b, d, e, h, i, k), And(a, c, e, f), And(b, d, e, f));
	}
	
	protected static Operator getFormula18()
	{
		return Or(And(a, c, e, Not(j), Not(k)), And(a, c, e, Not(h), Not(j)), And(a, c, e, Not(g), Not(k)), And(b, d, e, Not(j), Not(k)), And(b, d, e, Not(h), Not(j)), And(b, d, e, Not(g), Not(k)), And(b, d, e, Not(i)), And(a, c, e, Not(i)));
	}
	
	protected static Operator getFormula19()
	{
		return Or(And(a, c, e, h, Not(f)), And(b, d, e, h, Not(f)), And(a, c, e, g, f), And(b, d, e, g, f));
	}
	
	protected static Operator getFormula20()
	{
		return Or(And(Not(a), Not(b), d, Not(e), Not(g), f), And(Not(a), b, c, Not(e), Not(g), f));
	}
	
}
