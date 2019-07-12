package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardMsg extends Message {

	public CardMsg() {
		super(MsgState.card);
	}

	private int card;
}