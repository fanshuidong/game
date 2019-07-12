package org.game.jumpre.http.request;

import java.util.Map;
import java.util.TreeMap;

import org.game.jumpre.JumperConfig;
import org.game.util.http.request.BaseRequest;
import org.game.util.http.response.BaseResponse;
import org.game.util.menu.ContentType;
import org.game.util.menu.GameType;
import org.game.util.serializer.SerializeUtil;

import okhttp3.Response;

public class GameStartRequest extends BaseRequest<BaseResponse, GameStartRequest> {

	public GameStartRequest(Map<String, String> params,Body body) {
		super(JumperConfig.gameStartUrl(), ContentType.APPLICATION_JSON_UTF_8);
		this.params = params;
		this.body = body;
	}
	
	@Override
	protected BaseResponse response(Response response) {
		return null;
	}

	public static class Builder extends BaseRequest.Builder<GameStartRequest, Builder> {

		private static final long serialVersionUID = 6301835650003998037L;

		public Builder(String gameId, String userIdA, String userIdB, String startTime) {
			this.gameId = gameId;
			this.userIdA = userIdA;
			this.userIdB = userIdB;
			this.startTime = startTime;
			this.gameType = GameType.jumper.mark();
		}

		protected String gameId;
		protected String userIdA;
		protected String userIdB;
		protected String startTime;

		@Override
		public GameStartRequest build() {
			Map<String, String> map = SerializeUtil.JSON.beanToMap(SerializeUtil.GSON_ANNO, this);
			return new GameStartRequest(new TreeMap<String, String>(map),this);
		}

	}

}
