package org.game.mora.websocket.menu;

public enum RoomState {
	ready(1, "匹配到开始准备阶段"), 
	look(2,"观看双方卡牌"),
	run(3, "比赛中"), 
	finish(4, "比赛结束"); 
	private int mark;
	private String desc;

	private RoomState(int mark, String desc) {
		this.mark = mark;
		this.desc = desc;
	}
	
	public static RoomState match(int mark) {
		for(RoomState state:RoomState.values()) {
			if(state.getMark() == mark) {
				return state;
			}
		}
		return null;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
