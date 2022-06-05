package mikeshafter.mikestcaddons.signactions;

import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import mikeshafter.mikestcaddons.MikesTCAddons;
import mikeshafter.mikestcaddons.util.TrigMinManager;
import org.bukkit.plugin.Plugin;


public class SignActionTrigMin extends SignAction {
  @Override
  public boolean match(SignActionEvent event) {
    return event.isType("trigmin");
  }
  
  @Override
  public void execute(SignActionEvent event) {
    if ((event.isTrainSign() && event.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasGroup())) {
      String variable = event.getLine(2);
      try {
        int min = Integer.parseInt(event.getLine(3));
  
        Variables.get(variable).set(min+" min");
        Variables.get(variable+"T").set(min+" min");
  
        if (event.getGroup().getProperties().hasDestination())
          Variables.get(variable+"D").set(event.getGroup().getProperties().getDestination());
        else
          Variables.get(variable+"D").set("Unknown");
  
        Variables.get(variable+"N").set(event.getGroup().getProperties().getDisplayName());
        Variables.get(variable+"V").set(String.valueOf(Math.min(event.getGroup().getAverageForce(), event.getGroup().getProperties().getSpeedLimit())));
  
        TrigMinManager.addTrigMin(min, variable);
  
      } catch (NumberFormatException e) {
        Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
        plugin.getLogger().warning(String.format("TrigMin sign linking to %s is not set up properly!", variable));
      }
    }
  }
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    return SignBuildOptions.create()
        .setName("trigmin")
        .setDescription("the same as trigger, just updates every 60s")
        .handle(event.getPlayer());
  }
}



