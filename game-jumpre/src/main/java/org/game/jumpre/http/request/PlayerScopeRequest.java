package org.game.jumpre.http.request;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.game.jumpre.JumperConfig;
import org.game.util.exception.RequestFailException;
import org.game.util.http.request.BaseRequest;
import org.game.util.http.response.BaseResponse;
import org.game.util.menu.ContentType;
import org.game.util.menu.GameType;
import org.game.util.serializer.SerializeUtil;

import okhttp3.Response;

public class PlayerScopeRequest extends BaseRequest<BaseResponse, PlayerScopeRequest> {

	public PlayerScopeRequest(Map<String, String> params,Body body) {
		super(JumperConfig.userScoreQueryUrl(), ContentType.APPLICATION_JSON_UTF_8);
		this.params = params;
		this.body = body;
	}
	
	@Override
	protected BaseResponse response(Response response) {
		try {
			BaseResponse resp = SerializeUtil.GSON.fromJson(response.body().string(), clazz);
			resp.verify();
			return resp;
		} catch (IOException e) {
			throw new RequestFailException(e);
		}
	}

	public static class Builder extends BaseRequest.Builder<PlayerScopeRequest, Builder> {

		private static final long serialVersionUID = 3769116924795413764L;

		public Builder(String userId) {
			this.userId = userId;
			this.gameType = GameType.jumper.mark();
		}

		protected String userId;
	

		@Override
		public PlayerScopeRequest build() {
			Map<String, String> map = SerializeUtil.JSON.beanToMap(SerializeUtil.GSON_ANNO, this);
			return new PlayerScopeRequest(new TreeMap<String, String>(map),this);
		}

	}

}
