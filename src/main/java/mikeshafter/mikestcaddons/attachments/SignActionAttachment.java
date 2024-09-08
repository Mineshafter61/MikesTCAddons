package mikeshafter.mikestcaddons.attachments;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.Material;

public class SignActionAttachment extends SignAction {

@Override
public boolean match (SignActionEvent info) {
	return info.isType("attachment", "changeitem");
}

@Override
public void execute (SignActionEvent info) {
	if ((info.isTrainSign() || info.isCartSign()) && info.isAction(SignActionType.GROUP_ENTER, SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && info.isPowered()) {
		String name = info.getLine(2);
		String[] newItem = info.getLine(3).split(" ", 2);
		Material material = null;
		int customModelData;
		if (newItem.length == 2) {
			material = Material.valueOf(newItem[0].toUpperCase());
			customModelData = Integer.parseInt(newItem[1]);
		}
		else {customModelData = Integer.parseInt(newItem[0]);}

		for (MinecartMember<?> member : info.getMembers()) {
			Changer a = new Changer(member, name, material, customModelData);
			a.run();
		}
	}
}

@Override
public boolean build (SignChangeActionEvent event) {
	return SignBuildOptions.create().setName("attachment changer").setDescription("changes named attachments").handle(event.getPlayer());
}
}

