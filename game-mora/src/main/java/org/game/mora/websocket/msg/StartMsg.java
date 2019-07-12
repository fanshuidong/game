package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

/**
 * 比赛开始
 * @author fansd
 * @date 2019年5月14日 下午3:26:02
 */

public class StartMsg extends Message{

	public StartMsg() {
		super(MsgState.start);
	}

}
