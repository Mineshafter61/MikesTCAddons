package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import mikeshafter.mikestcaddons.MikesTCAddons;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Swapper implements Runnable {
	private final MinecartMember<?> member;
	private final String a;
	private final String b;

	public Swapper(MinecartMember<?> member, String a, String b) {
		this.member = member;
		this.a = a;
		this.b = b;
	}

	@Override
	public void run() {
		ConfigurationNode fullConfig = this.member.getProperties().getModel().getConfig();
		Set<ConfigurationNode> attachments = fullConfig.getNode("attachments").getNodes();
		if (attachments == null) return;
		for (ConfigurationNode node : attachments) {
			ExecutorService es = Executors.newFixedThreadPool(4);
			Future<ConfigurationNode> future = es.submit(new SwapperThread(node, this.a, this.b));
			try {
				ConfigurationNode newAttachments = future.get();
				fullConfig.set("attachments", newAttachments);
			} catch (Exception e) {
				Logger logger = MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger();
				logger.warning(e.getLocalizedMessage());
			}
			es.shutdown();
		}
		this.member.getProperties().getModel().update(fullConfig);

	}

	private record SwapperThread(ConfigurationNode node, String a, String b)
			implements Callable<ConfigurationNode> {

		@Override
		public ConfigurationNode call() {
			Set<ConfigurationNode> attachments = this.node.getNode("attachments").getNodes();
			if (attachments != null) for (ConfigurationNode newNode : attachments) {
				ExecutorService es = Executors.newFixedThreadPool(4);
				Future<ConfigurationNode> future = es.submit(new SwapperThread(newNode, a, b));
				try {
					ConfigurationNode newAttachments = future.get();
					this.node.set("attachments", newAttachments);
				} catch (Exception e) {
					Logger logger = MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger();
					logger.warning(e.getLocalizedMessage());
				}
				es.shutdown();
			}

			ConfigurationNode animations = this.node.getNode("animations");
			Set<String> animationNames = animations.getKeys();

			for (String name : animationNames) {
				var animation = animations.getNode(name);
				boolean ea = name.equals(a);
				boolean eb = name.equals(b);
				animations.set((ea ? b : eb ? a : name), animation);
				if (ea | eb) animations.remove(name);
				this.node.set("animations", animations);
			}

			return this.node;
		}
	}
}