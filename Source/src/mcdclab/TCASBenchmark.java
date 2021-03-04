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

import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.mcdc.Operator;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;

public class TCASBenchmark 
{
	protected static final Atom a = new Atom("a");
	protected static final Atom b = new Atom("b");
	protected static final Atom c = new Atom("c");
	protected static final Atom d = new Atom("d");
	protected static final Atom e = new Atom("e");
	protected static final Atom f = new Atom("f");
	protected static final Atom g = new Atom("g");
	protected static final Atom h = new Atom("h");
	protected static final Atom i = new Atom("i");
	protected static final Atom j = new Atom("j");
	protected static final Atom k = new Atom("k");
	protected static final Atom l = new Atom("l");
	protected static final Atom m = new Atom("m");
	protected static final Atom n = new Atom("n");

	public static Operator getFormula(int id)
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

	protected static Operator getFormula4()
	{
		return Or(And(a, Or(Not(b), Not(c)), d), e);
	}

	protected static Operator getFormula1()
	{
		return And(
				Not(And(a, b)),
				Or(
						And(d, Not(e), Not(f)),
						And(Not(d), e, Not(f)),
						And(Not(d), Not(e), Not(f))
						),
				Or(
						And(a, c, Or(d, e), h),
						And(a, Or(d, e), Not(h)),
						And(b, Or(e, f))
						)
				);
	}

	protected static Operator getFormula9()
	{
		return And(
				Not(And(c, d)),
				And(Not(e), f, Not(g), Not(a), Or(And(b, c), And(Not(b), d)))
				);
	}

	protected static Operator getFormula14()
	{
		return Or(
				And(a, c, Or(d, e), h),
				And(a, Or(d, e), Not(h)),
				And(b, Or(e, f))
				);
	}

	protected static Operator getFormula20()
	{
		return And(Not(e), f, Not(g), Not(a), Or(And(b, c), And(Not(b), d)));
	}

	/**
	 * (!ab+a!b)!(cd)!(gh) ((ac+bd)e(fg+!fh))
	 * @return
	 */
	protected static Operator getFormula8()
	{
		return And(
				Or(And(Not(a), b), And(a, Not(b))),
				Not(And(c, d)),
				Not(And(g, h)),
				And(
						Or(And(a, c), And(b, d)),
						e,
						Or(And(f, g), And(Not(f), h))
						)
				);
	}
	
	/**
	 * 
	 * @return
	 */
	protected static Operator getFormula7()
	{
		return And(
				Or(And(Not(a), b), And(a, Not(b))),
				Not(And(c, d)),
				Not(And(g, h)),
				Not(And(j, k)),
				And(
						Or(And(a, c), And(b, d)),
						e,
						Or(Not(i), And(Not(g), Not(k)), And(j, Or(Not(h), Not(k))))
						)
				);
	}

	/**
	 * (ac+bd)e(fg+!fh)
	 * @return
	 */
	protected static Operator getFormula19()
	{
		return And(
				Or(And(a, c), And(b, d)),
				e,
				Or(And(f, h), And(Not(f), h))
				);
	}

	/**
	 * (a((c+d+e)g+af+c(f+g+h+i)) + (a+b)(c+d+e)i)  !(ab)!(cd)!(ce)!(de)!(fg)!(fh)!(fi)!(gh)!(hi)
	 * @return The formula
	 */
	protected static Operator getFormula2()
	{
		return And(
				Or(
						And(a, Or(
								And(Or(c, d, e), g),
								And(a, f),
								And(c, Or(f, g, h, i))
								)
								),
						And(
								Or(a, b),
								Or(c, d, e),
								i)
						),
				Not(And(a, b)),
				Not(And(c, d)),
				Not(And(c, e)),
				Not(And(d, e)),
				Not(And(f, g)),
				Not(And(f, h)),
				Not(And(f, i)),
				Not(And(g, h)),
				Not(And(h, i))
				);
	}

	/**
	 * a(!b+!c+bc!(!fgh!i+!ghi)!(!fglk+!g!ik)) +f
	 * @return The formula
	 */
	protected static Operator getFormula5()
	{
		return Or(
				And(a), Or(
					Not(b),
					Not(c),
					And(b, 
						c,
						Not(Or(And(Not(f), g, h, Not(i)), And(Not(g), h, i))),
						Not(Or(And(Not(f), g, l, k), And(Not(g), Not(i), k)))
						)
				),
				f
			);
	}

	/**
	 * a ((c+d+e)g+af+c(f+g+h+i)) (a+b) (c+d+e) i
	 * @return The formula
	 */
	protected static Operator getFormula15()
	{
		return And(
				a,
				Or(
					And(Or(c, d, e), g),
					And(a, f),
					And(c, Or(f, g, h, i))
				),
				Or(a, b),
				Or(c, d, e),
				i
			);
	}

