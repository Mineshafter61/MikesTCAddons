//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import mikeshafter.mikestcaddons.MikesTCAddons;

import java.util.Iterator;
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

	public void run() {
		ConfigurationNode fullConfig = this.member.getProperties().getModel().getConfig();
		Set<ConfigurationNode> attachments = fullConfig.getNode("attachments").getNodes();
		if (attachments != null) {
			ExecutorService es;
			for (Iterator<ConfigurationNode> it = attachments.iterator(); it.hasNext(); es.shutdown()) {
				ConfigurationNode node = it.next();
				es = Executors.newFixedThreadPool(4);
				Future<ConfigurationNode> future = es.submit(new SwapperThread(node, this.a, this.b));

				try {
					ConfigurationNode newAttachments = future.get();
					fullConfig.set("attachments", newAttachments);
				} catch (Exception e) {
					Logger logger = MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger();
					logger.warning(e.getLocalizedMessage());
				}
			}

			this.member.getProperties().getModel().update(fullConfig);
		}
	}

	private record SwapperThread(ConfigurationNode node, String a, String b) implements Callable<ConfigurationNode> {

		public ConfigurationNode call() {
			Set<ConfigurationNode> attachments = this.node.getNode("attachments").getNodes();
			ExecutorService es;
			ConfigurationNode newAttachments;
			if (attachments != null) {
				for (Iterator<ConfigurationNode> it = attachments.iterator(); it.hasNext(); es.shutdown()) {
					ConfigurationNode newNode = it.next();
					es = Executors.newFixedThreadPool(4);
					Future<ConfigurationNode> future = es.submit(new SwapperThread(newNode, this.a, this.b));

					try {
						newAttachments = future.get();
						this.node.set("attachments", newAttachments);
					} catch (Exception e) {
						Logger logger = MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger();
						logger.warning(e.getLocalizedMessage());
					}
				}
			}

			ConfigurationNode animations = this.node.getNode("animations");
			Set<String> animationNames = animations.getKeys();

			for (Iterator<String> it = animationNames.iterator(); it.hasNext(); this.node.set("animations", animations)) {
				String name = it.next();
				newAttachments = animations.getNode(name);
				boolean ea = name.equals(this.a);
				boolean eb = name.equals(this.b);
				animations.set(ea ? this.b : (eb ? this.a : name), newAttachments);
				if (ea | eb) {
					animations.remove(name);
				}
			}

			return this.node;
		}
	}
}
