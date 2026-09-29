package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

public class Swapper extends RecurseHelper {

private final String a;
private final String b;

public Swapper (MinecartMember<?> member, String a, String b) {
	super(member);
	this.a = a;
	this.b = b;
}

@Override
protected synchronized void call (ConfigurationNode node) {
	ConfigurationNode animations = node.getNodeIfExists("animations");
	if (animations == null) return;
	if (a.equals(b)) return;

	ConfigurationNode ab = animations.getNodeIfExists(a);
	ConfigurationNode ba = animations.getNodeIfExists(b);
	if (ab == null && ba == null) return;

	// Deep-copy so we can safely reset without aliasing
	ConfigurationNode abcp = ab == null ? null : ab.clone();
	ConfigurationNode bacp = ba == null ? null : ba.clone();

	if (bacp != null) {animations.set(a, bacp);}
	else {animations.remove(a);}

	if (abcp != null) {animations.set(b, abcp);}
	else {animations.remove(b);}
}

}