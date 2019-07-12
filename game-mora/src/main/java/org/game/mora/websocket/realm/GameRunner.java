package org.game.mora.websocket.realm;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum GameRunner {
	
	INSTANCE;
	
	private LinkedList<String> queue = new LinkedList<String>();
	private ExecutorService runner = Executors.newCachedThreadPool();
	
	public synchronized void remove(String uid) {
		queue.remove(uid);
	}
	
	public synchronized void push(String uid) {
		if(!queue.contains(uid))
			queue.add(uid);
	}
	
	public synchronized void match() {
		while (queue.size() >= 2) {
			Room room = new Room(queue.poll(), queue.poll());
			runner.submit(() -> room.start());
		}
	}
}
