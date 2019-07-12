package org.game.util.http;

import java.io.IOException;

import org.game.util.exception.RequestFailException;
import org.game.util.lang.StringUtil;

import okhttp3.Call;
import okhttp3.Response;

public abstract class Callback<RESPONSE extends HttpResponse> implements okhttp3.Callback {
	
	protected HttpRequest<RESPONSE, ?> request;
	
	@Override
	public void onResponse(Call call, Response response) throws IOException {
		if (!response.isSuccessful()) {
			String errorContent = null;
			try {
				errorContent = response.body().string();
			} catch (Exception e) {}
			String error = response.message();
			if (StringUtil.hasText(errorContent))
				error += " - [" + errorContent + "]";
			throw new RequestFailException(response.code(), error);
		}
		RESPONSE resp = request.response(response);
		onResponse(resp);
	}
	
	protected abstract void onResponse(RESPONSE response); 
}
