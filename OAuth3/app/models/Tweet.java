package models;

import javax.persistence.*;
import play.db.ebean.Model;
import play.data.validation.*;

@Entity 
public class Tweet extends Model {
	
	@Id	
	public Long id;
	
	@Constraints.Required
	public String screenName;
	public String text;
	public String profileImageUrl;

  //Fiderクラスは全件検索や条件検索のメソッドが用意されている
  public static Finder<Long, Tweet> find = new Finder<Long, Tweet>(
      Long.class, Tweet.class
  );
}