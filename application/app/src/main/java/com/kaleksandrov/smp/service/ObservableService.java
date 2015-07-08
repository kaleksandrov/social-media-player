package com.kaleksandrov.smp.service;

import java.util.Observer;

public interface ObservableService {
	void registerObserver(Observer observer);

	void unregisterObserver(Observer observer);
}
