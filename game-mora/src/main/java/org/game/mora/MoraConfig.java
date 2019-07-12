package org.game.mora;

import org.game.util.spring.SpringContextUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "mora")
@Getter
@Setter
public class MoraConfig {
	
	private int lookTime;
	private int gameTime;
	private String gameStartUrl;
	private String gameEndUrl;
	private String userScoreQueryUrl;
	
	public static MoraConfig instance() {
		MoraConfig moraConfig = (MoraConfig)SpringContextUtil.getBean("moraConfig");
		return moraConfig;
	}
	
	public static String gameStartUrl() {
		return instance().getGameStartUrl();
	}
	
	public static String gameEndUrl() {
		return instance().getGameEndUrl();
	}
	
	public static String userScoreQueryUrl() {
		return instance().getUserScoreQueryUrl();
	}
	
	public static int lookTime() {
		return instance().getLookTime();
	}
	
	public static int gameTime() {
		return instance().getGameTime();
	}
}
