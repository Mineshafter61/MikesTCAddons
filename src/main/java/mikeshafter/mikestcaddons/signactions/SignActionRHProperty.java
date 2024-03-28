package mikeshafter.mikestcaddons.signactions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;

public class SignActionRHProperty extends SignAction {
@Override public boolean match (SignActionEvent info) { return info.isType("rhproperty"); }

@Override public void execute (SignActionEvent info) {
	if ((info.isTrainSign() || info.isCartSign())
		&& info.isAction(SignActionType.GROUP_ENTER, SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
		&& info.isPowered()) {

		ConfigurationNode trainConfig = info.isAction(SignActionType.GROUP_ENTER) ? info.getGroup().getProperties().getConfig() : info.getMember().getProperties().getConfig();
		trainConfig.set(info.getLine(2), info.getLine(3));
	}
}

@Override public boolean build (SignChangeActionEvent event) {
	return false;
}
}
