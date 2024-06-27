package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import mikeshafter.mikestcaddons.MikesTCAddons;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Changer implements Runnable {
	private final MinecartMember<?> member;
	private final String name;
	private final Material type;
	private final int data;

	public Changer(MinecartMember<?> member, String name, Material type, int data) {
		this.member = member;
		this.name = name;
		this.type = type;
		this.data = data;
	}

	@Override
	public void run() {
		ConfigurationNode fullConfig = member.getProperties().getModel().getConfig();
		Set<ConfigurationNode> attachments = fullConfig.getNode("attachments").getNodes();
		if (attachments == null) return;

		for (ConfigurationNode node : attachments) {
			ExecutorService es = Executors.newFixedThreadPool(4);
			Future<ConfigurationNode> future = es.submit(new ChangerThread(node, name, type, data));
			try {
				ConfigurationNode newAttachments = future.get();
				fullConfig.set("attachments", newAttachments);
			} catch (Exception e) {
				Logger logger = MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger();
				logger.warning(e.getLocalizedMessage());
			}
			es.shutdown();
		}
		member.getProperties().getModel().update(fullConfig);
	}

	private record ChangerThread(ConfigurationNode node, String name, Material type, int data)
			implements Callable<ConfigurationNode> {

		@Override
		public ConfigurationNode call() {
			Set<ConfigurationNode> attachments = this.node.getNode("attachments").getNodes();
			// check for deeper nodes
			if (attachments != null) for (ConfigurationNode newNode : attachments) {
				ExecutorService es = Executors.newFixedThreadPool(4);
				Future<ConfigurationNode> future = es.submit(new ChangerThread(newNode, name, type, data));

				try {
					ConfigurationNode newAttachments = future.get();
					this.node.set("attachments", newAttachments);
				} catch (Exception e) {
					Logger logger = MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger();
					logger.warning(e.getLocalizedMessage());
				}
				es.shutdown();
			}

			// set item
			List<String> names = this.node.getList("names", String.class);
			if (names.contains(this.name)) {
				ItemStack item = this.node.get("item", ItemStack.class);
				var meta = item.getItemMeta();
				item.setType(this.type);
				meta.setCustomModelData(this.data);
				item.setItemMeta(meta);
				this.node.set("item", item);
			}
			return this.node;
		}
	}
}
