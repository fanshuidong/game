package org.game.util.http.request;

import java.io.IOException;

import org.game.util.exception.RequestFailException;
import org.game.util.http.HttpPost;
import org.game.util.http.response.BaseResponse;
import org.game.util.menu.ContentType;
import org.game.util.serializer.SerializeUtil;

import okhttp3.Response;


public class BaseRequest <RESPONSE extends BaseResponse, REQUEST extends BaseRequest<RESPONSE, REQUEST>> extends HttpPost<RESPONSE, REQUEST>{
	
	public BaseRequest(String url,ContentType contentType) {
		super(url,contentType);
		this.contentType();
	}
	
	
	@Override
	protected RESPONSE response(Response response) {
		try {
			RESPONSE resp = SerializeUtil.GSON.fromJson(response.body().string(), clazz);
			resp.verify();
			return resp;
		} catch (IOException e) {
			throw new RequestFailException(e);
		}
	}
	
	public static abstract class Builder<REQUEST, BUILDER extends Builder<REQUEST, BUILDER>> implements HttpPost.Body {

		private static final long serialVersionUID = 7491685951697117463L;
		
		protected Integer gameType;
		
		public abstract REQUEST build();
	}

}
