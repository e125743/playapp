package controllers;

import play.mvc.*;
import play.libs.OAuth.*;
import play.libs.F.Option;
import play.libs.OAuth;

public class Twitter extends Controller {

	//発行されたキー
    private static final String CONSUMER_KEY = "DR55ofR2qMadX2X8Id5g";
    private static final String CONSUMER_SECRET = "vw8eEMfOCEdBhljh8PrKywX7dFogu3oX8y7N1Q4WKBg";

    //上記の２つをペアにする
    static final ConsumerKey  KEY  = new ConsumerKey(CONSUMER_KEY, CONSUMER_SECRET);    

    private static final ServiceInfo SERVICE_INFO = new ServiceInfo("https://api.twitter.com/oauth/request_token",
								    "https://api.twitter.com/oauth/access_token",
								    "https://api.twitter.com/oauth/authorize", 
								    KEY);
  
    private static final OAuth TWITTER = new OAuth(SERVICE_INFO);
  
    public static Result auth() {
    	
	String verifier = request().getQueryString("oauth_verifier");

	//verifierの有無を調べる
	//1.verifierがあった場合
	if (verifier == null || verifier.length() == 0) {
		
		//リクエストtokenを取得
	    String url = routes.Twitter.auth().absoluteURL(request());
	    RequestToken requestToken = TWITTER.retrieveRequestToken(url);
	    saveSessionTokenPair(requestToken);
	    
	    //2.Twitterの認証画面にリダイレクト
	    return redirect(TWITTER.redirectUrl(requestToken.token));
	    
	} else {
		//3.CallbackURLにリダイレクトされた後、アクセスtoken取得
	    RequestToken requestToken = getSessionTokenPair().get();
	    RequestToken accessToken = TWITTER.retrieveAccessToken(requestToken, verifier);
	    saveSessionTokenPair(accessToken);
	    
	    //4.indexページにリダイレクトする
	    return redirect(routes.Application.index());
	}
    }

    //session（cookie）にTokenを保存
    private static void saveSessionTokenPair(RequestToken requestToken) {
	session("token", requestToken.token);
	session("secret", requestToken.secret);
    }

    ////sessionに保存されたTokenを渡す
    static Option<RequestToken> getSessionTokenPair() {
	if (session().containsKey("token")) {
	    return Option.Some(new RequestToken(session("token"), session("secret")));
	}
	return Option.None();
    }
}
