package org.game.mora.websocket.msg;

import java.util.ArrayList;
import java.util.List;

import org.game.mora.websocket.menu.Card;
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

	public ResultMsg(int win,int card,List<Card> mCards,Integer tCards) {
		super(MsgState.result);
		this.win = win;
		this.card = card;
		this.mCards = new ArrayList<Integer>();
		for(Card card_ : mCards)
			this.mCards.add(card_.mark());
		this.tCards = tCards;
	}
	
	private int win;
	private int card;
	private List<Integer> mCards;//自己的手牌
	private Integer tCards;//对方手牌数量

}
