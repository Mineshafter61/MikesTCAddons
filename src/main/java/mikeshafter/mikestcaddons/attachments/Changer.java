package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
protected ConfigurationNode call (ConfigurationNode node) {
	List<String> names = node.getList("names", String.class);
	if (!names.contains(this.name)) return node;

	ItemStack item = node.get("item", ItemStack.class);
	ItemMeta meta = item.getItemMeta();
	item.setType(this.type);
	if (meta != null && this.data != 0) {
		meta.setCustomModelData(this.data);
		item.setItemMeta(meta);
	}
	node.set("item", item);
	return node;
}
}
