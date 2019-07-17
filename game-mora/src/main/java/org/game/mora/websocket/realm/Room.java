package org.game.mora.websocket.realm;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.game.mora.MoraConfig;
import org.game.mora.http.request.GameEndRequest;
import org.game.mora.http.request.GameStartRequest;
import org.game.mora.websocket.WebSocketMora;
import org.game.mora.websocket.menu.Card;
import org.game.mora.websocket.menu.LoseReason;
import org.game.mora.websocket.menu.MsgState;
import org.game.mora.websocket.menu.RoomState;
import org.game.mora.websocket.msg.CancelMsg;
import org.game.mora.websocket.msg.CardMsg;
import org.game.mora.websocket.msg.ContentMsg;
import org.game.mora.websocket.msg.FinishMsg;
import org.game.mora.websocket.msg.LookMsg;
import org.game.mora.websocket.msg.ReConnectMsg;
import org.game.mora.websocket.msg.ResultMsg;
import org.game.mora.websocket.msg.StartMsg;
import org.game.util.ExcutorUtil;
import org.game.util.lang.DateUtil;
import org.game.util.lang.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
	
	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(Room.class);
	private Player player1;
	private Player player2;
	private ScheduledFuture<?> lookTask;
	private ScheduledFuture<?> gameTask;