	/**
	 * (ac+bd)e(i+!g!k+!j(!h+!k))
	 * @return The formula
	 */
	protected static Operator getFormula18()
	{
		return And(
				Or(And(a, c), And(b, d)),
				e,
				Or(i, And(Not(g), Not(k)), And(Not(j), Or(Not(h), Not(k))))
			);
	}

	/**
	 * 
	 * @return The formula
	 */
	protected static Operator getFormula12()
	{
		return null;
	}

	/**
	 * (!ab+a!b) !(cd)  (f!g!h+!fg!h+!f!g!h) !(jk)  ((ac+bd)e(f+ (i(gj+hk))))
	 * @return The formula
	 */
	protected static Operator getFormula6()
	{
		return And(
				Or(And(Not(a), b), And(a, Not(b))),
				Not(And(c, d)),
				Or(And(f, Not(g), Not(h)), And(Not(f), g, Not(h)), And(Not(f), Not(g), Not(h))),
				Not(And(j, k)),
				And(Or(And(a, c), And(b, d)), e, Or(f, And(i, Or(And(g, j), And(h, k)))))
			);
	}

	/**
	 * (ac+bd)e(f+ (i(gj+hk)))
	 * @return The formula
	 */
	protected static Operator getFormula17()
	{
		return And(
				Or(And(a, c), And(b, d)),
				e,
				Or(f, And(g, j), And(h, k))
			);
	}

	/**
	 * (a(!d+!e+de!(!fgh!i+!ghi)!(!fglk+!g!ik)) + !(!fgh!i+!ghi)!(!fglk+!g!ik)(b+c!m+f)) (a!b!c+!ab!c+!a!bc)
	 * @return The formula
	 */
	protected static Operator getFormula3()
	{
		return And(
				And(a, Or(
					Not(d),
					Not(e),
					And(d, e, Not(Or(And(Not(f), g, h, Not(i)), And(Not(g), h, i))), Not(Or(And(Not(f), g, l, k), And(Not(g), Not(i), k)))),
					And(
						Not(Or(And(Not(f), g, h, Not(i)), And(Not(g), h, i))),
						Not(Or(And(Not(f), g, l, k), And(Not(g), Not(i), k))),
						Or(b, And(c, Not(m)), f))
					)
				),
				Or(And(a, Not(b), Not(c)), And(Not(a), b, Not(c)), And(Not(a), Not(b), c))
			);
	}

	/**
	 * a+b+c+!c!def!g!h+i(j+k)
	 * @return The formula
	 */
	protected static Operator getFormula13()
	{
		return Or(
				a, b, c, And(Not(c), d, e, f, Not(g), Not(h)), And(i, Or(j, k))
			);
	}

	/**
	 * a(!d+!e+de!(!fgh!i+!ghi)!(!fglk+!g!i!k)) + !(fgh!i+!ghi)!(!fglk+!g!ik)(b+c!m+f)
	 * @return The formula
	 */
	protected static Operator getFormula16()
	{
		return Or(
				And(a, Or(Not(d), Not(e), And(d, e, Not(Or(And(Not(f), g, h, Not(i)), And(Not(g), h, i))), Not(Or(And(Not(f), g, l, k), And(Not(g), Not(i), Not(k))))))),
				And(Not(Or(And(f, h, h, Not(i)), And(Not(g), h, i))), Not(Or(And(Not(f), g, l, k), And(Not(g), Not(i), Not(k)))), Or(c, And(c, Not(m)), f))
			);
	}

	/**
	 * a!b!c!d!ef(g+!g(h+i))!(jk+!jl+m)
	 * @return The formula
	 */
	protected static Operator getFormula10()
	{
		return And(
				a, Not(b), Not(c), Not(d), Not(e), f, Or(g, And(Not(g), Or(h, i))),
				Not(Or(And(j, k), And(Not(j), l), m))
				);
	}

	/**
	 * a!b!c(!f(g+!f(h+i))) +f(g+!g(h+i)!d!e)!(jk+!jl!m)
	 * @return The formula
	 */
	protected static Operator getFormula11()
	{
		return Or(
				And(a, Not(b), And(c, And(Not(f), Or(g, And(Not(f), Or(h, i)))))),
				And(f, Or(g, And(Not(g), Or(h, i), Not(d), Not(e))), Not(Or(And(j, k), And(Not(j), l, Not(m)))))
			);
	}

	/**
	 * a!b!c(f(g+!g(h+i)))(!e!n+d) + !n(jk+!jl!m)
	 * @return The formula
	 */
	protected static Operator getFormula20Safecomp() // Formula 20 of SAFECOMP, not found in APSEC99
	{
		return Or(
				And(a, Not(b), Not(c), And(f, (Or(g, And(Not(g), Or(h, i))))), And(Not(e), Not(n), Not(d))),
				And(Not(n), Or(And(j, k), And(Not(j), l, Not(m))))
			);
	}
}
