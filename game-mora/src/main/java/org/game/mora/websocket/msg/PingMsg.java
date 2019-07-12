package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

public class PingMsg extends Message{

	public PingMsg() {
		super(MsgState.ping);
	}

}
