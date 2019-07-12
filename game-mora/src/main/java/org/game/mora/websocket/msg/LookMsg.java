package org.game.mora.websocket.msg;

import java.util.ArrayList;
import java.util.List;

import org.game.mora.websocket.menu.Card;
import org.game.mora.websocket.menu.MsgState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookMsg extends Message{

	public LookMsg(List<Card> mCards_,List<Card> tCards_) {
		super(MsgState.look);
		this.mCards = new ArrayList<Integer>();
		this.tCards = new ArrayList<Integer>();
		for(Card card : mCards_)
			mCards.add(card.mark());
		for(Card card : tCards_) {
			if(tCards.size() == 2)
				break;
			tCards.add(card.mark());
		}
	}
	
	private List<Integer> mCards;
	private List<Integer> tCards;

}
