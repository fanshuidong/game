package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

import lombok.Getter;
import lombok.Setter;

/**
 * 每轮胜负消息
 * 
 * @author fansd
 * @date 2019年5月14日 下午3:26:02
 */

@Getter
@Setter
public class ResultMsg extends Message {

	public ResultMsg(int win,int card) {
		super(MsgState.result);
		this.win = win;
		this.card = card;
	}
	
	private int win;
	private int card;

}
