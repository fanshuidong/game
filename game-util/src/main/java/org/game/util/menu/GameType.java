package org.game.util.menu;

public enum GameType {
	
	jumper(1,"跳冰箱"),
	mora(2,"猜拳杀");
	
	private int mark;
	private String desc;
	
	private GameType(int mark,String desc) {
		this.mark = mark;
		this.desc = desc;
	}
	
	public int mark() {
		return mark;
	}

	public String desc() {
		return desc;
	}
}
