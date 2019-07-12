package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

import lombok.Getter;
import lombok.Setter;

/**
 * 比赛结果数据
 * 
 * @author fansd
 * @date 2019年5月14日 下午3:26:02
 */
@Getter
@Setter
public class FinishMsg extends Message {

	public FinishMsg(int win) {
		super(MsgState.finish);
		this.win = win;// 1赢0输2平局
	}
	
	public FinishMsg(int win,int suc) {
		super(MsgState.finish);
		this.win = win;// 1赢0输2平局
		this.suc = suc;
	}

	private int win;// 谁赢了
	private int suc;// 连胜数量

}
