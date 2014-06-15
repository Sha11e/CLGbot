package org.sha11e.clgtwitterposter;

import org.jibble.pircbot.Colors;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStreamer {
    
    public TwitterStreamer(BotProperties props, IrcPoster bot) {
	this.bot = bot;
	ConfigurationBuilder cbb = new ConfigurationBuilder();
	cbb.setDebugEnabled(Boolean.getBoolean(props.getProperty("twitter.debug")));
	cbb.setOAuthConsumerKey(props.getProperty("twitter.consumerKey"));
	cbb.setOAuthConsumerSecret(props.getProperty("twitter.consumerSecret"));
	cbb.setOAuthAccessToken(props.getProperty("twitter.accessToken"));
	cbb.setOAuthAccessTokenSecret(props.getProperty("twitter.accessTokenSecret"));
	cb = cbb.build();
        TwitterStream twitterStream = new TwitterStreamFactory(cb).getInstance();
        twitterStream.addListener(listener);
        // user() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        twitterStream.user(); 
    }
    
    private static final UserStreamListener listener = new UserStreamListener() {
        @Override
        public void onStatus(Status status) {
            ConsoleLogger.LOG.info(status.getUser().getScreenName() + " just tweeted" + ConsoleLogger.NL);
            String from = status.getUser().getName() + Colors.BOLD + " @"  + status.getUser().getScreenName() + Colors.NORMAL + ": ";
            String message = null;
            
            if (status.isRetweet()) {
        	Status retweet = status.getRetweetedStatus();
        	message = "RT \"" + Colors.BOLD + "@" + retweet.getUser().getScreenName() +  Colors.NORMAL + ": " + retweet.getText() + "\" * " + status.getCreatedAt();
            } else {
        	message = "\"" + status.getText() + "\" · " + status.getCreatedAt();
            }
            if (status.getInReplyToScreenName() != null) {
        	message = message + " · " + "Tweet link: http://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
            }
            message = message.replace("\n", "  ");
            bot.sendMessage(from + message);
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            //showUser(statusDeletionNotice.getUserId());
            ConsoleLogger.LOG.info(statusDeletionNotice.getUserId() + " just deleted a tweet" + ConsoleLogger.NL);
            bot.sendMessage("Got a status deletion notice from " + statusDeletionNotice.getUserId());
        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {
            // System.out.println("Got a direct message deletion notice id:" + directMessageId);
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            ConsoleLogger.LOG.warning("Got a track limitation notice: " + numberOfLimitedStatuses + ConsoleLogger.NL);
            bot.sendMessage("Got a track limitation notice:" + numberOfLimitedStatuses);
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
            ConsoleLogger.LOG.warning("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId + ConsoleLogger.NL);
            bot.sendMessage("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            ConsoleLogger.LOG.warning("Got stall warning:" + warning + ConsoleLogger.NL);
            bot.sendMessage("Got stall warning:" + warning);
        }

        @Override
        public void onFriendList(long[] friendIds) {
            //  System.out.print("onFriendList");
            //for (long friendId : friendIds) {
            //   System.out.print(" " + friendId);
            //}
            //System.out.println();
        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {
            // System.out.println("onFavorite source:@"
              //      + source.getScreenName() + " target:@"
            //    + target.getScreenName() + " @"
            //      + favoritedStatus.getUser().getScreenName() + " - "
            //      + favoritedStatus.getText());
        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
            // System.out.println("onUnFavorite source:@"
              //      + source.getScreenName() + " target:@"
            //    + target.getScreenName() + " @"
            //      + unfavoritedStatus.getUser().getScreenName()
            //      + " - " + unfavoritedStatus.getText());
        }

        @Override
        public void onFollow(User source, User followedUser) {
            bot.sendMessage("The bot just followed " + followedUser.getName() + Colors.BOLD + " @" + followedUser.getScreenName());
            //  System.out.println("onFollow source:@"
              //      + source.getScreenName() + " target:@"
            //    + followedUser.getScreenName());
        }

        @Override
        public void onUnfollow(User source, User followedUser) {
            bot.sendMessage("The bot just unfollowed " + followedUser.getName() + Colors.BOLD + " @" + followedUser.getScreenName());
            // System.out.println("onFollow source:@"
              //      + source.getScreenName() + " target:@"
            //    + followedUser.getScreenName());
        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {
            // System.out.println("onDirectMessage text:"
              //      + directMessage.getText());
        }

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
            //  System.out.println("onUserListMemberAddition added member:@"
              //      + addedMember.getScreenName()
            //    + " listOwner:@" + listOwner.getScreenName()
            //      + " list:" + list.getName());
        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
            // System.out.println("onUserListMemberDeleted deleted member:@"
              //      + deletedMember.getScreenName()
            //    + " listOwner:@" + listOwner.getScreenName()
            //      + " list:" + list.getName());
        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
            // System.out.println("onUserListSubscribed subscriber:@"
            //        + subscriber.getScreenName()
            //       + " listOwner:@" + listOwner.getScreenName()
            //        + " list:" + list.getName());
        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
            // System.out.println("onUserListUnsubscribed subscriber:@"
             //       + subscriber.getScreenName()
            //      + " listOwner:@" + listOwner.getScreenName()
            //      + " list:" + list.getName());
        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {
            //  System.out.println("onUserListCreated  listOwner:@"
             //       + listOwner.getScreenName()
            //      + " list:" + list.getName());
        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {
            //  System.out.println("onUserListUpdated  listOwner:@"
             //       + listOwner.getScreenName()
            //       + " list:" + list.getName());
        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {
            // System.out.println("onUserListDestroyed  listOwner:@"
             //       + listOwner.getScreenName()
            //       + " list:" + list.getName());
        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {
            // System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
        }

        @Override
        public void onBlock(User source, User blockedUser) {
            // System.out.println("onBlock source:@" + source.getScreenName()
            //        + " target:@" + blockedUser.getScreenName());
        }

        @Override
        public void onUnblock(User source, User unblockedUser) {
            //System.out.println("onUnblock source:@" + source.getScreenName()
            //        + " target:@" + unblockedUser.getScreenName());
        }

        @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
            ConsoleLogger.LOG.warning("onException:" + ex.getMessage() + ConsoleLogger.NL);
        }
    };
    
    private static IrcPoster bot;
    private static Configuration cb;
    
}
