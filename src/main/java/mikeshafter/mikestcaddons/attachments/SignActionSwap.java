package mikeshafter.mikestcaddons.attachments;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

public class SignActionSwap extends SignAction {

@Override
public boolean match (SignActionEvent info) {
	return info.isType("swap", "swapdoor");
}

@Override
public void execute (SignActionEvent info) {
	if (((info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER)) || (info.isCartSign() && info.isAction(SignActionType.MEMBER_ENTER))) && info.isPowered()) {
		String a = info.getLine(2);
		String b = info.getLine(3);
		for (MinecartMember<?> member : info.getMembers()) {
			Swapper s = new Swapper(member, a, b);
			s.run();
		}
	}
}

@Override
public boolean build (SignChangeActionEvent event) {
	return SignBuildOptions.create().setName("door swapper").setDescription("swaps left and right doors").handle(event.getPlayer());
}
}