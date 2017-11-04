package ultimategdbot.commands;

import java.util.List;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import ultimategdbot.exceptions.CommandFailedException;

public class DMOnlyCommand extends EmbeddedCoreCommand {

	public DMOnlyCommand(CoreCommand cmd) {
		super(cmd);
	}

	@Override
	public void runCommand(MessageReceivedEvent event, List<String> args) throws CommandFailedException {
		if (!event.getChannel().isPrivate())
			throw new CommandFailedException("This command can only be used in DMs.");
		cmd.runCommand(event, args);
	}
}
