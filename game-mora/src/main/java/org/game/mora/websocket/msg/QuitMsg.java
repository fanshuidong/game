package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

public class QuitMsg extends Message{

	public QuitMsg() {
		super(MsgState.quit);
	}

}
