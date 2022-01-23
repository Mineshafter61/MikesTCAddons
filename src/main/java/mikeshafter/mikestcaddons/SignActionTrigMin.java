package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.sl.API.Variable;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;


public class SignActionTrigMin extends SignAction {
  @Override
  public boolean match(SignActionEvent event) {
    return event.isType("trigmin");
  }
  
  @Override
  public void execute(SignActionEvent event) {
    if ((event.isTrainSign() && event.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasGroup()) || (event.isCartSign() && event.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasMember())) {
      String variable = event.getLine(2);
      try {
        int min = Integer.parseInt(event.getLine(3));
        Variable var = Variables.get(variable);
        Variable time = Variables.get(variable+"T");
        Variable destination = Variables.get(variable+"D");
        Variable name = Variables.get(variable+"N");
        Variable speed = Variables.get(variable+"V");
        var.set(min+" min");
        time.set(min+" min");
        destination.set(event.getMember().getProperties().getDestination());
        name.set(event.getGroup().getProperties().getDisplayName());
        speed.set(String.valueOf(Math.min(MathUtil.round(event.getMember().getRealSpeed(), 2), event.getGroup().getProperties().getSpeedLimit())));
        TrigMinManager.addTrigMin(min, variable, event);
      } catch (NumberFormatException e) {
        e.printStackTrace();
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
