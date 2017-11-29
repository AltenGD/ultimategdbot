package ultimategdbot.gdevents.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import ultimategdbot.exceptions.RawDataMalformedException;
import ultimategdbot.gdevents.GDEvent;
import ultimategdbot.gdevents.handler.GDEventHandler;
import ultimategdbot.gdevents.levels.NewDailyLevelGDEvent;
import ultimategdbot.gdevents.levels.NewWeeklyDemonGDEvent;
import ultimategdbot.guildsettings.ChannelTimelyLevelsSetting;
import ultimategdbot.guildsettings.RoleTimelyLevelsSetting;
import ultimategdbot.net.database.dao.GuildSettingsDAO;
import ultimategdbot.net.database.dao.UserSettingsDAO;
import ultimategdbot.net.database.entities.GuildSettings;
import ultimategdbot.net.database.entities.UserSettings;
import ultimategdbot.net.geometrydash.GDLevel;
import ultimategdbot.net.geometrydash.GDUser;
import ultimategdbot.net.geometrydash.GDUserFactory;
import ultimategdbot.util.AppTools;
import ultimategdbot.util.GDUtils;

public abstract class TimelyLevelListeners {
	
	/**
	 * Builds a list of listeners and returns it
	 * @return a list of GD event listeners.
	 */
	public static List<GDEventHandler<? extends GDEvent>> getListeners() {
		List<GDEventHandler<? extends GDEvent>> listeners = new ArrayList<>();
		
		listeners.add(new GDEventHandler<>((NewDailyLevelGDEvent event) -> {
			notifySubscribers("There is a new Daily level on Geometry Dash !!!", event.getLevel(),
					newDailyLevelEmbed(event.getLevel()), true);
		}));
		
		listeners.add(new GDEventHandler<>((NewWeeklyDemonGDEvent event) -> {
			notifySubscribers("There is a new Weekly demon on Geometry Dash !!!", event.getLevel(),
					newWeeklyLevelEmbed(event.getLevel()), false);
		}));
		
		return listeners;
	}
	
	private static void notifySubscribers(String message, GDLevel level, EmbedObject levelEmbed, boolean daily) {
		List<GuildSettings> gsList = new GuildSettingsDAO().findAll();

		for (GuildSettings gs : gsList) {
			IGuild guild = gs.getGuild();

			if (guild != null) {
				IChannel channelTimelyLevels = gs.getSetting(ChannelTimelyLevelsSetting.class).getValue();
				IRole roleTimelyLevelsSub = gs.getSetting(RoleTimelyLevelsSetting.class).getValue();
				if (channelTimelyLevels != null) {
					AppTools.sendMessage(channelTimelyLevels,
							(roleTimelyLevelsSub != null ? roleTimelyLevelsSub.mention() + " " : "")
									+ message,
							levelEmbed);
					
					try {
						GDUser creator = GDUserFactory.buildGDUserFromNameOrDiscordTag(level.getCreator());
						if (creator != null) {
							UserSettings us = new UserSettingsDAO().findByGDUserID(creator.getAccountID());
							if (us != null) {
								IUser discordUser = guild.getUserByID(us.getUserID());
								if (discordUser != null) {
									AppTools.sendMessage(channelTimelyLevels, "Congratulations " + discordUser.mention()
											+ "for getting a " + (daily ? "Daily level" : "Weekly demon") + " !");
								}
							}
						}
					} catch (RawDataMalformedException | IOException e) {
					}
				}
			} else {
				System.err.println("[INFO] Guild deleted");
				new GuildSettingsDAO().delete(gs);
			}
		}
	}
	
	private static EmbedObject newDailyLevelEmbed(GDLevel level) {
		String id;
		try {
			id = "" + GDUtils.fetchCurrentTimelyID(true);
		} catch (IOException e) {
			id = "-";
		}
		return GDUtils.buildEmbedForGDLevel("New Daily level! (#" + id + ")", "https://i.imgur.com/enpYuB8.png", level);
	}
	
	private static EmbedObject newWeeklyLevelEmbed(GDLevel level) {
		String id;
		try {
			id = "" + GDUtils.fetchCurrentTimelyID(false);
		} catch (IOException e) {
			id = "-";
		}
		return GDUtils.buildEmbedForGDLevel("New Weekly demon! (#" + id + ")", "https://i.imgur.com/kcsP5SN.png", level);
	}
}