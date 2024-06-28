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
	public boolean match(SignActionEvent info) {
		return info.isType("attachment");
	}

	@Override
	public void execute(SignActionEvent info) {
	/*
	[train]
	attachment
	<name>
	<item_type> <custom_model_data>
	 */

		if ((info.isTrainSign() || info.isCartSign()) && info.isAction(SignActionType.GROUP_ENTER, SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && info.isPowered()) {
			String name = info.getLine(2);
			String[] newItem = info.getLine(3).split(" ");
			Material material = Material.valueOf(newItem[0].toUpperCase());
			int customModelData = Integer.parseInt(newItem[1]);

			for (MinecartMember<?> member : info.getMembers()) {
				Changer a = new Changer(member, name, material, customModelData);
				a.run();
			}
		}
	}


	@Override
	public boolean build(SignChangeActionEvent event) {
		return SignBuildOptions.create()
				.setName("attachment changer")
				.setDescription("changes named attachments")
				.handle(event.getPlayer());
	}
}

