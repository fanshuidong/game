package org.game.mora.websocket.msg;

import java.util.ArrayList;
import java.util.List;

import org.game.mora.websocket.menu.Card;
import org.game.mora.websocket.menu.MsgState;

import lombok.Getter;
import lombok.Setter;
/**
 * 比赛过程中重连数据
 * @author fansd
 * @date 2019年7月1日 上午10:05:59
 */
@Getter
@Setter
public class ReConnectMsg extends Message{
	
	public ReConnectMsg(List<Card> mCards,Integer tCards,Integer time,Integer ms,Integer ts) {
		super(MsgState.connect);
		this.mCards = new ArrayList<Integer>();
		for(Card card : mCards)
			this.mCards.add(card.mark());
		this.tCards = tCards;
		this.time = time;
		this.ms = ms;
		this.ts = ts;
	}
	
	private List<Integer> mCards;//自己的手牌
	private Integer tCards;//对方手牌数量
	private Integer time;//剩余的出牌时间
	private Integer ms;//自己的出牌状态 1:出牌 0 未出牌
	private Integer ts;//对手出牌状态

}
