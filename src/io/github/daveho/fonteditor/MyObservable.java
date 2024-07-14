package io.github.daveho.fonteditor;

import java.util.ArrayList;
import java.util.List;

public class MyObservable {
	private List<MyObserver> observerList;
	
	public MyObservable() {
		this.observerList = new ArrayList<MyObserver>();
	}
	
	public void notifyObservers() {
		
	}
	
	public void addObserver(MyObserver observer) {
		this.observerList.add(observer);
	}
}
