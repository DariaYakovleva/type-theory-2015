
import java.util.*;


public class Application implements Expression {

	Expression e1;
	Expression e2;
	public Application(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	public String printExp() {
		return "(" + e1.printExp() + " " + e2.printExp() + ")";
	}
	public List<String> freeVariables(List<String> cur) {
		List<String> res = e1.freeVariables(cur);
		res.addAll(e2.freeVariables(cur));
		return res;
	}
	public String printExp2() {
		return printExp();
	}
	public Expression substitution(List<String> booked, Expression var, Expression sub) {
		return new Application(e1.substitution(booked, var, sub), e2.substitution(booked, var, sub));
	}

	public Expression substitution2(List<String> booked, Expression var, Expression sub) {
		e1 = e1.substitution2(booked, var, sub);
		e2 = e2.substitution2(booked, var, sub);
		return this;
	}

	public void getSetEquations(Map<Expression, Expression> types, List<Expression> equations, List<Integer> cnt) {
		Map<Expression, Expression> types2 = new HashMap<>(types);
		e1.getSetEquations(types2, equations, cnt);
		Map<Expression, Expression> types3 = new HashMap<>(types);
		e2.getSetEquations(types3, equations, cnt);
		Expression type_e1 = null;
		Expression type_e2 = null;
		for (Expression exp: types2.keySet()) {
			if (this.isEqual(exp)) return;
			if (e1.isEqual(exp)) type_e1 = types2.get(exp).createCopy();
		}
		for (Expression exp: types3.keySet()) {
			if (e2.isEqual(exp)) type_e2 = types3.get(exp).createCopy();
		}
		if (type_e1 == null || type_e2 == null) {
			System.err.println("Application e1 or e2 is equal to null");
			return;
		}
		Expression cur_type = new Variable("t" + Integer.toString(cnt.get(0)));
		cnt.set(0, cnt.get(0) + 1);
		types.put(this.createCopy(), cur_type);
		List<Expression> lst = new ArrayList<>();
		lst.add(type_e2);
		lst.add(cur_type);
		equations.add(new Equation(type_e1, new Function("f2", lst)));
	}

	public boolean isEqual(Expression b) {
		if (b instanceof Application) {
			if (((Application) b).e1.isEqual(e1) && ((Application) b).e2.isEqual(e2)) return true;
		}
		return false;
	}

	public Expression replace(Expression a, Expression b) {
		if (this.isEqual(a)) {
			return b.createCopy();
		}
		return this.createCopy();
	}

	public Expression createCopy() {
		return new Application(e1.createCopy(), e2.createCopy());
	}

	public Expression getNormalForm(Map<String, Expression> normals, Map<String, Expression> headNormals) { //beta
		String printStr = this.printExp();
//		System.err.println("app norm " + " " + normals.containsKey(printStr));
		if (normals.containsKey(printStr)) {
			return normals.get(printStr).createCopy();
		}
		Expression a = e1;
		Expression b = e2;
		String strA = a.printExp();
		String strB = b.printExp();
		a = a.getHeadNormalForm(normals, headNormals);
		if (a instanceof Lambda) {
			Lambda lam = (Lambda) a;
			Expression res = lam.e2.createCopy().substitution(new ArrayList<>(), lam.e1, b);
			String resStr = res.printExp();
			List<Expression> exps = new ArrayList<>();
			while (!res.isNormalForm()) {
				exps.add(res);
				res = res.getNormalForm(normals, headNormals);
			}
			System.err.println("eres" + exps.size());
			for (Expression e: exps) normals.put(e.printExp(), res);
			normals.put(resStr, res);
			normals.put(printStr, res);
			return res;
		}
		List<Expression> exps = new ArrayList<>();
		while (!a.isNormalForm()) {
			exps.add(a);
			a = a.getNormalForm(normals, headNormals);
		}
		normals.put(strA, a);
		System.err.println("ea" + exps.size());
		for (Expression e: exps) normals.put(e.printExp(), a);
		exps.clear();
		while (!b.isNormalForm()) {
			exps.add(b);
			b = b.getNormalForm(normals, headNormals);
		}
		normals.put(strB, b);
		System.err.println("eb" + exps.size());
		for (Expression e: exps) normals.put(e.printExp(), b);
		Expression res = new Application(a, b);
		normals.put(printStr, res);
		return res;
	}

	public Expression getHeadNormalForm(Map<String, Expression> normals, Map<String, Expression> headNormals) {
		String printStr = this.printExp();
//		System.err.println("app head");
		if (headNormals.containsKey(printStr)) {
			return headNormals.get(printStr).createCopy();
		}
		if (printStr.length() < 100) System.err.println("!!!app head "  + printStr);
		Expression a = e1;
		Expression b = e2;
		String strA = a.printExp();
		a = a.getHeadNormalForm(normals, headNormals);
		Expression res;
		if (a instanceof Lambda) {
//			System.err.println(" a -- lambda");
			if (a.printExp().length() < 50) System.err.println(a.printExp());
			headNormals.put(strA, a);
			Lambda lam = (Lambda) a;
			res = lam.e2.createCopy().substitution(new ArrayList<>(), lam.e1, b);
			res = res.getHeadNormalForm(normals, headNormals);
			headNormals.put(new Application(a, b).printExp(), res);
		} else {
			System.err.println("app just app");
			res = new Application(a, b);
		}
		if (!(res instanceof Lambda)) res = res.getNormalForm(normals, headNormals);
		headNormals.put(printStr, res);
		return res;
	}

	public boolean isNormalForm() {
		if (e1 instanceof Lambda) {
			return false;
		}
		return e1.isNormalForm() && e2.isNormalForm();
	}
}