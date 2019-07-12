package org.game.jumpre.websocket.msg;

import org.game.jumpre.websocket.menu.MsgState;

public class PingMsg extends Message{

	public PingMsg() {
		super(MsgState.ping);
	}

}
