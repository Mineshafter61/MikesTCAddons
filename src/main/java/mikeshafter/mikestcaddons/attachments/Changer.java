package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class Changer extends RecurseHelper {

private final String name;
private final Material type;
private final int data;

public Changer (MinecartMember<?> member, String name, Material type, int data) {
	super(member);
	this.name = name;
	this.type = type;
	this.data = data;
}

@Override
protected void call (ConfigurationNode node) {
	List<String> names = node.getList("names", String.class);
	if (!names.contains(this.name)) return;

	ItemStack item = node.get("item", ItemStack.class);
	var c = CommonItemStack.copyOf(item);
	c.setCustomModelData(this.data);
	if (this.type != null) c.setType(this.type);

	node.set("item", c.toBukkit());
}
}