//	private ScheduledFuture<?> compareTask;
	private AtomicReference<RoomState> roomState = new AtomicReference<RoomState>(null);//房间状态
	private LoseReason loseReason;// 失败原因
	private AtomicReference<Boolean> isCompare = new AtomicReference<Boolean>(false);//是否再比较中
	private String startTime;// 开始时间
	private String endTime;// 结束时间
	private String gameId = "";
	private String winner = "1000";// 1000表示平局;

	public Room() {}
	
	public Room(String player1Id, String player2Id) {
		this.player1 = WebSocketMora.players.get(player1Id);
		this.player2 = WebSocketMora.players.get(player2Id);
	}

	public void start() {
		player1.joinRoom(player2, this);
		player2.joinRoom(player1, this);
		roomState.set(RoomState.ready);
		logger.info("玩家 {} 与 {} 成功匹配",player1.getUserId(),player2.getUserId());
	}

	public void action(MsgState state, String message, Player player) {
		switch (state) {
		case ready:// 玩家就绪
			if (RoomState.ready == roomState.get()) {
				player.setReady(true);
				Player rival = player.getRival();
				if (rival.isReady()) {// 如果对手已经就绪
					if (roomState.compareAndSet(RoomState.ready, RoomState.look)) {
						player.send(new LookMsg(player.getCards(), rival.getCards()));
						rival.send(new LookMsg(rival.getCards(), player.getCards()));
						this.startTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
						this.gameId = KeyUtil.uuid();
						//双方玩家就绪之后 查看卡牌 ，查看玩之后正式开始比赛
						lookTask = ExcutorUtil.excuter.schedule(() -> {
							if (roomState.compareAndSet(RoomState.look, RoomState.run)) {
								gaming();
							}
						}, MoraConfig.lookTime(), TimeUnit.SECONDS);
						// 调用比赛开始接口
						Room.httpStart(gameId, player1.getUserId(), player2.getUserId(), startTime);
					}
				}
			}
			break;
		case pready:// 每轮比赛就绪
			if (RoomState.run == roomState.get()) {
				synchronized (this) {
					player.setPready(true);
					Player rival = player.getRival();
					if (rival.isPready()) {// 如果对手已经就绪
						player.send(new StartMsg());
						rival.send(new StartMsg());
						logger.info("比赛开始");
						gameTaskInit();
						player.setPready(false);
						rival.setPready(false);
					}
				}
			}
			break;
		case card:// 客户端推送的玩家出牌信息
			try {
				if (RoomState.run == roomState.get()) {
					CardMsg msg = gson.fromJson(message, CardMsg.class);
					if(!player.getCards().contains(Card.match(msg.getCard()))) {
						logger.info("用户 {} 没有该卡牌：{}",player.getUserId(),msg.getCard());
						return;
					}
					synchronized (this) {
						player.setCard(msg.getCard());
						player.getRival().send(new CardMsg());
						if(player.getRival().getCard() != null) {
							gameTask.cancel(false);
							if(isCompare.compareAndSet(false, true))
								compare_();
						}
					}
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
			break;
		case quit:
			finish_(player, LoseReason.quit);
			break;
		case content:
			if (RoomState.run == roomState.get()) 
				player.getRival().send(gson.fromJson(message, ContentMsg.class));
			break;
		default:
			break;
		}
	}
	
	//观看手牌后正式进入比赛
	private void gaming() {
		player1.send(new StartMsg());
		player2.send(new StartMsg());
		//间断性检测双方玩家的出牌信息
//		compareTask = ExcutorUtil.excuter.scheduleAtFixedRate(() -> {
//			comparing();
//		}, 0, 1,TimeUnit.SECONDS);
		gameTaskInit();
	}
	
	/**
	 * 启动每轮出牌计时
	 */
	private void gameTaskInit() {
		isCompare.set(false);
		gameTask = ExcutorUtil.excuter.schedule(() -> {
			if(isCompare.compareAndSet(false, true))
				compare();
		},MoraConfig.gameTime(), TimeUnit.SECONDS);
	}
	
	//每轮的双方卡牌比较，出牌时间到了之后调用
	private void compare() {
		//1、只有一方玩家出牌，另外一方掉线等待重连没出牌
		if(player1.getCard() != null && player2.getCard() == null) {
			//掉线方随机选出一张牌
			player2.setCard(player2.getCards().get(0).mark());
			result(player1);
		}
		if(player2.getCard() != null && player1.getCard() == null) {
			//掉线方随机选出一张牌
			player1.setCard(player1.getCards().get(0).mark());
			result(player2);
		}
		//2、双方玩家都出牌（这里包含前端随机出牌）
		if(player1.getCard() != null && player2.getCard() != null)
			compare_();
		//3、双方都掉线等待重连没出牌
		if(player2.getCard() == null && player1.getCard() == null) {
			player1.setCard(player1.getCards().get(0).mark());
			player2.setCard(player2.getCards().get(0).mark());
			result(null);
		}
	}
	
	//双方都出牌的情况下比较大小
	private void compare_() {
		switch (player1.getCard() - player2.getCard()) {
		case -1:
		case 2:
			//player1赢
			result(player1);
			break;
		case 1:
		case -2:
			//player2赢
			result(player2);
			break;
		case 0:
			//平手
			result(null);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 每轮结果处理
	 */
	protected void result(Player win) {
		if(win == null) {//平手
			player1.send(new ResultMsg(2,player2.getCard()));
			player2.send(new ResultMsg(2,player1.getCard()));
			player2.getCards().remove(Card.match(player2.getCard()));
			player1.getCards().remove(Card.match(player1.getCard()));
		}else {
			Player loser = win.getRival();
			win.send(new ResultMsg(1, loser.getCard()));
			loser.send(new ResultMsg(0, win.getCard()));
			win.getCards().add(Card.match(loser.getCard()));
			loser.getCards().remove(Card.match(loser.getCard()));
		}
		if(player1.getCards().size() == 0 && player2.getCards().size() == 0) {
			finish(null);
		}else if(player1.getCards().size() == 0) {
			finish(player2);
		}else if(player2.getCards().size() == 0) {
			finish(player1);
		}
		player1.setCard(null);
		player2.setCard(null);
	}

	//比赛正常结束
	private void finish(Player win) {
		if (roomState.compareAndSet(RoomState.run, RoomState.finish)) {
			if(win == null) {//平手
				player1.send(new FinishMsg(2));
				player2.send(new FinishMsg(2));
				WebSocketMora.succession.remove(player1.getUserId());
				WebSocketMora.succession.remove(player2.getUserId());
			}else {
				Player loser = win.getRival();
				WebSocketMora.addSuc(win.getUserId());// 添加连胜
				WebSocketMora.succession.remove(loser.getUserId());
				winner = win.getUserId();
				loser.send(new FinishMsg(0));
				win.send(new FinishMsg(1,WebSocketMora.getSuc(win.getUserId())));
			}
//			compareTask.cancel(false);
			this.loseReason = LoseReason.normal;
			logger.info("{} 玩家 {} 与  {} 比赛结束,失败原因：{}", endTime, player1.getUserId(), player2.getUserId(),loseReason.desc());
			this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
			Room.httpEnd(gameId, player1.getUserId(), 0, player2.getUserId(), 0,endTime, loseReason.mark(), winner);
		}
	}

	/**
	 * 比赛异常结束 有玩家退出
	 * @param player 退出的玩家
	 */
	public void finish_(Player player, LoseReason reason) {
		if (roomState.compareAndSet(RoomState.run, RoomState.finish) || roomState.compareAndSet(RoomState.look, RoomState.finish)) {
//			compareTask.cancel(false);
			Player win = player.getRival();//对手获胜
			winner = win.getUserId();
			WebSocketMora.addSuc(winner);// 添加连胜
			WebSocketMora.succession.remove(player.getUserId());
			player.send(new FinishMsg(0));
			win.send(new FinishMsg(1,WebSocketMora.getSuc(winner)));
			this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
			this.loseReason = reason;
			logger.info("{} 玩家 {} 主动退出比赛结束,失败原因：{}", endTime, player.getUserId(), loseReason.desc());
			player.close();
			// 调用对方奖励结算接口
			Room.httpEnd(gameId, player1.getUserId(), 0, player2.getUserId(), 0,
					endTime, loseReason.mark(), winner);
		} else if (roomState.compareAndSet(RoomState.ready, null)) {
			if (player.getRival() != null)
				player.getRival().send(new CancelMsg());
			player.close();
		} else {
			player.close();
		}
	}

	public static void httpStart(String gameId, String userIdA, String userIdB, String startTime) {
		GameStartRequest.Builder builder = new GameStartRequest.Builder(gameId, userIdA, userIdB, startTime);
		builder.build().sync_();
	}

	public static void httpEnd(String gameId, String userIdA, Integer scopeA, String userIdB, Integer scopeB,
			String endTime, int loseReason, String winner) {
		GameEndRequest.Builder builder = new GameEndRequest.Builder(gameId, userIdA, scopeA, userIdB, scopeB, endTime,
				loseReason, winner);
		builder.build().sync_();
	}

	// 房间重连
	public void reConnect(Player player) {
		if (roomState.get() == RoomState.ready) {
			player.quitRoom();
			GameRunner.INSTANCE.push(player.getUserId());
		}
		if (roomState.get() == RoomState.look) {
			player.send(new LookMsg(player.getCards(), player.getRival().getCards()));
		}
		if (roomState.get() == RoomState.run) {
			int ts = player.getRival().getCard() == null ? 0:1;
			int time = gameTask.isCancelled()?-1:(int)gameTask.getDelay(TimeUnit.SECONDS);	
			player.send(new ReConnectMsg(player.getCards(), player.getRival().getCards().size(),time,player.getCard(),ts,player.getMatchUserId()));
		}
		if (roomState.get() == RoomState.finish) {
			player.send(new FinishMsg(winner.equals(player.getUserId()) ? 1:winner.equals("1000") ? 2 : 0 , 
					WebSocketMora.getSuc(player.getUserId())));
		}
	}
	
	/**
	 * 匹配成功且没开始前有玩家断线 解散房间
	 * @param player 断线玩家
	 */
	public void dissolve(Player player) {
		if(roomState.get() == RoomState.ready) {
			try {
				Player to = player.getRival();
				if(to != null) {
					to.quitRoom();
					if(WebSocketMora.players.containsKey(to.getUserId())) {
						GameRunner.INSTANCE.push(to.getUserId());
						to.send(new CancelMsg());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				player.quitRoom();
			}
		}
	}
}
