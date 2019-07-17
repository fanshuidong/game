package org.game.mora.websocket.realm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.websocket.Session;

import org.game.mora.websocket.WebSocketMora;
import org.game.mora.websocket.menu.Card;
import org.game.mora.websocket.menu.LoseReason;
import org.game.mora.websocket.msg.MatchMsg;
import org.game.mora.websocket.msg.Message;
import org.game.util.ExcutorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
	
	private static Logger logger = LoggerFactory.getLogger(Player.class);
	private static Gson gson = new Gson();
	
	private ScheduledFuture<?> pingTask;

	public Player() {};
	
	public Player(Session session, String userId) {
		this.session = session;
		this.userId = userId;
		pushTime = System.currentTimeMillis() / 1000;
		startPing();
	}
	
	public void recon(Session session){//玩家重连
		try {
			if (this.session.isOpen()) 
				this.session.close();// 重连关闭原先的链接
			this.setSession(session);
			closePing();
			pushTime = System.currentTimeMillis() / 1000;
			startPing();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Session session;
	private String userId;
	private long pushTime;// 最近一次接收到包的时间
	private int dif = 0;// 每次执行定时任务时间与pushTime的差值
	private List<Card> cards = new ArrayList<>();//玩家的手牌
	private boolean isMatch;
	private boolean isReady;
	private boolean isPready;//每轮比赛就绪状态
	private String matchUserId;// 匹配对手
	private Room room;
	private Integer card;//玩家当前出的牌

	public void send(Message message) {
		try {
			if(session.isOpen())
				this.session.getBasicRemote().sendText(gson.toJson(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//获取对手
	public Player getRival() {
		return matchUserId == null ? null :WebSocketMora.players.get(this.matchUserId);
	}

	public void close() {
		try {
			WebSocketMora.players.remove(userId);
			this.session.close();
			this.pingTask.cancel(false);
			this.room = null;
			this.matchUserId=null;
			this.isMatch = false;
			this.isReady = false;
			this.cards.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加入房间
	 * @param match 对手
	 */
	public void joinRoom(Player match,Room room) {
		this.room = room;
		this.setMatchUserId(match.getUserId());
		this.setMatch(true);
		this.send(new MatchMsg(match.getUserId()));
		initCards();
	}
	
	//发牌
	private void initCards() {
		Random random = new Random();
		while (cards.size() < 3) {
			Card card = Card.values()[random.nextInt(Card.values().length)];
			if(cards.size() == 2 && cards.get(0) == cards.get(1) && cards.get(0) == card)
				continue;
			else
				cards.add(card);
		}
	}
	
	//退出房间
	public void quitRoom() {
		this.room = null;
		this.matchUserId=null;
		this.isMatch = false;
		this.isReady = false;
		this.cards.clear();
	}
	
	public void closePing() {
		this.pingTask.cancel(false);
	}
	
	public Player player() {
		return this;
	}
	
	public void startPing() {
		pingTask = ExcutorUtil.excuter.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				dif = (int) (System.currentTimeMillis() / 1000 - pushTime);
				if (dif > 11) {
					timeOut();
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
	}
	
	//心跳超时处理
	public synchronized void timeOut() {
		if(room != null) {
			room.finish_(player(),LoseReason.timeOut);
			WebSocketMora.succession.remove(userId);
		}else {
			GameRunner.INSTANCE.remove(userId);
			close();
		}
		logger.info("用户 {} 长时间未操作断开连接",userId);
	}

}
