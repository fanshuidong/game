package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

public class CancelMsg extends Message{

	public CancelMsg() {
		super(MsgState.cancel);
	}

}
