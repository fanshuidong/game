package org.game.mora.websocket.msg;

import org.game.mora.websocket.menu.MsgState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentMsg extends Message{

	public ContentMsg(String msg) {
		super(MsgState.content);
		this.msg = msg;
	}
	
	private String msg;

}
