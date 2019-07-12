package org.game.jumpre.websocket.msg;

import org.game.jumpre.websocket.menu.MsgState;

public class ScopeMsg extends Message {

	public ScopeMsg() {
		super(MsgState.scope);
	}

	private String userId;
	private int scope;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

}
