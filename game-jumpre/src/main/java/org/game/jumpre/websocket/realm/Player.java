package org.game.jumpre.websocket.realm;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

import org.game.jumpre.http.request.PlayerScopeRequest;
import org.game.jumpre.websocket.WebScoketJumpre;
import org.game.jumpre.websocket.menu.LoseReason;
import org.game.jumpre.websocket.menu.QueueType;
import org.game.jumpre.websocket.msg.Message;
import org.game.util.ExcutorUtil;
import org.game.util.http.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Player {
	
	private static Logger logger = LoggerFactory.getLogger(Player.class);
	private static Gson gson = new Gson();
	
	private ScheduledFuture<?> pingTask;

	public Player(Session session, String userId) {
		this.session = session;
		this.userId = userId;
		pushTime = System.currentTimeMillis() / 1000;
		initAverageScope();
		startPing();
	}
	
	public void init() {//一般用于重连
		pushTime = System.currentTimeMillis() / 1000;
		startPing();
	}
	
	private Session session;
	private String userId;
	private long pushTime;// 最近一次接收到包的时间
	private int dif = 0;// 每次执行定时任务时间与pushTime的差值
	private int scope = 0;
	private boolean isMatch;
	private boolean isReady;
	private String matchUserId;// 匹配对手
	private Room room;
	private int averageScope;//最近 3场 平均分

	public Player getBySession(Session session) {
		return session.equals(this.session) ? this : null;
	}

	public String getUserIdBySession(Session session) {
		return session.equals(this.session) ? this.userId : null;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getPushTime() {
		return pushTime;
	}

	public void setPushTime(long pushTime) {
		this.pushTime = pushTime;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public String getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(String matchUserId) {
		this.matchUserId = matchUserId;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getAverageScope() {
		return averageScope;
	}

	public void setAverageScope(int averageScope) {
		this.averageScope = averageScope;
	}

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
		return matchUserId == null ? null :WebScoketJumpre.players.get(this.matchUserId);
	}

	public void close() {
		try {
			WebScoketJumpre.players.remove(userId);
			this.session.close();
			this.pingTask.cancel(false);
			this.room = null;
			this.matchUserId=null;
			this.isMatch = false;
			this.isReady = false;
			this.averageScope = -1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void quitRoom() {
		this.room = null;
		this.matchUserId=null;
		this.isMatch = false;
		this.isReady = false;
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
			WebScoketJumpre.succession.remove(userId);
		}else {
			GameRunner.INSTANCE.remove(userId);
			close();
		}
		logger.info("用户 {} 长时间未操作断开连接",userId);
	}
	
	public void initAverageScope() {
		try {
			PlayerScopeRequest.Builder builder = new PlayerScopeRequest.Builder(this.userId);
			BaseResponse response =  builder.build().sync_();
			if(response!=null) {
				if(response.getReturnFlag().equals("100") && this.userId.equals(response.getUserID())) {
					averageScope =  response.getAvgScore();
					logger.info("用户 {} 最近5场平均分：{}",response.getUserID(),averageScope);
				}else {
					averageScope = 0;
				}
			}
		} catch (Exception e) {
			averageScope = 0;
			e.printStackTrace();
		} 
	}
	
	public QueueType getQueue() {
		return QueueType.match(averageScope);
	}

}
