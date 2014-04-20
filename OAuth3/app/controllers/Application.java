package controllers;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import models.Tweet;
import play.mvc.*;
import play.libs.F.Option;
import play.libs.*;
import play.libs.WS.Response;
import play.libs.OAuth.*;
import static play.libs.F.Function;

public class Application extends Controller {
  
    @SuppressWarnings("deprecation")
	public static Result index() {
    	//sessionからtoken情報を取得する
	Option<RequestToken> sessionTokenPair = Twitter.getSessionTokenPair();

	//1.token情報があった場合
	if (sessionTokenPair.isDefined()) {
	    //利用したいTwitter API
	    String feedUrl = "https://api.twitter.com/1.1/statuses/user_timeline.json";
	    //WSの署名用クラス
	    OAuthCalculator calc = new OAuthCalculator(Twitter.KEY, sessionTokenPair.get());
	    //5.認証情報をsign()にセットして、APIにアクセスする
	    return async(WS.url(feedUrl)
		.sign(calc)
		.get()
		.map(new Function<Response, Result>() {
			   public Result apply(Response response) {
                   if(response.getStatus() == 200){
                       //正常レスポンスを受け取ったらJSONを表示
                	   createEntity(response.asJson());
                	   List<Tweet> tweets = Tweet.find.all();
                	    return ok(views.html.index.render(tweets));
                   }else{
                       //ステータスコード200以外の場合は、エラーとして表示
                       return badRequest("error:"+response.getStatus());
                   }
               }

			//取得したTweetをDBに保存
			private void createEntity(JsonNode rootNode) {
			    JsonNode current;
			    for (int i = 0; (current = rootNode.get(i)) != null; i++) {
			        Tweet tweet = new Tweet();
			        tweet.id = current.findPath("id").asLong();
			        tweet.text = current.findPath("text").textValue();
			        tweet.screenName = current.findPath("screen_name").textValue();
			        tweet.profileImageUrl =  current.findPath("profile_image_url").textValue();
			        tweet.save();
			    }
			}			
		    }
		    )
	    	);

	}
	//0.OAuth認証ページ(http://localhost:9000/auth)にリダイレクト
	return redirect(routes.Twitter.auth());
    }
}
