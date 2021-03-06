package ultimategdbot.commands;

import java.util.List;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import ultimategdbot.exceptions.CommandFailedException;
import ultimategdbot.net.database.dao.impl.DAOFactory;
import ultimategdbot.net.database.entities.UserSettings;

/**
 * Will run the command only if the user has a Geometry Dash account linked.
 * Will throw a CommandFailedException if it isn't the case.
 * 
 * @author Alex1304
 *
 */
public class GDAccountLinkedRequiredCommand extends EmbeddedCoreCommand {

	public GDAccountLinkedRequiredCommand(CoreCommand cmd) {
		super(cmd);
	}

	@Override
	public void runCommand(MessageReceivedEvent event, List<String> args) throws CommandFailedException {
		UserSettings us = DAOFactory.getUserSettingsDAO().find(event.getAuthor().getLongID());
		if (us == null || !us.isLinkActivated())
			throw new CommandFailedException("You must be linked to a Geometry Dash account to use this command.");
		
		cmd.runCommand(event, args);
	}

}
