package org.game.mora.websocket.menu;

public enum MsgState {

	ping(1, "心跳"), 
	match(2, "匹配成功"), 
	ready(3, "玩家就绪"), 
	look(4, "双方玩家查看卡牌"), 
	start(5, "比赛正式开始"), 
	card(6, "玩家出牌消息"), 
	result(7, "每轮胜负结果"),
	finish(8, "比赛结束"),
	quit(9, "玩家主动退出,游戏结束"),
	clear(10, "清空连胜"),
	cancel(11, "比赛开始前有玩家退出"),
	connect(12, "重连比赛双方数据"),
	pready(13, "每轮比赛就绪"),
	content(20, "玩家游戏内消息推送"),
	curdata(30, "主动获取当前最新比赛数据");
	
	
	private int mark;
	private String desc;

	private MsgState(int mark, String desc) {
		this.mark = mark;
		this.desc = desc;
	}
	
	public static MsgState match(int mark) {
		for(MsgState state:MsgState.values()) {
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
