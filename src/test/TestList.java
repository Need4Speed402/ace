package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestList implements Iterable<Test>{
	private final List<Test> tests;
	
	public TestList () {
		this.tests = new ArrayList<Test>();
	}
	
	public void push (Test t) {
		this.tests.add(t);
	}
	
	public void push (Test[] tests) {
		for (int i = 0; i < tests.length; i++) {
			this.push(tests[i]);
		}
	}
	
	public void clear () {
		this.tests.clear();
	}
	
	public Test[] toArray () {
		Test[] tests = new Test[this.tests.size()];
		
		for (int i = 0; i < tests.length; i++) {
			tests[i] = this.tests.get(i);
		}
		
		return tests;
	}
	
	public int size () {
		return this.tests.size();
	}
	
	public Test get (int i) {
		return this.tests.get(i);
	}
	
	public Test first () {
		return this.tests.get(0);
	}
	
	public Test last () {
		return this.tests.get(this.tests.size() - 1);
	}
	
	public void set (int i, Test t) {
		this.tests.set(i, t);
	}

	@Override
	public Iterator<Test> iterator() {
		return this.tests.iterator();
	}
}
