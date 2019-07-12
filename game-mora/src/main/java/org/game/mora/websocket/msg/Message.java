package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

public class Message {
	
	public Message(MsgState state) {
		this.state = state.getMark();
	}

	private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
