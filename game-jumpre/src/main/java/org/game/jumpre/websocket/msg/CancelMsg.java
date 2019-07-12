package org.game.jumpre.websocket.msg;

import org.game.jumpre.websocket.menu.MsgState;

public class CancelMsg extends Message{

	public CancelMsg() {
		super(MsgState.cancel);
	}

}
