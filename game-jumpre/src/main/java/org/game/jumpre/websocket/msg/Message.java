package org.game.jumpre.websocket.msg;

import org.game.jumpre.websocket.menu.MsgState;

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
