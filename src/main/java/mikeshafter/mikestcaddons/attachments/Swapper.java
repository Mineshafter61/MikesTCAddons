package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

public class Swapper extends RecurseHelper {
	private final String a;
	private final String b;

	public Swapper(MinecartMember<?> member, String a, String b) {
		super(member);
		this.a = a;
		this.b = b;
	}

	@Override
	protected ConfigurationNode call(ConfigurationNode node) {
		ConfigurationNode animations = node.getNode("animations");
		Object ab = animations.contains(a) ? animations.getNode(a) : null;
		Object ba = animations.contains(b) ? animations.getNode(b) : null;
		animations.remove(a);
		animations.remove(b);
		if (ab != null) animations.set(b, ab);
		if (ba != null) animations.set(a, ba);

		node.set("animations", animations);
		return node;
	}

}