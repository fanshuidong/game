package org.game.mora.websocket.menu;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Card {
	
	stone(1,"石头"),
	scissors(2,"剪刀"),
	cloth(3,"布");
	
	private int mark;
	private String desc;
	
	public int mark() {
		return mark;
	}
	
	public String desc() {
		return desc;
	}
	
	public static Card match(int mark) {
		for(Card card:Card.values()) {
			if(card.mark() == mark) {
				return card;
			}
		}
		return null;
	}
}
