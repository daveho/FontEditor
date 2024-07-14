package io.github.daveho.fonteditor;

import java.util.ArrayList;
import java.util.List;

public class MyObservable {
	private List<MyObserver> observerList;
	
	public MyObservable() {
		this.observerList = new ArrayList<MyObserver>();
	}
	
	public void notifyObservers() {
		notifyObservers(null);
	}
	
	public void notifyObservers(Object hint) {
		for (MyObserver observer : observerList)
			observer.update(this, hint);
	}
	
	public void addObserver(MyObserver observer) {
		this.observerList.add(observer);
	}
}
